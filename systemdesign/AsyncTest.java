package leetcode.practice;

import java.util.Date;
import java.util.concurrent.*;
import java.util.function.Function;

public class AsyncTest {
    public static void main(String[] args) {
        ScheduledExecutorService scheduledExecutorService =
                Executors.newScheduledThreadPool(5);
        System.out.println("inside main method started before completable future async call");
        CompletableFuture<Integer> cf = CompletableFuture.supplyAsync(() -> {
            System.out.println("Completable Future started inside thread: " + Thread.currentThread().getName() + " at " + new Date().toString());
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Completable Future completed inside thread: " + Thread.currentThread().getName() + " at " + new Date().toString());
            return 2;
        });

        System.out.println("inside main method before task scheduling " + " at " + new Date().toString());
        // 0 second delay to just make it asynchronous, keeping out of main thread
        // and executing in some other thread, independent of main thread
        scheduledExecutorService.schedule(task.apply(cf), 0, TimeUnit.SECONDS);
        //Running same CompletableFuture twice assuming it is fetched from a cache
        scheduledExecutorService.schedule(task.apply(cf), 0, TimeUnit.SECONDS);
        System.out.println("inside main method after task scheduling " + " at " + new Date().toString());
    }

    private static Function<CompletableFuture<Integer>, Runnable> task = (cf) -> {
        return () -> {
            System.out.println("Task started in thread: " + Thread.currentThread().getName() + " at " + new Date().toString());
            try {
                Thread.sleep(2000);
                System.out.println("Task value is: " + cf.get() + ", executed inside Thread: "+ Thread.currentThread().getName() +
                        " at " + new Date().toString());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        };
    };
}
