package bigchadguys.sellingbin.init;

import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

import java.util.function.Supplier;

public class ModItems extends ModRegistries {

    public static void register() {

    }

    public static <V extends Item> RegistrySupplier<V> register(Identifier id, Supplier<V> item) {
        return register(ITEMS, id, item);
    }

    public static <V extends Item> RegistrySupplier<V> register(String name, Supplier<V> item) {
        return register(ITEMS, name, item);
    }

}

