package systemdesign.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutorServiceProviderImpl implements ExecutorServiceProvider {
    private static final ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private ExecutorServiceProviderImpl(){}

    public static ExecutorServiceProvider getInstance() {
        return ExecutorServiceProviderHolder.getInstance();
    }
    public ExecutorService executorService() {
        return executorService;
    }

    private static class ExecutorServiceProviderHolder {
        private static ExecutorServiceProvider executorServiceProvider;
        private static ExecutorServiceProvider getInstance() {
            executorServiceProvider = new ExecutorServiceProviderImpl();
            return executorServiceProvider;
        }
    }
}
