package wily.factocrafty.item;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import org.jetbrains.annotations.Nullable;

public interface FactocraftyDiggerItem {
    default @Nullable Tier getTier(){
        return null;
    };

    default boolean shouldContinueBlockBreaking(Player player, ItemStack oldStack, ItemStack newStack) {
        return true;
    }
}
