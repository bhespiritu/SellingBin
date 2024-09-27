package bigchadguys.sellingbin.data.serializable;

import net.minecraft.nbt.NbtElement;

import java.util.Optional;

public interface INbtSerializable<N extends NbtElement> {

    Optional<N> writeNbt();

    void readNbt(N nbt);

}
