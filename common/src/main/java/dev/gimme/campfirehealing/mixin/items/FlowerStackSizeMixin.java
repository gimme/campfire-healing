package dev.gimme.campfirehealing.mixin.items;

import dev.gimme.campfirehealing.Main;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Limits the max stack size for Oxeye Daisies to make it harder to stockpile Suspicious Stew (Regeneration) ingredients.
 */
@Mixin(ItemStack.class)
public abstract class FlowerStackSizeMixin {

    @Inject(method = "getMaxStackSize", at = @At("RETURN"), cancellable = true)
    private void limitFlowerStackSize(CallbackInfoReturnable<Integer> cir) {
        if (Main.INSTANCE == null) return;

        int oxeyeDaisyMaxStackSize = Main.INSTANCE.getServerConfig().getOxeyeDaisyMaxStackSize();
        if (oxeyeDaisyMaxStackSize < 1) return;

        ItemStack self = (ItemStack) (Object) this;
        if (self.is(Items.OXEYE_DAISY)) {
            cir.setReturnValue(Math.min(cir.getReturnValue(), oxeyeDaisyMaxStackSize));
        }
    }
}
