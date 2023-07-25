package dev.foltz.item.grenade;

import dev.foltz.item.stage.StageBuilder;

public class GrenadeStageBuilder extends StageBuilder<StagedGrenadeItem> {
    public GrenadeStageBuilder() {
        super();
    }

    public GrenadeStageBuilder(int maxTicks) {
        super(maxTicks);
    }
}
