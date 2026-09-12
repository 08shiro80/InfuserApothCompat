package com.digitalfeonix.infuserapothcompat;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.WeakHashMap;

@EventBusSubscriber(modid = InfuserApothCompat.MODID)
public class InfuserContainerHandler {

    private static final WeakHashMap<AbstractContainerMenu, ItemStack> lastSeenItems = new WeakHashMap<>();
    private static Class<?> infuserMenuClass = null;
    private static Field enchantSlotsField = null;
    private static Field levelAccessField = null;
    private static boolean initialized = false;

    private static void init() {
        if (initialized) return;
        initialized = true;

        try {
            infuserMenuClass = Class.forName("fuzs.enchantinginfuser.world.inventory.InfuserMenu");
            enchantSlotsField = infuserMenuClass.getDeclaredField("enchantSlots");
            enchantSlotsField.setAccessible(true);
            levelAccessField = infuserMenuClass.getDeclaredField("levelAccess");
            levelAccessField.setAccessible(true);

            InfuserApothCompat.LOGGER.info("InfuserContainerHandler initialized successfully");
        } catch (Exception e) {
            InfuserApothCompat.LOGGER.debug("Could not initialize InfuserContainerHandler: {}", e.getMessage());
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) return;
        if (!InfuserApothCompat.enchModuleEnabled) return;
        if (!InfusionHelper.isInfusionAvailable()) return;

        init();
        if (infuserMenuClass == null) return;

        AbstractContainerMenu menu = player.containerMenu;

        if (menu == null || !infuserMenuClass.isInstance(menu)) return;

        try {
            Container enchantSlots = (Container) enchantSlotsField.get(menu);
            ItemStack currentItem = enchantSlots.getItem(0);
            ItemStack lastItem = lastSeenItems.get(menu);

            if (currentItem.isEmpty()) {
                lastSeenItems.remove(menu);
                return;
            }

            if (lastItem != null && ItemStack.isSameItemSameComponents(currentItem, lastItem)) {
                return;
            }

            lastSeenItems.put(menu, currentItem.copy());

            ContainerLevelAccess levelAccess = (ContainerLevelAccess) levelAccessField.get(menu);
            Level level = player.level();

            final Container finalEnchantSlots = enchantSlots;
            final ItemStack finalCurrentItem = currentItem;

            levelAccess.execute((lvl, pos) -> {
                Optional<InfusionHelper.InfusionResult> infusion = InfusionHelper.findMatchingInfusion(lvl, pos, finalCurrentItem);

                if (infusion.isPresent() && infusion.get().canInfuse()) {
                    InfusionHelper.InfusionResult result = infusion.get();

                    InfuserApothCompat.LOGGER.info("Performing infusion via tick handler: {} -> {}",
                            finalCurrentItem.getItem(), result.output().getItem());

                    finalEnchantSlots.setItem(0, result.output().copy());
                    finalEnchantSlots.setChanged();
                    lastSeenItems.put(menu, result.output().copy());

                    lvl.playSound(null, pos, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
                }
            });

        } catch (Exception e) {
            InfuserApothCompat.LOGGER.debug("Error in onPlayerTick: {}", e.getMessage());
        }
    }
}
