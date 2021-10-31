package systemdesign;

import systemdesign.service.ItemService;

import java.util.List;

public class FileProcessor {
    public static void main(String[] args) {
        Configuration configuration = Configuration.getInstance();
        ItemService itemService = configuration.itemService();
        //trying to get ItemService again/twice to check if configuration maintains singleton
        itemService = configuration.itemService();

        itemService.processItems(List.of("file1", "file2", "file3", "file4"));
    }
}
