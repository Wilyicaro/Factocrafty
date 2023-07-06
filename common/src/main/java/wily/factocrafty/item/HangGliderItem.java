package wily.factocrafty.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import dev.architectury.extensions.ItemExtension;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class HangGliderItem extends ElytraItem implements ItemExtension,DyeableLeatherItem {

    public static UUID HANG_UUID = UUID.fromString("d6b30458-4bb0-4eab-8c9e-76a2ce20ca7c");
    public HangGliderItem(Properties properties) {

        super(properties.defaultDurability(512));

    }
    public int getColor(ItemStack itemStack) {
        CompoundTag compoundTag = itemStack.getTagElement("display");
        return compoundTag != null && compoundTag.contains("color", 99) ? compoundTag.getInt("color") : 0xFDFDFD;
    }
    public boolean elytraFlightTick(ItemStack stack, LivingEntity entity, int flightTicks) {

        if (!entity.level().isClientSide) {
            int nextFlightTick = flightTicks + 1;
            if (nextFlightTick % 10 == 0) {
                if (nextFlightTick % 30 == 0) {
                    entity.gameEvent(GameEvent.ELYTRA_GLIDE);
                }
                stack.hurtAndBreak(1, entity, (e) -> {
                    e.broadcastBreakEvent(EquipmentSlot.CHEST);
                });
            }
        }

        return true;
    }
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack){
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ARMOR, new AttributeModifier(HANG_UUID,"Hang Glider defense", 4, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(HANG_UUID,"Hang Glider toughness", 0.3F, AttributeModifier.Operation.ADDITION));


        return slot == EquipmentSlot.CHEST ? builder.build() : ImmutableMultimap.of();
    }
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(ItemStack stack, EquipmentSlot slot) {
        return getAttributeModifiers(slot,stack);
    }
    public boolean canElytraFly(ItemStack stack, LivingEntity entity) {
        return true;
    }
    @Override
    public @Nullable EquipmentSlot getCustomEquipmentSlot(ItemStack stack) {
        return EquipmentSlot.CHEST;
    }

    @Override
    public void tickArmor(ItemStack stack, Player entity) {
        int nextRoll = entity.getFallFlyingTicks() + 1;
        if (entity.onGround() && !entity.isFallFlying()) entity.setSpeed(0.03F);
        if (!entity.level().isClientSide && nextRoll % 10 == 0) {
            if ((nextRoll / 10) % 3 == 0) {
                entity.gameEvent(GameEvent.ELYTRA_GLIDE);
            }
            stack.hurtAndBreak(1, entity, p -> p.broadcastBreakEvent(EquipmentSlot.CHEST));
        }
    }

    public boolean isValidRepairItem(ItemStack itemStack, ItemStack itemStack2) {
        return itemStack2.is(ItemTags.WOOL);
    }
}
