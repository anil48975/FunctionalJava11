package systemdesign.service;

import systemdesign.repository.ItemDBRepository;
import systemdesign.repository.ItemFileRepository;
import systemdesign.repository.entity.ItemEntity;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * This is a service class which reads items from files and write them to DB.
 *
 * Reading and Writing are implemented using Producer-Consumer pattern, and it uses
 * a GlobalQueue for read and write operations.
 *
 * Possible improvements :
 *  Partitioning using hashing the key and writing to multiple queue based on the key hash(mod for now)
 *  replication - for distributed system and fault tolerance if one queue becomes unavailable
 *  fault tolerance
 * transactions [a group of items sent as a whole to either process or reject as transaction]
 * CAP theorem
 * If Queue is like a log database, just append [for example like KAFKA then performance will be high
 * as write operation will be very quick.]
 */
public class ItemServiceImpl implements ItemService{
    ItemFileRepository itemFileRepository;
    ItemDBRepository itemDBRepository;
    GlobalQueue globalQueue;
    ExecutorServiceProvider executorServiceProvider;

    public ItemServiceImpl(ItemFileRepository itemFileRepository, ItemDBRepository itemDBRepository, GlobalQueue globalQueue, ExecutorServiceProvider executorServiceProvider) {
        System.out.println("ItemServiceImpl class is singleton from Configuration, this consturctor should be called only once.");

        this.itemFileRepository = itemFileRepository;
        this.itemDBRepository = itemDBRepository;
        this.globalQueue = globalQueue;
        this.executorServiceProvider = executorServiceProvider;
    }

    // private fields above due to forward reference issue
    private enum ReaderParams {INPUT_FILE, ITEM_FILE_REPO, GLOBAL_QUEUE, WAIT_BEFORE_START};
    private enum WriterParams {ITEM_DB_REPO, GLOBAL_QUEUE};

//Public fields and methods__________________________________________________________________________________________________

    public void processItems (List<String> files, boolean applyFileProcessingDelayPattern) {
        files.stream()
                .forEach(file -> executorServiceProvider.executorService().submit(() -> readTask.apply(Map.of(ReaderParams.INPUT_FILE, file,
                        ReaderParams.ITEM_FILE_REPO, itemFileRepository,
                        ReaderParams.GLOBAL_QUEUE, globalQueue,
                        ReaderParams.WAIT_BEFORE_START, getWaitTime.apply(file, applyFileProcessingDelayPattern))).run()));
        writeItems(itemDBRepository);
    }

    public void writeItems(ItemDBRepository itemDBRepository) {
        scheduledThreadPoolExecutor.scheduleAtFixedRate(writeTask.apply(Map.of(WriterParams.GLOBAL_QUEUE, globalQueue,
                WriterParams.ITEM_DB_REPO, itemDBRepository)), WRITE_TASK_SCHEDULE_IN_SECONDS, WRITE_TASK_SCHEDULE_IN_SECONDS, TimeUnit.SECONDS);
    }


// Private Fields declarations above due to forward reference issue___________________________________________________________________________________________________________________
    private static Function<Map<WriterParams, Object>, Runnable> writeTask = (params) -> {
        return () -> {
            System.out.println("Write Task running as per schedule at: " + System.currentTimeMillis()/1000);
            System.out.println("Write Task running by Thread " + Thread.currentThread().getName());
            GlobalQueue globalQueue = (GlobalQueue) params.get(WriterParams.GLOBAL_QUEUE);
            ItemDBRepository itemDBRepository = (ItemDBRepository) params.get(WriterParams.ITEM_DB_REPO);
            List<ItemEntity> items = globalQueue.pollItems();
            items.stream()
                    .forEach(item -> itemDBRepository.save(item));
        };
    };

    private static Function<Map<ReaderParams, Object>, Runnable> readTask = (params) -> {
        return () -> {
            System.out.println("Read Task running by Thread " + Thread.currentThread().getName());
            GlobalQueue GLOBAL_QUEUE_INSTANCE = (GlobalQueue) params.get(ReaderParams.GLOBAL_QUEUE);
            String inputFile = (String) params.get(ReaderParams.INPUT_FILE);
            ItemFileRepository itemFileRepository = (ItemFileRepository) params.get(ReaderParams.ITEM_FILE_REPO);

            List<ItemEntity> itemEntities = itemFileRepository.getItems(inputFile);
            try {
                System.out.println("Reading file " + inputFile + "delay is : " + params.get(ReaderParams.WAIT_BEFORE_START));
                Thread.sleep((Long)params.get(ReaderParams.WAIT_BEFORE_START));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Reading file " + inputFile + " started after " + params.get(ReaderParams.WAIT_BEFORE_START) + " seconds delay.");
            GLOBAL_QUEUE_INSTANCE.offerItems(itemEntities);
        };
    };
//_____________________________________________________________________________________________________________________________

    private static final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(Runtime.getRuntime().availableProcessors());
    private static final int WRITE_TASK_SCHEDULE_IN_SECONDS = 2;
    private static final Function<String, Boolean> isNumber = input -> {
        try {
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    };

    private static final BiFunction<String, Boolean, Long> getWaitTime =(fileName, applyFileProcessingDelayPattern) -> {
        if (applyFileProcessingDelayPattern) {
            char[] fileChars = fileName.toCharArray();
            StringBuilder numbersInFile = new StringBuilder();
            for (int count = 0; count < fileChars.length; count++) {
                if (isNumber.apply("" + fileChars[count])) {
                    numbersInFile.append("" + fileChars[count]);
                }
            }
            return Long.parseLong(numbersInFile.length() > 0 ? numbersInFile.toString() : "5") * 1000;
        } else {
            return Long.valueOf(0);
        }
    };
}
