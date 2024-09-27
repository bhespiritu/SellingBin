package bigchadguys.sellingbin.block;

import net.minecraft.util.StringIdentifiable;

public enum BinMode implements StringIdentifiable {
    BLOCK_BOUND("block_bound"),
    PLAYER_BOUND("player_bound");

    private final String id;

    BinMode(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    @Override
    public String asString() {
        return this.id;
    }
}
