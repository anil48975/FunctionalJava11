package systemdesign;

import systemdesign.repository.entity.ItemEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Function;

// This class requires in java 11 runtime
//Possible improvements :
// Partitioning using hashing the key and writing to multiple queue based on the key hash(mod for now)
// replication - for distributed system and fault tolerance if one queue becomes unavailable
// fault tolerance
// transactions [a group of items sent as a whole to either process or reject as transaction]
// CAP theorem

public class FileProcessorSelfContainedForDemo {

    public static void main(String[] args) {
        ItemService itemService = new ItemService();
        ItemFileRepository itemFileRepository = new ItemFileRepository();
        ItemDBRepository itemDBRepository = new ItemDBRepository();
        itemService.processItems(itemFileRepository, List.of("file1", "file2", "file3", "file4"), itemDBRepository);
        itemService.processItems(itemFileRepository, List.of("file1", "file2", "file3", "file4"), itemDBRepository);
    }

    // Private Fields declarations above due to forward reference issue___________________________________________________________________________________________________________________
    private static Function<Map<WriterParams, Object>, Runnable> writeTask = (params) -> {
        return () -> {
            GlobalQueue globalQueue = (GlobalQueue) params.get(WriterParams.GLOBAL_QUEUE);
            ItemDBRepository itemDBRepository = (ItemDBRepository) params.get(WriterParams.ITEM_DB_REPO);
            List<ItemEntity> items = globalQueue.pollItems();
            items.stream()
                    .forEach(item -> itemDBRepository.save(item));
        };
    };

    // Public Fields declarations___________________________________________________________________________________________________________________
    public static Function<Map<ReaderParams, Object>, Runnable> readTask = (params) -> {
        return () -> {
            GlobalQueue GLOBAL_QUEUE_INSTANCE = (GlobalQueue) params.get(ReaderParams.GLOBAL_QUEUE);
            String inputFile = (String) params.get(ReaderParams.INPUT_FILE);
            ItemFileRepository itemFileRepository = (ItemFileRepository) params.get(ReaderParams.ITEM_FILE_REPO);

            List<ItemEntity> itemEntities = itemFileRepository.getItems(inputFile);
            GLOBAL_QUEUE_INSTANCE.offerItems(itemEntities);
        };
    };
//_____________________________________________________________________________________________________________________________

    public static class ItemService {
        // @Autowired ItemFileRepository or it can be passed to function
        // @Autowired ItemDBRepository or it can be passed to function

        public void processItems (ItemFileRepository itemFileRepository, List<String> files, ItemDBRepository itemDBRepository) {
            files.stream()
                    .forEach(file -> executorService.submit(() -> readTask.apply(Map.of(ReaderParams.INPUT_FILE, file,
                            ReaderParams.ITEM_FILE_REPO, itemFileRepository,
                            ReaderParams.GLOBAL_QUEUE, GlobalQueue.INSTANCE)).run()));
            writeItems(itemDBRepository);
        }

        public void writeItems(ItemDBRepository itemDBRepository) {
            scheduledThreadPoolExecutor.schedule(writeTask.apply(Map.of(WriterParams.GLOBAL_QUEUE, GlobalQueue.INSTANCE,
                    WriterParams.ITEM_DB_REPO, itemDBRepository)), 5, TimeUnit.SECONDS);
        }
    }

    //private class declaration___________________________________________________________________________________________________________________________________________
    private enum ReaderParams {INPUT_FILE, ITEM_FILE_REPO, GLOBAL_QUEUE};
    private enum WriterParams {ITEM_DB_REPO, GLOBAL_QUEUE};

    private static enum GlobalQueue {
        INSTANCE;
        private static final PriorityBlockingQueue<FileProcessorSelfContainedForDemo.ItemEntity> GLOBAL_QUEUE = new PriorityBlockingQueue<FileProcessorSelfContainedForDemo.ItemEntity>(GLOBAL_QUEUE_CAPACITY, (e1, e2) -> e1.seq - e2.seq);

        synchronized void offerItems(List<FileProcessorSelfContainedForDemo.ItemEntity> items) {
            while (GLOBAL_QUEUE.remainingCapacity() < items.size()) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            try {
                items.stream()
                        .forEach(item -> {
                            System.out.println("Item added to queue: " + item.toString());
                            GLOBAL_QUEUE.offer(item);
                        });
            } finally {
                notifyAll();
            }
        }

        synchronized List<FileProcessorSelfContainedForDemo.ItemEntity> pollItems() {
            List<FileProcessorSelfContainedForDemo.ItemEntity> items = new ArrayList<>();
            while (GLOBAL_QUEUE.isEmpty()) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            try {
                while (!GLOBAL_QUEUE.isEmpty()) {
                    FileProcessorSelfContainedForDemo.ItemEntity item = GLOBAL_QUEUE.poll();
                    System.out.println("Item taken from the queue: " + item.toString());
                    items.add(item);
                }
            } finally {
                notifyAll();
                return items;
            }
        }
    }
    // Datasource and private classes____________________________________________________________________________________________________________________________________
    private static class ItemDBRepository {
        void save(FileProcessorSelfContainedForDemo.ItemEntity entity) {
            System.out.println("Item saved to DB: " + entity.toString());
        }
    }

    private static class ItemEntity {
        //@Version
        int seq;
        String uuid;
        private String itemCode;
        private String itemName;
        private double price;
        private int quantity;
        ItemEntity (int seq, String itemCode, String itemName, int price, int quantity) {
            this.seq = seq;
            this.itemCode = itemCode;
            this.itemName = itemName;
            this.price = price;
            this.quantity = quantity;
        }
        //hashcode
        //equals
        @Override
        public String toString() {
            return "Seq: " + seq + " ItemCode: " + itemCode;
        }
    }

    private static class ItemFileRepository {
        List<FileProcessorSelfContainedForDemo.ItemEntity> getItems(String filesName) {
            List<FileProcessorSelfContainedForDemo.ItemEntity> file1 = List.of(new FileProcessorSelfContainedForDemo.ItemEntity(1, "1234", "item1", 543, 56),
                    new FileProcessorSelfContainedForDemo.ItemEntity(2, "1234", "item1", 543, 56),
                    new FileProcessorSelfContainedForDemo.ItemEntity(3, "1234", "item1", 543, 56),
                    new FileProcessorSelfContainedForDemo.ItemEntity(4, "1234", "item1", 543, 56));
            List<FileProcessorSelfContainedForDemo.ItemEntity> file2 = List.of(new FileProcessorSelfContainedForDemo.ItemEntity(5, "1234", "item1", 543, 56),
                    new FileProcessorSelfContainedForDemo.ItemEntity(6, "1234", "item1", 543, 56),
                    new FileProcessorSelfContainedForDemo.ItemEntity(7, "1234", "item1", 543, 56),
                    new FileProcessorSelfContainedForDemo.ItemEntity(8, "1234", "item1", 543, 56));
            List<FileProcessorSelfContainedForDemo.ItemEntity> file3 = List.of(new FileProcessorSelfContainedForDemo.ItemEntity(9, "1234", "item1", 543, 56),
                    new FileProcessorSelfContainedForDemo.ItemEntity(10, "1234", "item1", 543, 56),
                    new FileProcessorSelfContainedForDemo.ItemEntity(11, "1234", "item1", 543, 56),
                    new FileProcessorSelfContainedForDemo.ItemEntity(12, "1234", "item1", 543, 56));
            List<FileProcessorSelfContainedForDemo.ItemEntity> file4 = List.of(new FileProcessorSelfContainedForDemo.ItemEntity(13, "1234", "item1", 543, 56),
                    new FileProcessorSelfContainedForDemo.ItemEntity(14, "1234", "item1", 543, 56),
                    new FileProcessorSelfContainedForDemo.ItemEntity(15, "1234", "item1", 543, 56),
                    new FileProcessorSelfContainedForDemo.ItemEntity(16, "1234", "item1", 543, 56));
            Map<String, List<FileProcessorSelfContainedForDemo.ItemEntity>> itemRepo = Map.of("file1" , file1,
                    "file2", file2,
                    "file3", file3,
                    "file4", file4);
            return itemRepo.get(filesName);
        }
    }

    private static final ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private static final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(Runtime.getRuntime().availableProcessors());
    private static final int GLOBAL_QUEUE_CAPACITY = 20;
}
