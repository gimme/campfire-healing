package dev.gimme.campfirehealing.mixin.items;

import dev.gimme.campfirehealing.Main;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Limits the max stack size for flower items to make it harder to stockpile Suspicious Stew ingredients.
 */
@Mixin(ItemStack.class)
public abstract class FlowerStackSizeMixin {

    @Inject(method = "getMaxStackSize", at = @At("RETURN"), cancellable = true)
    private void limitFlowerStackSize(CallbackInfoReturnable<Integer> cir) {
        if (Main.INSTANCE == null) return;

        int flowerMaxStackSize = Main.INSTANCE.getServerConfig().getFlowerMaxStackSize();
        if (flowerMaxStackSize < 1) return;

        ItemStack self = (ItemStack) (Object) this;
        if (self.is(ItemTags.FLOWERS)) {
            cir.setReturnValue(Math.min(cir.getReturnValue(), flowerMaxStackSize));
        }
    }
}
