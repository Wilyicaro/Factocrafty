package wily.factocrafty.forge.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.AbstractZombieRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import wily.factocrafty.client.renderer.entity.JetpackModel;
import wily.factocrafty.item.FluidCellItem;
import wily.factocrafty.item.JetpackItem;
import wily.factoryapi.forge.base.ForgeItemFluidHandler;

import java.util.function.Consumer;

@Mixin(JetpackItem.class)
public class AddJetpackModel extends Item{


    public AddJetpackModel(Properties arg) {
        super(arg);



    }

    @Override
    public @Nullable String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return JetpackModel.getTexture().toString();
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original) {

                return new JetpackModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(JetpackModel.LAYER_LOCATION));
            }
        });
        super.initializeClient(consumer);
    }
}
