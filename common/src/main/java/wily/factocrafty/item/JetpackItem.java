package wily.factocrafty.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerInputPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import wily.factocrafty.Factocrafty;
import wily.factocrafty.network.FactocraftyJetpackUsePacket;
import wily.factocrafty.util.DirectionUtil;

public abstract class JetpackItem extends ArmorItem {
    public final ArmorMaterial armorMaterial;
    public JetpackItem(ArmorMaterial armorMaterial, Properties properties) {
        super(armorMaterial, EquipmentSlot.CHEST, properties);
        this.armorMaterial = armorMaterial;

    }
    protected boolean canLaunchJetpack(ItemStack stack){
        return true;
    }
    protected abstract ItemStack consumeFuel(ItemStack stack);
    @Override
    public void inventoryTick(ItemStack itemStack, Level level, Entity entity, int i, boolean bl) {
        if (entity instanceof AbstractClientPlayer player){

            if (player.getItemBySlot(EquipmentSlot.CHEST).is(itemStack.getItem()) && Minecraft.getInstance().options.keyJump.isDown() && canLaunchJetpack(itemStack)){
                //Factocrafty.NETWORK.sendToServer(new FactocraftyJetpackUsePacket(consumeFuel(itemStack)));
                player.lerpMotion(Math.sin(Math.toRadians(-player.getYRot())),0.8, Math.cos(Math.toRadians(player.getYRot())));
                consumeFuel(itemStack);
            }
        }
    }

}
