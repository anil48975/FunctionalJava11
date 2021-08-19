package com.agiledeveloper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class SampleCopy {
  static Map<FutureResponseKey, CompletableFuture<Long>> requestResponseMap = new HashMap<>();

  public static void main(String[] args) throws ExecutionException, InterruptedException {

    ExecutorService pool = Executors.newFixedThreadPool(100);
    IntStream.rangeClosed(0, 1000).forEach(count ->
            pool.submit(() -> {
                      sleep(1000);
                        webServiceCall(count);
                        System.out.println(" ----------------------------------------------- Thread finished" + " for count" + count + " but async call pending " + " this from thread: "
                                + Thread.currentThread());
            }
            ));

    IntStream.rangeClosed(0, 10).forEach(count ->
            pool.submit(() -> {
                      sleep(1000);
                      System.out.println("meanwhile other tasks are running task count is: " + count);

            }
            ));
    //Don't shutdown the pool, as tasks run asynchronously even after main thread finishes
    //pool.shutdown();

  }

  static long webServiceCall(long number) {
    System.out.println("Actual service started ");
    sleep(20000);
    System.out.println("Actual service finished by Thread : " + Thread.currentThread());
    return number * 2;
  }

  private static boolean sleep(int ms) {
    try {
      Thread.sleep(ms);
      return true;
    } catch (InterruptedException e) {
      return false;
    }
  }
}