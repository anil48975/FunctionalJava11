package polishingthediamond;

import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.*;

public class StreamGrouping {
    public static void main(String[] args) {
        Expense expense1 = new Expense(500, 2016, Set.of("FOOD", "ENTERTAINMENT"));
        Expense expense2 = new Expense(1500, 2016, Set.of("UTILITY"));
        Expense expense3 = new Expense(700, 2015, Set.of("TRAVEL", "ENTERTAINMENT"));
        List<Expense> purchases = List.of(expense1, expense2, expense3);

//        //grouping by
//        System.out.println(purchases.stream().collect(groupingBy(expense -> expense.year)));
//
//        //How much money spent on 2015 and 2016
//        System.out.println(purchases.stream()
//                .collect(groupingBy(Expense::getYear, summingLong(expense -> expense.amount))));

 // Find the purchases that were bigger than 1000 pounds
//        purchases.stream()
//                .filter(expense -> expense.amount > 1000)
//                .collect(groupingBy(expense -> expense.year))
//                .entrySet()
//                .forEach(entry ->
//                {
//                    //System.out.println(entry.getKey() + ": " + entry.getValue().stream().collect(toList()));
//                    System.out.println(entry.getKey() + " -> " + entry.getValue().stream().map(expense -> expense.toString()).collect(joining("," , "[", "]")));
//                });
// With above logic 2015 year itself missing[2015 should appear with no expense above 1000 pound] as we filtered first

// So group by first then filter
//        purchases.stream()
//                .collect(groupingBy(expense -> expense.year, filtering(expense -> expense.amount > 1000, toList())))
//                .entrySet()
//                .forEach(entry ->
//                {
//                    //System.out.println(entry.getKey() + ": " + entry.getValue().stream().collect(toList()));
//                    System.out.println(entry.getKey() + " -> " + entry.getValue().stream().map(expense -> expense.toString()).collect(joining("," , "[", "]")));
//                });

        //Getting all the tags:
//        Set<String> purchaseTags = purchases.stream().flatMap(expense -> expense.tags.stream())
//                          .collect(toSet());

        //grouping by year with tags
//        purchases.stream()
//                 .collect(groupingBy(expense -> expense.year, mapping(expense -> expense.tags, toSet())))
//                 .entrySet()
//                 .forEach(entry ->
//                   System.out.println(entry.getKey() + " -> " +
//                           entry.getValue()
//                                .stream()
//                                .map(expense -> expense.toString())
//                                .collect(joining("," , "[", "]")))
//                 );

        //getting tags flat not as List or Set, use FlatMap
        // but just replacing map to flatMap in above statement will give error
        // because it[flatMap] expects a Stream not a collection
        purchases.stream()
                .collect(groupingBy(expense -> expense.year, flatMapping(expense -> expense.tags.stream(), toSet())))
                .entrySet()
                .forEach(entry ->
                        System.out.println(entry.getKey() + " -> " +
                                entry.getValue()
                                        .stream()
                                        .map(expense -> expense.toString())
                                        .collect(joining("," , "[", "]")))
                );

        //getting tags flat not as List or Set and tags should be sorted after grouping
        purchases.stream()
                .collect(groupingBy(expense -> expense.year,
                                    collectingAndThen(flatMapping(expense -> expense.tags.stream(), toList()),
                                                      tagList -> tagList.stream()
                                                                        .sorted((e1, e2) -> e1.compareTo(e2))
                                                                        .collect(toList())
                                                     )
                                   )
                       )
                .entrySet()
                .forEach(entry ->
                        System.out.println(entry.getKey() + " -> " +
                                entry.getValue())
                );
    }
}

class Expense {
    int amount;
    int year;
    Set<String> tags;
    Expense(int amount, int year, Set<String> tags) {
        this.amount = amount;
        this.year = year;
        this.tags = tags;
    }

    @Override
    public String toString() {
       return "Expense Details: " + " year: " + year + " amount: " + " tags: " + tags.stream().collect(joining(",", "[" ,"]"));
    }
    public int getYear() {
        return year;
    }
}
