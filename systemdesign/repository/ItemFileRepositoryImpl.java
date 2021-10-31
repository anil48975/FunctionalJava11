package systemdesign.repository;

import systemdesign.repository.entity.ItemEntity;

import java.util.List;
import java.util.Map;

public class ItemFileRepositoryImpl implements ItemFileRepository{
        public List<ItemEntity> getItems(String filesName) {
            List<ItemEntity> file1 = List.of(new ItemEntity(1, "1234", "item1", 543, 56),
                    new ItemEntity(2, "1234", "item1", 543, 56),
                    new ItemEntity(3, "1234", "item1", 543, 56),
                    new ItemEntity(4, "1234", "item1", 543, 56));
            List<ItemEntity> file2 = List.of(new ItemEntity(5, "1234", "item1", 543, 56),
                    new ItemEntity(6, "1234", "item1", 543, 56),
                    new ItemEntity(7, "1234", "item1", 543, 56),
                    new ItemEntity(8, "1234", "item1", 543, 56));
            List<ItemEntity> file3 = List.of(new ItemEntity(9, "1234", "item1", 543, 56),
                    new ItemEntity(10, "1234", "item1", 543, 56),
                    new ItemEntity(11, "1234", "item1", 543, 56),
                    new ItemEntity(12, "1234", "item1", 543, 56));
            List<ItemEntity> file4 = List.of(new ItemEntity(13, "1234", "item1", 543, 56),
                    new ItemEntity(14, "1234", "item1", 543, 56),
                    new ItemEntity(15, "1234", "item1", 543, 56),
                    new ItemEntity(16, "1234", "item1", 543, 56));
            Map<String, List<ItemEntity>> itemRepo = Map.of("file1" , file1,
                    "file2", file2,
                    "file3", file3,
                    "file4", file4);
            return itemRepo.get(filesName);
        }
}
