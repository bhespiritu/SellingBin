package bigchadguys.sellingbin.data.tile;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

public interface TilePlacement<T> extends TilePredicate {

    boolean isSubsetOf(T other);

    boolean isSubsetOf(BlockView world, BlockPos pos);

    void fillInto(T other);

    void place(WorldAccess world, BlockPos pos, int flags);

    T copy();

}
