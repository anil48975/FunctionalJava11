package systemdesign.service;

import systemdesign.repository.ItemDBRepository;
import systemdesign.repository.ItemFileRepository;
import systemdesign.repository.entity.ItemEntity;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

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
    //Public field declaration__________________________________________________________________________________________________
    private enum ReaderParams {INPUT_FILE, ITEM_FILE_REPO, GLOBAL_QUEUE};
    private enum WriterParams {ITEM_DB_REPO, GLOBAL_QUEUE};

    public void processItems (List<String> files) {
        files.stream()
                .forEach(file -> executorServiceProvider.executorService().submit(() -> readTask.apply(Map.of(ReaderParams.INPUT_FILE, file,
                        ReaderParams.ITEM_FILE_REPO, itemFileRepository,
                        ReaderParams.GLOBAL_QUEUE, globalQueue)).run()));
        writeItems(itemDBRepository);
    }

    public void writeItems(ItemDBRepository itemDBRepository) {
        scheduledThreadPoolExecutor.schedule(writeTask.apply(Map.of(WriterParams.GLOBAL_QUEUE, globalQueue,
                WriterParams.ITEM_DB_REPO, itemDBRepository)), 5, TimeUnit.SECONDS);
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

    private static Function<Map<ReaderParams, Object>, Runnable> readTask = (params) -> {
        return () -> {
            GlobalQueue GLOBAL_QUEUE_INSTANCE = (GlobalQueue) params.get(ReaderParams.GLOBAL_QUEUE);
            String inputFile = (String) params.get(ReaderParams.INPUT_FILE);
            ItemFileRepository itemFileRepository = (ItemFileRepository) params.get(ReaderParams.ITEM_FILE_REPO);

            List<ItemEntity> itemEntities = itemFileRepository.getItems(inputFile);
            GLOBAL_QUEUE_INSTANCE.offerItems(itemEntities);
        };
    };
//_____________________________________________________________________________________________________________________________

    private static final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(Runtime.getRuntime().availableProcessors());

}
