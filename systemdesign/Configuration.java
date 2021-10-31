package systemdesign;

import systemdesign.repository.ItemDBRepository;
import systemdesign.repository.ItemDBRepositoryImpl;
import systemdesign.repository.ItemFileRepository;
import systemdesign.repository.ItemFileRepositoryImpl;
import systemdesign.service.*;

import java.util.HashMap;
import java.util.Map;

public class Configuration {
    private static final Map<Class, InstanceHolder> holderMap = new HashMap<>();

    private Configuration() {
        System.out.println("Configuration class is singleton, this consturctor should be called only once.");
    }

    ItemService itemService() {
        InstanceHolder<ItemService> holder = holderMap.get(ItemService.class);
        if (holder == null)
            holder = new InstanceHolder<ItemService>(new ItemServiceImpl(itemFileRepository(), itemDBRepository(), globalQueue(), executorServiceProvider()));
        addToHolder(ItemService.class, holder);
        return holder.getInstance();
    }

    ItemDBRepository itemDBRepository() {
        InstanceHolder<ItemDBRepository> holder = holderMap.get(ItemDBRepository.class);
        if (holder == null)
            holder = new InstanceHolder<ItemDBRepository>(new ItemDBRepositoryImpl());
        addToHolder(ItemDBRepository.class, holder);
        return holder.getInstance();
    }

    ItemFileRepository itemFileRepository() {
        InstanceHolder<ItemFileRepository> holder = holderMap.get(ItemFileRepository.class);
        if (holder == null)
            holder = new InstanceHolder<ItemFileRepository>(new ItemFileRepositoryImpl());
        addToHolder(ItemFileRepository.class, holder);
        return holder.getInstance();
    }

    GlobalQueue globalQueue() {
        InstanceHolder<GlobalQueue> holder = holderMap.get(GlobalQueue.class);
        if (holder == null)
            holder = new InstanceHolder<GlobalQueue>(GlobalQueueImpl.INSTANCE);
        addToHolder(GlobalQueue.class, holder);
        return holder.getInstance();
    }

    ExecutorServiceProvider executorServiceProvider() {
        InstanceHolder<ExecutorServiceProvider> holder = holderMap.get(ExecutorServiceProvider.class);
        if (holder == null)
            holder = new InstanceHolder<ExecutorServiceProvider>(ExecutorServiceProviderImpl.getInstance());
        addToHolder(ExecutorServiceProvider.class, holder);
        return holder.getInstance();
    }

    public static Configuration getInstance() {
        return ConfigurationHolder.getInstance();
    }
    private <T> void addToHolder(Class<T> classOfHolder, InstanceHolder<T> holder) {
        holderMap.putIfAbsent(classOfHolder, holder);
    }

    private static class ConfigurationHolder {
        private static Configuration configuration;
        private static Configuration getInstance() {
            configuration = new Configuration();
            return configuration;
        }
    }
}
