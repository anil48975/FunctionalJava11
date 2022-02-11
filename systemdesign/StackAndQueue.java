package leetcode.practice;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Stack;

public class StackAndQueue {
    public static void main(String[] args) {
        Deque<Integer> stack = new ArrayDeque<Integer>();
        stack.addFirst(1);
        stack.addFirst(2);
        stack.addFirst(5);
        stack.addFirst(7);
        while (!stack.isEmpty()) {
            System.out.println(stack.poll());
        }

        Deque<Integer> queue = new ArrayDeque<Integer>();
        queue.addLast(1);
        queue.addLast(2);
        queue.addLast(5);
        queue.addLast(7);
        while (!queue.isEmpty()) {
            System.out.println(queue.poll());
        }

    }
}
