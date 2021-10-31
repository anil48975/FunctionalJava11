package systemdesign.service;

import systemdesign.repository.entity.ItemEntity;

import java.util.List;

public interface GlobalQueue {
    void offerItems(List<ItemEntity> items);
    List<ItemEntity> pollItems();
}
