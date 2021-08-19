package trial04.org.paumard.visitor.model.visitor.model.visitor.model.visitor;

import trial04.org.paumard.visitor.model.visitor.model.visitor.model.visitor.model.Body;
import trial04.org.paumard.visitor.model.visitor.model.visitor.model.visitor.model.Car;
import trial04.org.paumard.visitor.model.visitor.model.visitor.model.visitor.model.Engine;
import trial04.org.paumard.visitor.model.visitor.model.visitor.model.visitor.model.Wheel;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class PlayWithVisitor {

    public static void main(String[] args) {
        Car renault = new Car();

        VisitorInitilizer<String> visitorInitilizer =
                builder -> {
                    builder.register(Car.class, car -> "Visited car" + car);
                    builder.register(Body.class, body -> "Visited body" + body);
                    builder.register(Engine.class, engine -> "Visited engine" + engine);
                    builder.register(Wheel.class, wheel -> "Visited car" + wheel);
                };

        Visitor<String> visitor = Visitor.of(visitorInitilizer);

        String visit = visitor.visit(renault);
        System.out.println("visit = " + visit);

        String visit1 = visitor.visit(renault.getBody());
        System.out.println("visit1 = " + visit1);
    }
}
