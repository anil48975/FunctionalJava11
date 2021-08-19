package fpcommonmistakes;

import java.util.List;
import java.util.function.Function;

// This example will handle the exception but the Stream will stop if we get an Exception
public class ExceptionExample {
    public static void main(String[] args) {
        System.out.println("Run Successfully  .....");

        List<String> myList = List.of("ab", "cd", "xx");
        new ExceptionExample().handleString(myList);
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
        R apply(T t) throws Exception;
    }

    public <T, R> Function<T, R> wrap (CheckFunc<T, R> func) {
        return t -> {
            try {
                return func.apply(t);
            } catch (Exception e) {
                throw new RuntimeException();
            }
        };
    }
}
