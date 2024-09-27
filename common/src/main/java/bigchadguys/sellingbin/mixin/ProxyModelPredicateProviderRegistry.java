package bigchadguys.sellingbin.mixin;

import net.minecraft.client.item.ClampedModelPredicateProvider;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ModelPredicateProviderRegistry.class)
public interface ProxyModelPredicateProviderRegistry {

    @Invoker("register")
    static void register(Item item, Identifier id, ClampedModelPredicateProvider provider) {
        throw new UnsupportedOperationException();
    }

}
