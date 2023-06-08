package wily.factocrafty.item;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import wily.factocrafty.init.Registration;

import java.util.List;

public class WeldableItem extends Item {
    public WeldableItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
        CompoundTag tag= itemStack.getOrCreateTag();
        ListTag components = tag.getList("Components",8);
        if (!components.isEmpty()){
            list.add(Component.translatable("tooltip.factocrafty.item.components").withStyle(ChatFormatting.DARK_GRAY));
            components.forEach(i->{
                String l = i.getAsString();
                list.add(Registration.ITEMS_REGISTRAR.get(new ResourceLocation(l)).getName(null).copy().withStyle(ChatFormatting.GRAY));
            });
        }
    }
}
