package bigchadguys.sellingbin.data.entity;

import net.minecraft.entity.Entity;
import net.minecraft.world.ModifiableWorld;

public interface EntityPlacement<T> extends EntityPredicate {

    boolean isSubsetOf(T other);

    boolean isSubsetOf(Entity entity);

    void fillInto(T other);

    void place(ModifiableWorld world);

    T copy();

}
