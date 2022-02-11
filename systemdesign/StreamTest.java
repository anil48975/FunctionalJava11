package leetcode.practice;

import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class StreamTest {

    public static void main(String[] args) {

        Set<String> roles1 = Set.of("role1", "role2");
        Set<String> roles2 = Set.of("role3", "role4");
        Map<String, Set<String>> roles = Map.of("role1", roles1, "role2", roles2);
        //Map<String, Set<String>> emptyRoles = roles;
        Map<String, Set<String>> emptyRoles = null;

        //Handling null collection
        Stream.ofNullable(emptyRoles)
                .flatMap(rolesStream -> rolesStream.entrySet().stream())
                .flatMap(entry -> entry.getValue().stream())
                .forEach(System.out::println);

        roles.entrySet().stream()
                        .flatMap(entry -> entry.getValue().stream())
                        .forEach(System.out::println);
    }
}
