package leetcode.practice;

import java.util.Arrays;

public class MyBinarySearch {
    public static void main(String[] args) {
        int [] input = {2, 7, 89, 37, 96, 49};
      //int [] input = {2, 7, 37, 49, 89, 96,};
        int element = 7;
        MyBinarySearch binarySearch = new MyBinarySearch();
        System.out.println(binarySearch.binarySearch(input, element, 0));

        element = 8;
        System.out.println(binarySearch.binarySearch(input, element, 0));

        element = 89;
        System.out.println(binarySearch.binarySearch(input, element, -1));

        element = 55;
        System.out.println(binarySearch.binarySearch(input, element, 1));
    }

    /*
    if matchIndex is 0, it will search for exact match,
    if it is 1 if no match then next big element
    if it is -1 if no match then next small element
     */
    public int binarySearch(int[] input, int element, int matchIndex) {
        Arrays.sort(input);
        int foundElement = Integer.MIN_VALUE;
        int start = 0;
        int end = input.length - 1;

        while (start <= end) {
            //various conditions on start and end,
            // last one is the correct one.
            //int mid = (end + start) / 2;

            // This calculation will prevent overflow of Integer if values are very high and direct addition will overflow
            // while here we are subtracting first then adding after devision
            int mid = start + (end - start) / 2;
            int currentElement = input[mid];

            if (currentElement == element) {
                foundElement = element;
                break;
            } else if (element > currentElement) {
                if (matchIndex != 0 && mid < input.length-1 && element < input[mid+1]) {
                    foundElement = matchIndex < 0 ? currentElement : input[mid+1];
                    break;
                } else {
                    start = mid + 1;
                }
            } else {
                if (matchIndex != 0 && mid > 0 && element < currentElement && element > input[mid-1]) {
                    foundElement = matchIndex < 0 ? input[mid-1] : currentElement;
                    break;
                } else {
                    end = mid - 1;
                }
            }
        }
        return foundElement;
    }
}
