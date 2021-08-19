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

public class Sample {
  static Map<FutureResponseKey, CompletableFuture<Response>> requestResponseMap = new HashMap<>();

  public static void main(String[] args) throws ExecutionException, InterruptedException {

    ExecutorService pool = Executors.newFixedThreadPool(4);
    IntStream.rangeClosed(0, 10).forEach(count ->
            pool.submit(() -> {
                      sleep(1000);
                        try {
                            System.out.println("Response for count : " + count + " is: "+ startAsyncTask(count).value);
                            //startAsyncTask(count)
                            //.thenAccept(result -> System.out.println("For Count " + count + " result is : " + result.value + " executed by Thread : " + Thread.currentThread()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                      System.out.println(" ----------------------------------------------- Thread finished but async call pending, this from thread: " + Thread.currentThread());
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

  private static Response startAsyncTask(long number) throws Exception{
    String mappingNumber = number % 2 + "";
    FutureResponseKey key = new FutureResponseKey(mappingNumber);

    Optional<CompletableFuture<Response>> existingCompletableFuture = requestResponseMap.entrySet()
                                                                          .stream()
                                                                          //.peek(mapEntry -> System.out.println(mapEntry.getKey().requestTime))
                                                                          .filter(mapEntry -> new Date().getTime() - mapEntry.getKey().requestTime.getTime() < 50000 && mapEntry.getKey().number.equals(key.number))
                                                                          //.peek(mapEntry -> System.out.println("Match found for count " + number + " is key count : " + mapEntry.getKey().number))
                                                                          .map(mapEntry -> mapEntry.getValue())
                                                                          .findFirst();


    if (existingCompletableFuture.isPresent()) {
      return existingCompletableFuture.get().get();
    } else {
        //CompletableFuture<Response> newCompletableFuture = new CompletableFuture().completeAsync(() ->  webServiceCall(number));
        CompletableFuture<Response> newCompletableFuture = new CompletableFuture().supplyAsync(() ->  webServiceCall(number));
        //CompletableFuture<Long> newCompletableFuture = new CompletableFuture<>();
        FutureResponseKey newKey = new FutureResponseKey(mappingNumber);
        newKey.requestTime = new Date();
        requestResponseMap.put(newKey, newCompletableFuture);
        //newCompletableFuture.complete(webServiceCall(number));
        sleep(2000);
        return newCompletableFuture.get();
    }
  }

  static Response webServiceCall(long number) {
    System.out.println("Actual service started ");
    sleep(20000);
    System.out.println("Actual service finished by Thread : " + Thread.currentThread());
    Response response = new Response();
    response.key = "" + number * 2;
    response.value = "" + number * 2;
    return response;
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

class Response{
    String key;
    String value;
}

class FutureResponseKey{
  String number;
  Date requestTime;

  public FutureResponseKey(String mappingNumber) {
    this.number = mappingNumber;
  }
  @Override
  public int hashCode(){
    return Integer.parseInt(number);
  }
  @Override
  public boolean equals(Object obj) {
    return this.number.equals(((FutureResponseKey)obj).number);

  }
}