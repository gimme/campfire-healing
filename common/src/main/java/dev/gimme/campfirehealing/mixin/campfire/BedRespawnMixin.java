package dev.gimme.campfirehealing.mixin.campfire;

import dev.gimme.campfirehealing.Main;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Blocks bed use at Y-levels where campfire healing is restricted.
 */
@Mixin(BedBlock.class)
public class BedRespawnMixin {

    @Inject(method = "useWithoutItem", at = @At("HEAD"), cancellable = true)
    private void blockBedUseAtBlockedYLevel(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir) {
        if (level.isClientSide()) return;
        if (!(level instanceof ServerLevel serverLevel)) return;
        if (!Main.INSTANCE.getServerConfig().isBlockBedRespawnBelowMinY()) return;

        if (pos.getY() < Main.INSTANCE.getServerConfig().getMinYLevelForDimension(serverLevel, false)) {
            player.sendOverlayMessage(Component.translatable("block.campfirehealing.bed.too_deep"));
            cir.setReturnValue(InteractionResult.SUCCESS_SERVER);
        }
    }
}
