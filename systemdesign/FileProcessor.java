package systemdesign;

import systemdesign.service.ItemService;

import java.util.List;

// This class requires in java 11 runtime
//Possible improvements :
// Partitioning using hashing the key and writing to multiple queue based on the key hash(mod for now)
// replication - for distributed system and fault tolerance if one queue becomes unavailable
// fault tolerance
// transactions [a group of items sent as a whole to either process or reject as transaction]
// CAP theorem

public class FileProcessor {
    public static void main(String[] args) {
        Configuration configuration = Configuration.getInstance();
        ItemService itemService = configuration.itemService();
        //trying to get ItemService again/twice to check if configuration maintains singleton
        itemService = configuration.itemService();

        itemService.processItems(List.of("file1", "file2", "file3", "file4"));
    }
}
