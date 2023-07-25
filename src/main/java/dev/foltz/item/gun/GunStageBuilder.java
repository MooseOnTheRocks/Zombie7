package dev.foltz.item.gun;

import dev.foltz.item.stage.StageBuilder;

public class GunStageBuilder extends StageBuilder<GunStagedItem> {
    public GunStageBuilder() {
        super();
    }

    public GunStageBuilder(int maxTicks) {
        super(maxTicks);
    }
}
