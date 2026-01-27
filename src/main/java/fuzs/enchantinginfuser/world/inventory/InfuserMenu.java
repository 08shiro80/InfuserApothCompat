package fuzs.enchantinginfuser.world.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

public class InfuserMenu extends AbstractContainerMenu implements ContainerListener {

    private Container enchantSlots;
    private ContainerLevelAccess levelAccess;
    private Player player;

    protected InfuserMenu(MenuType<?> menuType, int containerId) {
        super(menuType, containerId);
        throw new UnsupportedOperationException("Stub");
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slot) {
        throw new UnsupportedOperationException("Stub");
    }

    @Override
    public boolean stillValid(Player player) {
        throw new UnsupportedOperationException("Stub");
    }

    private boolean mayEnchantStack(ItemStack itemStack) {
        throw new UnsupportedOperationException("Stub");
    }

    @Override
    public void slotsChanged(Container container) {
        throw new UnsupportedOperationException("Stub");
    }

    @Override
    public void slotChanged(AbstractContainerMenu menu, int slotIndex, ItemStack stack) {
        throw new UnsupportedOperationException("Stub");
    }

    @Override
    public void dataChanged(AbstractContainerMenu menu, int dataSlotIndex, int value) {
        throw new UnsupportedOperationException("Stub");
    }
}
