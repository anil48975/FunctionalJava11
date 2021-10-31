package systemdesign.repository;

import systemdesign.repository.entity.ItemEntity;

import java.util.List;

public interface ItemFileRepository {
    List<ItemEntity> getItems(String filesName);
}
