package fpcommonmistakes;

import java.util.function.Function;

public class BearHandler {
    public String handleBear(Bear bear, Function<Bear, String> function) {
        System.out.println("Handling bear " + bear.getName());
        return function.apply(bear);
    }
}
