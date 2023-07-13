package dev.foltz.item.stage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class GunStageGraphBuilder {
//    private Map<String, GunStageBuilder> stageBuilders;
    private final List<String> stageNames;
    private final List<GunStageBuilder> stageBuilders;

    public GunStageGraphBuilder(Map<String, GunStageBuilder> stages) {
        this();
        stages.forEach((s, gunStageBuilder) -> {
            stageNames.add(s);
            stageBuilders.add(gunStageBuilder);
        });
    }

    public GunStageGraphBuilder() {
//        stageBuilders = new HashMap<>();
        stageNames = new ArrayList<>();
        stageBuilders = new ArrayList<>();
    }

    public GunStageGraphBuilder stage(String name, GunStageBuilder stageBuilder) {
        if (stageNames.contains(name)) {
            throw new RuntimeException("GunStageGraphBuilder already has stage named: " + name);
        }

        stageNames.add(name);
        stageBuilders.add(stageBuilder);

        return this;
    }

    public GunStageGraphBuilder stage(String name, Function<GunStageBuilder, GunStageBuilder> builder) {
        return stage(name, builder.apply(new GunStageBuilder()));
    }

    public GunStageGraph build() {
        Map<String, GunStage> builtStages = new HashMap<>();

        // "default" stage is required and set as 0th stage.
        int defaultIndex = stageNames.indexOf("default");
        builtStages.put("default", stageBuilders.get(defaultIndex).build());
        stageNames.remove(defaultIndex);
        stageBuilders.remove(defaultIndex);

        for (int i = 0; i < stageBuilders.size(); i++) {
            builtStages.put(stageNames.get(i), stageBuilders.get(i).build());
        }
        return new GunStageGraph(stageNames, builtStages);
    }
}
