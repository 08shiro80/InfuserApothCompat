package snownee.jade.api.theme;

import net.minecraft.network.chat.MutableComponent;

public interface IThemeHelper {
    static IThemeHelper get() {
        throw new UnsupportedOperationException("Stub");
    }
    MutableComponent info(Object componentOrString);
    MutableComponent success(Object componentOrString);
    MutableComponent warning(Object componentOrString);
    MutableComponent danger(Object componentOrString);
}
