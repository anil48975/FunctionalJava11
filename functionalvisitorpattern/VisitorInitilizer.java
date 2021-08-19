package trial04.org.paumard.visitor.model.visitor.model.visitor.model.visitor;

import java.util.function.Consumer;

public interface VisitorInitilizer<R> extends Consumer<VisitorBuilder<R>> {
    default void init(VisitorBuilder<R> builder) {
        this.accept(builder);
    }
}
