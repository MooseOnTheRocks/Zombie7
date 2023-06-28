package dev.foltz.gui;

import com.ibm.icu.text.Collator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.CustomizeBuffetLevelScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.GeneratorOptionsHolder;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class Z7CustomizeWorldScreen extends Screen {

    private final Screen parent;
    private final Consumer<RegistryEntry<Biome>> onDone;
    final Registry<Biome> biomeRegistry;
    private Z7CustomizeWorldScreen.CustomBiomesListWidget biomeSelectionList;
    RegistryEntry<Biome> biome;
    private ButtonWidget confirmButton;

    public Z7CustomizeWorldScreen(Screen parent, GeneratorOptionsHolder generatorOptionsHolder, Consumer<RegistryEntry<Biome>> onDone) {
//        super(Text.of("Zombie7 World Customization"));
        super(Text.translatable("createWorld.customize.zombie7.title"));
        this.parent = parent;
        this.onDone = onDone;
        this.biomeRegistry = generatorOptionsHolder.getCombinedRegistryManager().get(RegistryKeys.BIOME);
        RegistryEntry registryEntry = (RegistryEntry)this.biomeRegistry.getEntry(BiomeKeys.PLAINS).or(() -> this.biomeRegistry.streamEntries().findAny()).orElseThrow();
        this.biome = generatorOptionsHolder.selectedDimensions().getChunkGenerator().getBiomeSource().getBiomes().stream().findFirst().orElse(registryEntry);
    }

    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }

    @Override
    protected void init() {
        this.biomeSelectionList = new Z7CustomizeWorldScreen.CustomBiomesListWidget();
        this.addSelectableChild(this.biomeSelectionList);
        this.confirmButton = this.addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, button -> {
            this.onDone.accept(this.biome);
            this.client.setScreen(this.parent);
        }).dimensions(this.width / 2 - 155, this.height - 28, 150, 20).build());
        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.CANCEL, button -> this.client.setScreen(this.parent)).dimensions(this.width / 2 + 5, this.height - 28, 150, 20).build());
        this.biomeSelectionList.setSelected((Z7CustomizeWorldScreen.CustomBiomesListWidget.CustomBiomeItem)this.biomeSelectionList.children().stream().filter(entry -> Objects.equals(entry.biome, this.biome)).findFirst().orElse(null));
    }

    void refreshConfirmButton() {
        this.confirmButton.active = this.biomeSelectionList.getSelectedOrNull() != null;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackgroundTexture(matrices);
        this.biomeSelectionList.render(matrices, mouseX, mouseY, delta);
        CustomizeBuffetLevelScreen.drawCenteredTextWithShadow(matrices, this.textRenderer, this.title, this.width / 2, 8, 0xFFFFFF);
        CustomizeBuffetLevelScreen.drawCenteredTextWithShadow(matrices, this.textRenderer, Text.translatable("createWorld.customize.zombie7.biome"), this.width / 2, 28, 0xA0A0A0);
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Environment(value=EnvType.CLIENT)
    class CustomBiomesListWidget
            extends AlwaysSelectedEntryListWidget<CustomBiomesListWidget.CustomBiomeItem> {
        CustomBiomesListWidget() {
            super(Z7CustomizeWorldScreen.this.client, Z7CustomizeWorldScreen.this.width, Z7CustomizeWorldScreen.this.height, 40, Z7CustomizeWorldScreen.this.height - 37, 16);
            Collator collator = Collator.getInstance(Locale.getDefault());
            Z7CustomizeWorldScreen.this.biomeRegistry.streamEntries().map(entry -> new Z7CustomizeWorldScreen.CustomBiomesListWidget.CustomBiomeItem((RegistryEntry.Reference<Biome>)entry)).sorted(Comparator.comparing(biome -> biome.text.getString(), collator)).forEach(entry -> this.addEntry(entry));
        }

        @Override
        public void setSelected(@Nullable Z7CustomizeWorldScreen.CustomBiomesListWidget.CustomBiomeItem customBiomeItem) {
            super.setSelected(customBiomeItem);
            if (customBiomeItem != null) {
                Z7CustomizeWorldScreen.this.biome = customBiomeItem.biome;
            }
            Z7CustomizeWorldScreen.this.refreshConfirmButton();
        }

        @Environment(value=EnvType.CLIENT)
        class CustomBiomeItem
                extends AlwaysSelectedEntryListWidget.Entry<CustomBiomeItem> {
            final RegistryEntry.Reference<Biome> biome;
            final Text text;
//            final Text hoverText;

            public CustomBiomeItem(RegistryEntry.Reference<Biome> biome) {
                this.biome = biome;
                Identifier identifier = biome.registryKey().getValue();
                String string = identifier.toTranslationKey("biome");
                this.text = Language.getInstance().hasTranslation(string) ? Text.translatable((String)string) : Text.literal((String)identifier.toString());
//                this.hoverText = Text.of("howdy!");
            }

            @Override
            public Text getNarration() {
                return Text.translatable((String)"narrator.select", (Object[])new Object[]{this.text});
            }

            @Override
            public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
                DrawableHelper.drawTextWithShadow(matrices, Z7CustomizeWorldScreen.this.textRenderer, this.text, x + 5, y + 2, 0xFFFFFF);
//                if (hovered) {
//                    DrawableHelper.drawTextWithShadow(matrices, Zombie7CustomizeWorldScreen.this.textRenderer, this.hoverText, mouseX, mouseY, 0xFFFFFF);
//                }
            }

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                if (button == 0) {
                    Z7CustomizeWorldScreen.CustomBiomesListWidget.this.setSelected(this);
                    return true;
                }
                return false;
            }
        }
    }
}
