package snownee.jade.api;

import snownee.jade.api.config.IPluginConfig;

public interface IBlockComponentProvider extends IToggleableProvider {
    void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config);
}
