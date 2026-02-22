package com.digitalfeonix.infuserapothcompat.mixin;

import com.digitalfeonix.infuserapothcompat.InfusionHelper;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(targets = "fuzs.enchantinginfuser.world.inventory.InfuserMenu", remap = false)
public abstract class InfuserMenuMixin extends AbstractContainerMenu {

    @Shadow @Final private Container enchantSlots;
    @Shadow @Final private ContainerLevelAccess levelAccess;
    @Shadow @Final private Player player;

    protected InfuserMenuMixin(MenuType<?> menuType, int containerId) {
        super(menuType, containerId);
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
