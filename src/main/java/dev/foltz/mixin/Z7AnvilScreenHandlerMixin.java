package dev.foltz.mixin;

import dev.foltz.item.gun.GunStagedItem;
import dev.foltz.item.Z7Items;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.Property;
import net.minecraft.text.Text;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AnvilScreenHandler.class)
public abstract class Z7AnvilScreenHandlerMixin {
    @Shadow @Final private Property levelCost;

    @Shadow private int repairItemUsage;

    @Shadow private String newItemName;

    @Inject(method="canTakeOutput", at=@At("HEAD"), cancellable = true)
    protected void allowedToTakeOutput(PlayerEntity player, boolean present, CallbackInfoReturnable<Boolean> cir) {
        var self = (AnvilScreenHandler) (Object) this;
        var input = self.input;
        var output = self.output;
        ItemStack stackTool = input.getStack(0);
        ItemStack stackRepair = input.getStack(1);

        if ((stackTool.getItem() instanceof GunStagedItem gunItem) && (stackRepair.getItem() == Z7Items.ITEM_REPAIR_KIT)) {
//            System.out.println("Something...");
            cir.setReturnValue(stackTool.getDamage() != 0);
        }
    }

    @Inject(method="updateResult", at=@At("HEAD"), cancellable = true)
    public void doUpdateResult(CallbackInfo ci) {
//        System.out.println("Here i am!");
        var self = (AnvilScreenHandler) (Object) this;
        var input = self.input;
        var output = self.output;
        ItemStack stackTool = input.getStack(0);
        ItemStack stackRepair = input.getStack(1);

//        System.out.println("Tool: " + stackTool);
//        System.out.println("Repair: " + stackRepair);

        if (!(stackTool.getItem() instanceof GunStagedItem gunItem)) {
            return;
        }

        if (!(stackRepair.getItem() == Z7Items.ITEM_REPAIR_KIT)) {
            return;
        }

//        System.out.println("Updating anvil result with gun and repair kit!");

        ItemStack result = stackTool.copy();

        boolean renamed = false;
        boolean repaired = false;

        if (StringUtils.isBlank(newItemName)) {
            if (stackTool.hasCustomName()) {
                result.removeCustomName();
                renamed = true;
            }
        }
        else if (!this.newItemName.equals(stackTool.getName().getString())) {
            result.setCustomName(Text.literal(this.newItemName));
            renamed = true;
        }

        if (!result.isDamageable() || result.getDamage() == 0) {
//            System.out.println("Gun cannot be repaired atm...");
            this.levelCost.set(0);
            this.repairItemUsage = 0;
        }
        else {
            repaired = true;
            result.setDamage(0);
            this.levelCost.set(0);
            this.repairItemUsage = 1;
        }

        if (renamed || repaired) {
            output.setStack(0, result);
        }
        else {
            output.setStack(0, ItemStack.EMPTY);
        }
        self.sendContentUpdates();
        ci.cancel();
    }
}
