package dev.foltz.item.stage;

import java.util.List;
import java.util.Map;

public class GunStageGraph {
    public final List<String> nameOrder;
    public final Map<String, GunStage> gunStages;

    public GunStageGraph(List<String> nameOrder, Map<String, GunStage> gunStages) {
        this.nameOrder = List.copyOf(nameOrder);
        this.gunStages = Map.copyOf(gunStages);
    }

    public GunStage stageFromId(int id) {
        return gunStages.get(nameFromId(id));
    }

    public GunStage stageFromName(String id) {
        return gunStages.get(id);
    }

    public int idFromName(String name) {
        return nameOrder.indexOf(name);
    }

    public String nameFromId(int id) {
        return id < 0 || id >= nameOrder.size() ? "default" : nameOrder.get(id);
    }
}
