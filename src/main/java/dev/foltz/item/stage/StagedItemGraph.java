package dev.foltz.item.stage;

import java.util.List;
import java.util.Map;

public class StagedItemGraph<T extends StagedItem> {
    public final List<String> nameOrder;
    public final Map<String, Stage<T>> gunStages;

    public StagedItemGraph(List<String> nameOrder, Map<String, Stage<T>> gunStages) {
        this.nameOrder = List.copyOf(nameOrder);
        this.gunStages = Map.copyOf(gunStages);
        System.out.println("Making new GunStageGraph:");
        System.out.println("  - " + nameOrder);
        System.out.println("  - " + gunStages);
    }

    public Stage<T> stageFromId(int id) {
        var stage = gunStages.get(nameFromId(id));
        if (stage == null) {
            System.out.println("Got NULL stage from id: " + id);
            System.out.println("All stages: " + gunStages.keySet());
        }
        return stage;
    }

    public int idFromName(String name) {
        var index = nameOrder.indexOf(name);
        return Math.max(index, 0);
    }

    public String nameFromId(int id) {
        var newid = id < 0 || id >= nameOrder.size() ? "default" : nameOrder.get(id);
//        System.out.println("id " + id + " = " + newid);
        return newid;
    }
}
