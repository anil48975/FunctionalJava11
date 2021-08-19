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

public class Sample1 {
    public static int compute() {
        System.out.println("Compute: " + Thread.currentThread());
        sleep(10000);
        System.out.println("Compute finished");
        return 2;
    }

    public static CompletableFuture<Integer> create() {
        return CompletableFuture.supplyAsync(() -> compute());
    }

    public static void main(String[] args) {
        System.out.println("Main : " + Thread.currentThread());
        create().thenAccept(data -> System.out.println(data));
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

