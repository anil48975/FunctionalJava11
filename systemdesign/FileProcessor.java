package systemdesign;

import systemdesign.service.ItemService;

import java.util.List;

/**
 * This class is a client program of a service [ItemService]
 * which reads items from files and write them to DB.
 *
 * This and all other other classes in this package require Java11 runtime.
 */


public class FileProcessor {
    public static void main(String[] args) {
        Configuration configuration = Configuration.getInstance();
        ItemService itemService = configuration.itemService();
        //trying to get ItemService again/twice to check if configuration maintains singleton
        itemService = configuration.itemService();

        itemService.processItems(List.of("file10", "file20", "file3", "file4"), true);
        itemService.processItems(List.of("file10", "file20", "file3", "file4"), false);
    }
}
