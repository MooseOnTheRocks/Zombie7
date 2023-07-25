package dev.foltz.item.stage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class StagedItemGraphBuilder<T extends StagedItem> {
//    private Map<String, GunStageBuilder> stageBuilders;
    private final List<String> stageNames;
    private final List<StageBuilder<T>> stageBuilders;

    public StagedItemGraphBuilder(Map<String, StageBuilder<T>> stages) {
        this();
        stages.forEach((s, gunStageBuilder) -> {
            stageNames.add(s);
            stageBuilders.add(gunStageBuilder);
        });
    }

    public StagedItemGraphBuilder() {
//        stageBuilders = new HashMap<>();
        stageNames = new ArrayList<>();
        stageBuilders = new ArrayList<>();
    }

    public StagedItemGraphBuilder<T> stage(String name, StageBuilder<T> stageBuilder) {
        if (stageNames.contains(name)) {
            throw new RuntimeException("GunStageGraphBuilder already has stage named: " + name);
        }

        stageNames.add(name);
        stageBuilders.add(stageBuilder);

        return this;
    }

    public StagedItemGraphBuilder<T> stage(String name, Function<StageBuilder<T>, StageBuilder<T>> builder) {
        return stage(name, builder.apply(new StageBuilder<>()));
    }

    public StagedItemGraph<T> build() {
        Map<String, Stage<T>> builtStages = new HashMap<>();
        for (int i = 0; i < stageNames.size(); i++) {
            var name = stageNames.get(i);
            builtStages.put(name, stageBuilders.get(i).build());
        }

        // Maintain name ordering (prevent Map implementation from changing name string -> int mapping/order),
        // and enforce "default" as 0th stage.
        List<String> orderedNames = new ArrayList<>(stageNames);
        orderedNames.remove("default");
        orderedNames.sort(String::compareTo);
        orderedNames.add(0, "default");

        return new StagedItemGraph<>(orderedNames, builtStages);
    }
}
