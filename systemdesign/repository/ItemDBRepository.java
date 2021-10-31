package systemdesign.repository;

import systemdesign.repository.entity.ItemEntity;

public interface ItemDBRepository {
    void save(ItemEntity entity);
}
