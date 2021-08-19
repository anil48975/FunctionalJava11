package trial04.org.paumard.visitor.model.visitor.model.visitor.model.visitor;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public interface Visitor<R> {

    R  visit(Object o);

    static <R> Visitor<R> of(VisitorInitilizer<R> visitorInitilizer) {
        Map<Class<?>, Function<Object, R>> registry = new HashMap<>();

 //        VisitorBuilder<R> visitorBuilder =
//                (type, function) -> registry.put(type, function);
//        visitorInitilizer.init(visitorBuilder);

        visitorInitilizer.init(registry::put);

        return o -> registry.get(o.getClass()).apply(o);
    }
}
