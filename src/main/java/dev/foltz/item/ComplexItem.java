package dev.foltz.item;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.Arrays;

public abstract class ComplexItem extends Item {
    public ComplexItem(Settings settings) {
        super(settings);
    }

    @Override
    protected String getOrCreateTranslationKey() {
        if (this.translationKey == null) {
            Identifier id = Registries.ITEM.getId(this);
            var fullPath = id.getPath().replace('/', '.');
            var parts = fullPath.split("\\.");
            // Strip off trailing .default
            int newLength = parts[parts.length - 1].equals(".default") ? parts.length : parts.length - 1;
            parts = Arrays.copyOf(parts, newLength);
            var joined = String.join(".", parts);
            return "item" + "." + id.getNamespace() + "." + joined;
        }
        return this.translationKey;
    }
}
