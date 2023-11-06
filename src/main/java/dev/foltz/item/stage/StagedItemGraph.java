package dev.foltz.item.stage;

import java.util.List;
import java.util.Map;

public class StagedItemGraph<T extends StagedItem<?>> {
    public final List<String> nameOrder;
    public final Map<String, Stage<T>> gunStages;

    public StagedItemGraph(List<String> nameOrder, Map<String, Stage<T>> gunStages) {
        this.nameOrder = List.copyOf(nameOrder);
        this.gunStages = Map.copyOf(gunStages);
    }

    public Stage<T> stageFromId(int id) {
        return gunStages.get(nameFromId(id));
    }

    public int idFromName(String name) {
        var index = nameOrder.indexOf(name);
        return Math.max(index, 0);
    }

    public String nameFromId(int id) {
        return id < 0 || id >= nameOrder.size() ? "default" : nameOrder.get(id);
    }
}
