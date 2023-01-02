package wily.factocrafty.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import wily.factocrafty.init.Registration;

public class FactocraftyBoat extends Boat implements IFactocraftyBoat{
    public FactocraftyBoat(EntityType<? extends Boat> entityType, Level level) {
        super(entityType, level);
    }
    public FactocraftyBoat(Level level, double d, double e, double f) {
        this(Registration.FACTOCRAFTY_BOAT.get(), level);
        this.setPos(d, e, f);
        this.xo = d;
        this.yo = e;
        this.zo = f;
    }
    public Item getDropItem() {
        switch (this.getFactocraftyBoatType()) {
            case RUBBER:
            default:
                return Registration.RUBBER_BOAT_ITEM.get();
        }
    }
    public IFactocraftyBoat.Type getFactocraftyBoatType() {
        return IFactocraftyBoat.Type.byId(this.entityData.get(DATA_ID_TYPE));
    }

    public void setType(IFactocraftyBoat.Type type) {
        this.entityData.set(DATA_ID_TYPE, type.ordinal());;
    }



    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        compoundTag.putString("Type", this.getFactocraftyBoatType().getName());
    }

    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        if (compoundTag.contains("Type", 8)) {
            this.setType(IFactocraftyBoat.Type.byName(compoundTag.getString("Type")));
        }

    }

}
