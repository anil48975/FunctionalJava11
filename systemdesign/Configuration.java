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
        holder = holder == null ? new InstanceHolder<ItemService>(new ItemServiceImpl(itemFileRepository(), itemDBRepository(), globalQueue(), executorServiceProvider()))
                : null;
        addToHolder(ItemService.class, holder);
        holder = holderMap.get(ItemService.class);

        return holder.getInstance();
    }

    ItemDBRepository itemDBRepository() {
        InstanceHolder<ItemDBRepository> holder = holderMap.get(ItemDBRepository.class);
        holder = holder == null ? new InstanceHolder<ItemDBRepository>(new ItemDBRepositoryImpl()) : null;
        addToHolder(ItemDBRepository.class, holder);
        holder = holderMap.get(ItemDBRepository.class);
        return holder.getInstance();
    }

    ItemFileRepository itemFileRepository() {
        InstanceHolder<ItemFileRepository> holder = holderMap.get(ItemFileRepository.class);
        holder = holder == null ? new InstanceHolder<ItemFileRepository>(new ItemFileRepositoryImpl()) : null;
        addToHolder(ItemFileRepository.class, holder);
        holder = holderMap.get(ItemFileRepository.class);
        return holder.getInstance();
    }

    GlobalQueue globalQueue() {
        InstanceHolder<GlobalQueue> holder = holderMap.get(GlobalQueue.class);
        holder = holder == null ? new InstanceHolder<GlobalQueue>(GlobalQueueImpl.INSTANCE) : null;
        addToHolder(GlobalQueue.class, holder);
        holder = holderMap.get(GlobalQueue.class);
        return holder.getInstance();
    }

    ExecutorServiceProvider executorServiceProvider() {
        InstanceHolder<ExecutorServiceProvider> holder = holderMap.get(ExecutorServiceProvider.class);
        holder = holder == null ? new InstanceHolder<ExecutorServiceProvider>(ExecutorServiceProviderImpl.getInstance()) : null;
        addToHolder(ExecutorServiceProvider.class, holder);
        holder = holderMap.get(ExecutorServiceProvider.class);
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
