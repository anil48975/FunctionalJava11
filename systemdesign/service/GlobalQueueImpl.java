package systemdesign.service;

import systemdesign.repository.entity.ItemEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;

public enum GlobalQueueImpl implements GlobalQueue{
    INSTANCE;
    private static final int GLOBAL_QUEUE_CAPACITY = 20;
    private static final PriorityBlockingQueue<ItemEntity> GLOBAL_QUEUE = new PriorityBlockingQueue<ItemEntity>(GLOBAL_QUEUE_CAPACITY, (e1, e2) -> e1.getSeq() - e2.getSeq());

    public synchronized void offerItems(List<ItemEntity> items) {
        while (GLOBAL_QUEUE.remainingCapacity() < items.size()) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            items.stream()
                    .forEach(item -> {
                        System.out.println("Item added to queue: " + item.toString());
                        GLOBAL_QUEUE.offer(item);
                    });
        } finally {
            notifyAll();
        }
    }

    public synchronized List<ItemEntity> pollItems() {
        List<ItemEntity> items = new ArrayList<>();
        while (GLOBAL_QUEUE.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            while (!GLOBAL_QUEUE.isEmpty()) {
                ItemEntity item = GLOBAL_QUEUE.poll();
                System.out.println("Item taken from the queue: " + item.toString());
                items.add(item);
            }
        } finally {
            notifyAll();
        }
        return items;
    }
}
