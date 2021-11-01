package systemdesign.repository;

import systemdesign.repository.entity.ItemEntity;

public class ItemDBRepositoryImpl implements ItemDBRepository{
    public void save(ItemEntity entity) {
        System.out.println("Item saved to DB: " + entity.toString());
    }
}
