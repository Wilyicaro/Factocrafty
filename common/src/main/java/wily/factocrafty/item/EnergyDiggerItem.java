package wily.factocrafty.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Vanishable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import wily.factocrafty.FactocraftyExpectPlatform;
import wily.factoryapi.base.FactoryCapacityTiers;
import wily.factoryapi.base.TransportState;

public class EnergyDiggerItem extends EnergyItem implements Vanishable, FactocraftyTierItem {
    private  final Tier tier;
    private final TagKey<Block> blocks;
    protected final float speed;
    private final float attackDamageBaseline;


    private final Multimap<Attribute, AttributeModifier> defaultModifiers;
    public EnergyDiggerItem(Tier tier, int f, float g, TagKey<Block> tagKey, FactoryCapacityTiers energyTier, TransportState canIE, Properties properties) {
        super(energyTier,canIE,properties);

        this.tier = tier;
        this.blocks = tagKey;
        this.speed = tier.getSpeed();
        this.attackDamageBaseline = f + tier.getAttackDamageBonus();
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Tool modifier", this.attackDamageBaseline, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Tool modifier", g, AttributeModifier.Operation.ADDITION));
        this.defaultModifiers = builder.build();
    }
    public Tier getTier() {
        return this.tier;
    }

    @Override
    public boolean mineBlock(ItemStack itemStack, Level level, BlockState blockState, BlockPos blockPos, LivingEntity livingEntity) {
        getCraftyEnergy(itemStack).consumeEnergy((int) Math.min(1, blockState.getBlock().defaultDestroyTime() *2),false);
        return super.mineBlock(itemStack, level, blockState, blockPos, livingEntity);
    }


    @Override
    public float getDestroySpeed(ItemStack itemStack, BlockState blockState) {
        if (getCraftyEnergy(itemStack).getEnergyStored() <= 0) return 0.05F;
        return blockState.is(this.blocks) ? this.speed : 1.0F;
    }

    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot equipmentSlot) {
        return equipmentSlot == EquipmentSlot.MAINHAND ? this.defaultModifiers : super.getDefaultAttributeModifiers(equipmentSlot);
    }

    public float getAttackDamage() {
        return this.attackDamageBaseline;
    }

    public boolean isCorrectToolForDrops(BlockState blockState) {
        return FactocraftyExpectPlatform.platformCorrectDiggerToolForDrops(tier,blocks,blockState);
    }
}
