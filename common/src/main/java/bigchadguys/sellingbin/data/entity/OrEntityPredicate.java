package bigchadguys.sellingbin.data.entity;

import bigchadguys.sellingbin.data.nbt.PartialCompoundNbt;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Arrays;

public class OrEntityPredicate implements EntityPredicate {

    private EntityPredicate[] children;

    public OrEntityPredicate(EntityPredicate... children) {
        this.children = children;
    }

    public EntityPredicate[] getChildren() {
        return this.children;
    }

    @Override
    public boolean test(Vec3d pos, BlockPos blockPos, PartialCompoundNbt nbt) {
        for(EntityPredicate child : this.children) {
            if(child.test(pos, blockPos, nbt)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String toString() {
        return Arrays.toString(this.children);
    }

}
