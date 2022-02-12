package leetcode.practice.dsandalgo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public  class MergeSortWithExceptionFlow<T extends Integer> {
    // private final int numberOfProcessors = Runtime.getRuntime().availableProcessors();
    private final int numberOfProcessors = 4;
    public T[] sort(T[] input, Comparator comparator) {
        List<InputIndexPair> inputIndexPairs = getSortingPairsIndex(input.length, numberOfProcessors);
        List<CompletableFuture<T[]>> futures = new ArrayList<>();
        for (int i = 0; i < inputIndexPairs.size(); i++) {
            InputIndexPair inputIndexPair = inputIndexPairs.get(i);
            futures.add(CompletableFuture.supplyAsync(() ->
                    internalSort(Arrays.copyOfRange(input, inputIndexPair.startIndex, inputIndexPair.endIndex + 1), comparator)));
        }

        CompletableFuture<T[]> seed = new CompletableFuture<>();
        seed.complete(Arrays.copyOf(input, 0));
        for (int i = 0; i < futures.size(); i++) {
            CompletableFuture<T[]> future = futures.get(i);

            CompletableFuture<T[]> newSeed = seed.thenCombine(future, (result1, result2) -> {
                 T[] mergedResult = Arrays.copyOf(input, result1.length + result2.length);
                 Arrays.fill(mergedResult, null);
                 try {
                     for (int i1 = 0, j = 0, k = 0; k < mergedResult.length; k++) {
                         if (result1.length == 0) {
                             mergedResult[k] = result2[j];
                             j++;
                         } else if (result2.length == 0) {
                             mergedResult[k] = result1[i1];
                             i1++;
                         } else if (i1 < result1.length && j < result2.length && result1[i1].compareTo(result2[j]) < 0) {
                             mergedResult[k] = result1[i1];
                             i1++;
                         } else if (i1 < result1.length && j < result2.length && result1[i1].compareTo(result2[j]) > 0) {
                             mergedResult[k] = result2[j];
                             j++;
                         } else if (i1 < result1.length && j >= result2.length) {
                             mergedResult[k] = result1[i1];
                             i1++;
                         } else if (j < result2.length && i1 >= result1.length) {
                             mergedResult[k] = result2[j];
                             j++;
                         }
                     }
                 } catch (Exception e) {
                     e.printStackTrace();
                 }
                 return mergedResult;
            }).exceptionally(e -> {
                System.out.println(" Exception occured while merging: " + e.getMessage());
                return Arrays.copyOfRange(input, 0, 1);
            });

            if (newSeed.isDone() && !newSeed.isCompletedExceptionally()) {
                seed = newSeed;
            }
        }

        try {
            T[] sortedArray = seed.get();
            System.out.println(" ----------------------------------------- input array print : size is : " + input.length);
            print(input);
            System.out.println(" ----------------------------------------- sorted array print : size is : " + sortedArray.length);
            print(sortedArray);

            System.out.println("smallest element : " + seed.thenCompose(data -> CompletableFuture.supplyAsync(() -> {
                return data[0];
            })).get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    private T[] internalSort(T[] input, Comparator comparator) {
        for (int i = 0; i < input.length; i++) {
            if (input[i] instanceof Integer && (Integer)(input[i]) > 100) {
                throw new RuntimeException("exception occured for input size " + input.length + " first input element : " + input[0] +
                        " last input element " + input[input.length - 1]);
            }
            for (int j = i; j < input.length; j++) {
                if (comparator.compare(input[i], input[j]) > 0) {
                    T temp = input[i];
                    input[i] = input[j];
                    input[j] = temp;
                }
            }
        }

        return input;
    }

    private List<InputIndexPair> getSortingPairsIndex(int inputlength, int executorThreads) {
        int internalSortSize = Math.abs(inputlength/executorThreads);
        List<InputIndexPair> inputIndexPairs = new ArrayList<>();
        int runningndex = -1;
        while (runningndex < inputlength -1) {
            InputIndexPair pair = new InputIndexPair();
            pair.startIndex = runningndex + 1;
            runningndex = runningndex + 1;

            pair.endIndex = runningndex + internalSortSize >= inputlength ? inputlength - 1 : runningndex + internalSortSize;
            runningndex = runningndex + internalSortSize;

            inputIndexPairs.add(pair);
        }

        return inputIndexPairs;
    }

    private void print(T[] elements) {
        for (T t : elements) {
            System.out.println(t.intValue());
        }
    }
}

class InputIndexPair {
    int startIndex;
    int endIndex;

    @Override
    public String toString() {
        return "Start inde : " + startIndex + " endIndex: " + endIndex;
    }
}
