package wily.factocrafty.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.ChestBoat;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import wily.factocrafty.init.Registration;

public class FactocraftyChestBoat extends ChestBoat implements IFactocraftyBoat{
    public FactocraftyChestBoat(EntityType<? extends ChestBoat> entityType, Level level) {
        super(entityType, level);
    }
    public FactocraftyChestBoat(Level level, double d, double e, double f) {
        this(Registration.FACTOCRAFTY_CHEST_BOAT.get(), level);
        this.setPos(d, e, f);
        this.xo = d;
        this.yo = e;
        this.zo = f;
    }
    public Item getDropItem() {
        switch (this.getFactocraftyBoatType()) {
            case RUBBER:
            default:
                return Registration.RUBBER_CHEST_BOAT_ITEM.get();
        }
    }
    public IFactocraftyBoat.Type getFactocraftyBoatType() {
        return IFactocraftyBoat.Type .byId(this.entityData.get(DATA_ID_TYPE));
    }

    public void setType(IFactocraftyBoat.Type  type) {
        this.entityData.set(DATA_ID_TYPE, type.ordinal());;
    }

    @Override
    protected Component getTypeName() {
        return Component.translatable("entity.minecraft.chest_boat");
    }

    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        compoundTag.putString("Type", this.getFactocraftyBoatType().getName());
        this.addChestVehicleSaveData(compoundTag);
    }

    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        if (compoundTag.contains("Type", 8)) {
            this.setType(IFactocraftyBoat.Type.byName(compoundTag.getString("Type")));
        }
        this.readChestVehicleSaveData(compoundTag);

    }

}
