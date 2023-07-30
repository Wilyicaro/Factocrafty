package wily.factocrafty.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Vanishable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import wily.factocrafty.FactocraftyExpectPlatform;
import wily.factoryapi.base.FactoryCapacityTiers;
import wily.factoryapi.base.TransportState;

public class EnergyDiggerItem extends EnergyItem implements Vanishable, FactocraftyDiggerItem {
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
        getCraftyEnergy(itemStack).consumeEnergy((int) Math.max(1, blockState.getBlock().defaultDestroyTime() *2),false);
        return super.mineBlock(itemStack, level, blockState, blockPos, livingEntity);
    }
    public boolean isActivated(ItemStack itemStack){
        return itemStack.getOrCreateTag().getBoolean("activated");
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int i, boolean bl) {
        if (isActivated(stack) && !level.isClientSide && level.random.nextFloat() <= 0.7 && entity.tickCount % 18 == 0)
            getCraftyEnergy(stack).consumeEnergy(1,false);
        super.inventoryTick(stack, level, entity, i, bl);
    }


    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack itemStack, int i) {
        super.onUseTick(level, livingEntity, itemStack, i);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack enabled = player.getItemInHand(interactionHand);
        enabled.getOrCreateTag().putBoolean("activated",!isActivated(enabled));
        return InteractionResultHolder.sidedSuccess(enabled,!level.isClientSide);
    }

    @Override
    public float getDestroySpeed(ItemStack itemStack, BlockState blockState) {
        return blockState.is(this.blocks) && getCraftyEnergy(itemStack).getEnergyStored() > 0 && isActivated(itemStack) ? this.speed : 1.0F;
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
