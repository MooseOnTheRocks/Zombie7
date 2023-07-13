package dev.foltz.item.stage;

@FunctionalInterface
public interface GunStageEventHandler {
    String handleEvent(GunStageView view);
}
