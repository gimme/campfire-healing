package dev.gimme.campfirehealing.mixin.soulfire;

import dev.gimme.campfirehealing.domain.soulfire.SoulfireBehavior;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipePropertySet;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CampfireBlock.class)
public class SoulCampfireInteractMixin {

    /**
     * Allows interacting with Soul Campfires using custom fuel items.
     */
    @Redirect(method = "useItemOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/RecipePropertySet;test(Lnet/minecraft/world/item/ItemStack;)Z"))
    private boolean allowCustomFuelUse(RecipePropertySet instance, ItemStack itemStack, ItemStack p_316347_, BlockState state, Level level) {
        if (!level.isClientSide() && state.is(Blocks.SOUL_CAMPFIRE) && SoulfireBehavior.isSoulFuelItem(itemStack, level)) return true;
        return instance.test(itemStack);
    }
}
