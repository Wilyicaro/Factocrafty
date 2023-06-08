package wily.factocrafty.item;

import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import wily.factocrafty.Factocrafty;
import wily.factocrafty.network.FactocraftyJetpackLaunchPacket;
import wily.factocrafty.util.DirectionUtil;

public abstract class JetpackItem extends ArmorItem {
    public final ArmorMaterial armorMaterial;
    public JetpackItem(ArmorMaterial armorMaterial, Properties properties) {
        super(armorMaterial, Type.CHESTPLATE, properties);
        this.armorMaterial = armorMaterial;

    }
    protected boolean canLaunchJetpack(ItemStack stack){
        return true;
    }
    protected abstract ItemStack consumeFuel(ItemStack stack);
    @Override
    public void inventoryTick(ItemStack itemStack, Level level, Entity entity, int i, boolean bl) {
        if (entity instanceof  Player player) {
            boolean bl1 = !player.isSpectator() && !player.isCreative();
            if (level.isClientSide) {
            if (player.getItemBySlot(EquipmentSlot.CHEST).is(itemStack.getItem()) && Minecraft.getInstance().options.keyJump.isDown() && canLaunchJetpack(itemStack)) {
                    player.lerpMotion(Math.sin(Math.toRadians(-player.getYRot())) * 0.2, 0.8, Math.cos(Math.toRadians(player.getYRot())) * 0.2);
                    double angle = Math.toRadians(DirectionUtil.rotationCyclic(player.getYRot()));
                    for (ParticleOptions b : new ParticleOptions[]{ParticleTypes.SMALL_FLAME, ParticleTypes.SMOKE}) {
                        if (level.random.nextFloat() >= 0.3){
                            level.addParticle(b, false, DirectionUtil.rotateXByCenter(angle, player.getX(), 0.1875D, -0.21875D), player.getY() + 0.675, DirectionUtil.rotateZByCenter(angle, player.getZ(), 0.1875D, -0.21875D), 0.0, 0.0, 0.0);
                            level.addParticle(b, false, DirectionUtil.rotateXByCenter(angle, player.getX(), -0.1875D, -0.21875D), player.getY() + 0.675, DirectionUtil.rotateZByCenter(angle, player.getZ(), -0.1875D, -0.21875D), 0.0, 0.0, 0.0);
                        }
                    }
                    if (bl1) Factocrafty.NETWORK.sendToServer(new FactocraftyJetpackLaunchPacket(consumeFuel(itemStack),true));
                } else if (bl1) Factocrafty.NETWORK.sendToServer(new FactocraftyJetpackLaunchPacket(ItemStack.EMPTY,false));
            }
        }
    }

}
