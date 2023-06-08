package wily.factocrafty.mixin;

import net.fabricmc.fabric.api.entity.event.v1.FabricElytraItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.impl.item.FabricItemInternals;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import wily.factocrafty.item.HangGliderItem;

@Mixin(HangGliderItem.class)
public class HangGliderImplementer extends Item implements FabricElytraItem {
    public HangGliderImplementer(Properties properties) {
        super(new FabricItemSettings().equipmentSlot((i)-> EquipmentSlot.CHEST));
    }
}
