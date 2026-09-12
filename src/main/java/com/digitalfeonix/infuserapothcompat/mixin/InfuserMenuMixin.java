package com.digitalfeonix.infuserapothcompat.mixin;

import com.digitalfeonix.infuserapothcompat.InfusionHelper;
import com.digitalfeonix.infuserapothcompat.TreasureShelfHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(targets = "fuzs.enchantinginfuser.common.world.inventory.InfuserMenu", remap = false)
public abstract class InfuserMenuMixin extends AbstractContainerMenu {

    @Shadow @Final private Container enchantSlots;
    @Shadow @Final private ContainerLevelAccess levelAccess;
    @Shadow @Final private Player player;

    protected InfuserMenuMixin(MenuType<?> menuType, int containerId) {
        super(menuType, containerId);
    }

    @Inject(method = "slotsChanged", at = @At("HEAD"))
    private void infuserapothcompat$scanTreasureShelf(Container container, CallbackInfo ci) {
        if (container == this.enchantSlots) {
            levelAccess.execute((level, pos) -> {
                TreasureShelfHelper.scanShelves(level, pos);
            });
        }
    }

    @Inject(method = "slotsChanged", at = @At("RETURN"))
    private void infuserapothcompat$resetTreasureShelf(Container container, CallbackInfo ci) {
        TreasureShelfHelper.reset();
    }

    @Inject(method = "initializeEnchantmentMaps", at = @At("HEAD"))
    private void infuserapothcompat$clientScanTreasureShelf(Level level, CallbackInfo ci) {
        if (level.isClientSide()) {
            BlockPos playerPos = this.player.blockPosition();
            for (BlockPos pos : BlockPos.betweenClosed(
                    playerPos.offset(-8, -4, -8),
                    playerPos.offset(8, 4, 8))) {
                BlockState state = level.getBlockState(pos);
                Identifier blockId = BuiltInRegistries.BLOCK.getKey(state.getBlock());
                if (blockId.getPath().contains("infuser") && blockId.getNamespace().equals("enchantinginfuser")) {
                    TreasureShelfHelper.scanShelves(level, pos);
                    if (TreasureShelfHelper.treasureShelfPresent
                            || !TreasureShelfHelper.blacklistedEnchantments.isEmpty()) return;
                }
            }
        }
    }

    @Inject(method = "initializeEnchantmentMaps", at = @At("RETURN"))
    private void infuserapothcompat$clientResetTreasureShelf(Level level, CallbackInfo ci) {
        if (level.isClientSide()) {
            TreasureShelfHelper.reset();
        }
    }

    @Inject(method = "mayEnchantStack", at = @At("HEAD"), cancellable = true)
    private void infuserapothcompat$allowInfusionItems(ItemStack itemStack, CallbackInfoReturnable<Boolean> cir) {
        if (InfusionHelper.isInfusionAvailable() && !itemStack.isEmpty()) {
            levelAccess.execute((level, pos) -> {
                Optional<InfusionHelper.InfusionResult> infusion = InfusionHelper.findMatchingInfusion(level, pos, itemStack);
                if (infusion.isPresent()) {
                    cir.setReturnValue(true);
                }
            });
        }
    }
}
