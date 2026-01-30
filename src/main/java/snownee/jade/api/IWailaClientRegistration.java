package snownee.jade.api;

import net.minecraft.world.level.block.Block;

public interface IWailaClientRegistration {
    void registerBlockComponent(IBlockComponentProvider provider, Class<? extends Block> blockClass);
}
