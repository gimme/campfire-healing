package dev.gimme.campfirehealing.mixin;

import dev.gimme.campfirehealing.SoulfireBehavior;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

/**
 * Modifies Soul-specific Campfire behavior.
 */
@Mixin(CampfireBlockEntity.class)
public class SoulCampfireMixin {

    @Shadow
    @Final
    private NonNullList<ItemStack> items;

    @Shadow
    @Final
    private int[] cookingProgress;

    @Shadow
    @Final
    private int[] cookingTime;

    /**
     * Allows placing custom fuel in Soul Campfires, by making the campfire think we're trying to place a valid recipe.
     */
    @Redirect(method = "placeFood", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/RecipeManager;getRecipeFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/item/crafting/RecipeInput;Lnet/minecraft/world/level/Level;)Ljava/util/Optional;"))
    private Optional<RecipeHolder<CampfireCookingRecipe>> allowPlacingFuel(RecipeManager instance, RecipeType<?> recipeType, RecipeInput input, Level level) {
        CampfireBlockEntity campfire = (CampfireBlockEntity) (Object) this;
        var itemStack = input.getItem(0);

        if (SoulfireBehavior.isSoulFuel(campfire, itemStack, level)) {
            var recipe = new CampfireCookingRecipe("", CookingBookCategory.MISC, Ingredient.of(Items.DIRT), new ItemStack(Items.DIRT), 0, 1);
            return Optional.of(new RecipeHolder<>(null, recipe));
        }

        return instance.getRecipeFor(RecipeType.CAMPFIRE_COOKING, (SingleRecipeInput) input, level);
    }

    /**
     * Sets the burn time of fuel placed in Soul Campfires, stacking it with existing fuel if present.
     */
    @Redirect(method = "placeFood", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/CampfireCookingRecipe;cookingTime()I"))
    private int makeTheFuelStackBurnTime(CampfireCookingRecipe instance, ServerLevel level, @Nullable LivingEntity entity, ItemStack itemStack) {
        CampfireBlockEntity campfire = (CampfireBlockEntity) (Object) this;
        if (SoulfireBehavior.isSoulFuel(campfire, itemStack, level)) {
            return SoulfireBehavior.getNextFuelBurnTime(itemStack, items, cookingProgress, cookingTime, level);
        }
        return instance.cookingTime();
    }

    /**
     * Triggers side effects of fuel being placed in Soul Campfires.
     */
    @Inject(method = "placeFood", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/CampfireBlockEntity;markUpdated()V"))
    private void onPlaceFuel(ServerLevel level, @Nullable LivingEntity entity, ItemStack itemStack, CallbackInfoReturnable<Boolean> cir) {
        var campfire = (CampfireBlockEntity) (Object) this;
        if (!SoulfireBehavior.isSoulFuel(campfire, itemStack, level)) return;

        int placeIndex = -1;
        for (int i = 0; i < this.items.size(); ++i) {
            if (this.items.get(i).is(itemStack.getItem()) && cookingProgress[i] == 0) {
                placeIndex = i;
                break;
            }
        }

        SoulfireBehavior.onFuelPlaced(campfire, placeIndex);
    }

    /**
     * Prevents fuel from dropping loot when done, and triggers side effects of fuel being burned out.
     */
    @Redirect(method = "cookTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/Containers;dropItemStack(Lnet/minecraft/world/level/Level;DDDLnet/minecraft/world/item/ItemStack;)V"))
    private static void onCookResultDrop(Level l, double x, double y, double z, ItemStack itemStack, ServerLevel level, BlockPos pos, BlockState state, CampfireBlockEntity campfire) {
        if (SoulfireBehavior.isSoulFuel(campfire, itemStack, level)) {
            SoulfireBehavior.onFuelBurned(campfire, level);
            return;
        }

        Containers.dropItemStack(level, x, y, z, itemStack);
    }

    /**
     * Prevents fuel from dropping as loot when the Soul Campfire is broken.
     */
    @ModifyArg(method = "preRemoveSideEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/Containers;dropContents(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/NonNullList;)V"))
    private NonNullList<ItemStack> preventFuelDrops(Level level, BlockPos pos, NonNullList<ItemStack> stackList) {
        CampfireBlockEntity campfire = (CampfireBlockEntity) (Object) this;
        for (int i = 0; i < stackList.size(); i++) {
            if (SoulfireBehavior.isSoulFuel(campfire, stackList.get(i), level)) {
                stackList.set(i, ItemStack.EMPTY);
            }
        }
        return stackList;
    }
}
