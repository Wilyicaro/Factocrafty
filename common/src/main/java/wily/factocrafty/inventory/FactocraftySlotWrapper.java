package wily.factocrafty.inventory;

import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class FactocraftySlotWrapper extends Slot {
    final Slot target;

    public boolean active;
    public final int initialX;
    public final int initialY;

    public FactocraftySlotWrapper(Slot slot, int i, int x, int y) {
        super(slot.container, i, x, y);
        this.target = slot;
        this.initialX = x;
        this.initialY = y;
    }
    public FactocraftySlotWrapper(Slot slot) {
        this(slot, slot.index,slot.x, slot.y);
    }

    public void onTake(Player player, ItemStack itemStack) {
        this.target.onTake(player, itemStack);
    }

    public boolean mayPlace(ItemStack itemStack) {
        return this.target.mayPlace(itemStack);
    }

    public ItemStack getItem() {
        return this.target.getItem();
    }

    public boolean hasItem() {
        return this.target.hasItem();
    }

    public void setByPlayer(ItemStack itemStack) {
        this.target.setByPlayer(itemStack);
    }

    public void set(ItemStack itemStack) {
        this.target.set(itemStack);
    }

    public void setChanged() {
        this.target.setChanged();
    }

    public int getMaxStackSize() {
        return this.target.getMaxStackSize();
    }

    public int getMaxStackSize(ItemStack itemStack) {
        return this.target.getMaxStackSize(itemStack);
    }

    @Nullable
    public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
        return this.target.getNoItemIcon();
    }

    public ItemStack remove(int i) {
        return this.target.remove(i);
    }

    public boolean isActive() {
        return this.target.isActive() && active;
    }

    public boolean mayPickup(Player player) {
        return this.target.mayPickup(player);
    }
}
