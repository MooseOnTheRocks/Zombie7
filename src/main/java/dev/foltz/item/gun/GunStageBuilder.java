package dev.foltz.item.gun;

import dev.foltz.item.stage.StageBuilder;

public class GunStageBuilder<T extends GunStagedItem<?>> extends StageBuilder<T> {
    public GunStageBuilder() {
        super();
    }

    public GunStageBuilder(int maxTicks) {
        super(maxTicks);
    }
}
