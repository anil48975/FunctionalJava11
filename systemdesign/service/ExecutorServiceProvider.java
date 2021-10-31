package systemdesign.service;

import java.util.concurrent.ExecutorService;

public interface ExecutorServiceProvider {
    ExecutorService executorService();
}
