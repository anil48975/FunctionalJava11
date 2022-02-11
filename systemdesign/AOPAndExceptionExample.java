package leetcode.practice.eitherpatternandaop;

import java.util.List;
import java.util.function.Function;

// This example will handle the exception but the Stream will stop if we get an Exception
public class AOPAndExceptionExample {
    public static void main(String[] args) {
        System.out.println("Run Successfully  .....");

        List<String> myList = List.of("ab", "cd", "xx");
        new AOPAndExceptionExample().handleString(myList);
    }

    public void handleString(List<String> myStrings) {
        myStrings.stream()
                .map(wrap(myString -> doSomeThing(myString)))
                .forEach(System.out::println);
    }
    public String doSomeThing(String myString) throws Exception {
        if("xx".equals(myString))
            throw new RuntimeException();
        return myString;
    }

    @FunctionalInterface
    public interface CheckFunc<T, R> {
        R execute(T t) throws Exception;
    }

    public <T, R> Function<T, R> wrap (CheckFunc<T, R> func) {
        return t -> {
            try {
                return func.execute(t);
            } catch (Exception e) {
                throw new RuntimeException();
            }
        };
    }
}
