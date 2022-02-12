package leetcode.practice.dsandalgo;

import dsandalgo.MergeSortWithExceptionFlow;

import java.util.Comparator;

public class MergeSortClient {
    public static void main(String[] args) {
       Integer[] input = {5, 3, 7, 1, 2, 8, 14, 25, 20, 18, 57, 89, 789, 215};
       dsandalgo.MergeSortWithExceptionFlow<Integer> mergeSortWithExceptionFlow = new MergeSortWithExceptionFlow<>();
       mergeSortWithExceptionFlow.sort(input, Comparator.naturalOrder());

    }
}
