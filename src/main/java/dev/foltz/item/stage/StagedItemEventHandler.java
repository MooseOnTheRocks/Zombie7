package dev.foltz.item.stage;

@FunctionalInterface
public interface StagedItemEventHandler<T extends StagedItem<?>> {
    String handleEvent(StagedItemView<? extends T> view);
}
