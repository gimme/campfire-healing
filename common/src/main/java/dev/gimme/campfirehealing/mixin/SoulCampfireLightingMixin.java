package dev.gimme.campfirehealing.mixin;

import dev.gimme.campfirehealing.Main;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CampfireBlock.class)
public class SoulCampfireLightingMixin {

    /**
     * Makes Soul Campfires unlit by default when placed.
     */
    @Inject(method = "getStateForPlacement", at = @At(value = "RETURN"), cancellable = true)
    private void setUnlitByDefault(BlockPlaceContext context, CallbackInfoReturnable<BlockState> cir) {
        var blockState = cir.getReturnValue();

        if (blockState.is(Blocks.SOUL_CAMPFIRE) && Main.INSTANCE.getServerConfig().isSoulfireLitByFuel()) {
            cir.setReturnValue(blockState.setValue(CampfireBlock.LIT, false));
        }
    }

    /**
     * Makes Soul Campfires unlit by default when placed.
     */
    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/CampfireBlock;registerDefaultState(Lnet/minecraft/world/level/block/state/BlockState;)V"))
    private BlockState redirectRegisterDefaultState(BlockState blockState) {
        if (blockState.is(Blocks.SOUL_CAMPFIRE) && Main.INSTANCE.getServerConfig().isSoulfireLitByFuel()) {
            return blockState
                .setValue(CampfireBlock.LIT, false)
                .setValue(CampfireBlock.SIGNAL_FIRE, true);
        }

        return blockState;
    }

    /**
     * Prevents lighting Soul Campfires with right click.
     */
    @Inject(method = "canLight", at = @At("HEAD"), cancellable = true)
    private static void preventNaturalLighting(BlockState blockState, CallbackInfoReturnable<Boolean> cir) {
        if (blockState.is(Blocks.SOUL_CAMPFIRE) && Main.INSTANCE.getServerConfig().isSoulfireLitByFuel()) {
            cir.setReturnValue(false);
        }
    }

    /**
     * Prevents lighting Soul Campfires with projectiles.
     */
    @Inject(method = "onProjectileHit", at = @At("HEAD"), cancellable = true)
    private void preventProjectileLighting(Level level, BlockState blockState, BlockHitResult hit, Projectile projectile, CallbackInfo ci) {
        if (blockState.is(Blocks.SOUL_CAMPFIRE) && Main.INSTANCE.getServerConfig().isSoulfireLitByFuel()) {
            ci.cancel();
        }
    }
}
