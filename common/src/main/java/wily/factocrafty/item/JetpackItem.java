package wily.factocrafty.item;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import wily.factocrafty.FactocraftyClient;

public abstract class JetpackItem extends ArmorItem {
    public final ArmorMaterial armorMaterial;
    public JetpackItem(ArmorMaterial armorMaterial, Properties properties) {
        super(armorMaterial, Type.CHESTPLATE, properties.durability(-1));
        this.armorMaterial = armorMaterial;

    }
    public boolean canLaunchJetpack(ItemStack stack){
        return true;
    }
    public abstract ItemStack consumeFuel(ItemStack stack);
    @Override
    public void inventoryTick(ItemStack itemStack, Level level, Entity entity, int i, boolean bl) {
        if (level != null && level.isClientSide) FactocraftyClient.jetpackClientInventoryTick(this,itemStack,level,entity,i,bl);
    }
}
