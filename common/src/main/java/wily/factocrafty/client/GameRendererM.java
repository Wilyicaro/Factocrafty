package wily.factocrafty.client;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface GameRendererM {
    void loadArmorEffects(Player p, ItemStack s, boolean post);
}
