package wily.factocrafty.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import wily.factocrafty.FactocraftyClient;
import wily.factocrafty.client.renderer.entity.JetpackModel;
import wily.factoryapi.base.IFactoryItem;
import wily.factoryapi.base.client.IFactoryItemClientExtension;

import java.util.function.Consumer;

public abstract class JetpackItem extends ArmorItem implements IFactoryItem {
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

    @Override
    public void clientExtension(Consumer<IFactoryItemClientExtension> clientExtensionConsumer) {
        clientExtensionConsumer.accept(new IFactoryItemClientExtension(){
            @Override
            public HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
                return new JetpackModel<>(original, Minecraft.getInstance().getEntityModels().bakeLayer(JetpackModel.LAYER_LOCATION));
            }

            @Override
            public ResourceLocation getArmorTexture() {
                return asItem() instanceof FlexJetpackItem ?  JetpackModel.getFlexTexture() : JetpackModel.getElectricTexture();
            }
        });
    }
}
