package bigchadguys.sellingbin.data.adapter;

import net.minecraft.nbt.NbtElement;

import java.util.Optional;

public interface INbtAdapter<T, N extends NbtElement, C> {

    Optional<N> writeNbt(T value, C context);

    Optional<T> readNbt(N nbt, C context);

}
