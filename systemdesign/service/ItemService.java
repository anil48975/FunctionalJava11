package systemdesign.service;

import java.util.List;

public interface ItemService {
    void processItems (List<String> files, boolean applyFileProcessingDelayPattern);
}
