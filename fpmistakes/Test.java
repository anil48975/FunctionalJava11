package fpcommonmistakes;

import java.util.stream.IntStream;

public class Test {
    public static void main(String[] args) {
        System.out.println("Run Successfully .....");
        //incorrect ordering
        IntStream.iterate(0, i -> (i+1)%2) // 0 1 0 1 0 1 0 1
                 .distinct() //0 1
                 .limit(10) //never reaches 10 elements, so program never terminates
                 .forEach(System.out::println);

        //correct ordering
        IntStream.iterate(0, i -> (i+1)%2) // 0 1 0 1 0 1 0 1
                .limit(10)
                .distinct()
                .forEach(System.out::println);
    }
}
