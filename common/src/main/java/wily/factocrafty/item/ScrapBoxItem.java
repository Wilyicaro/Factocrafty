package wily.factocrafty.item;


import it.unimi.dsi.fastutil.Pair;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import wily.factocrafty.util.registering.FactocraftyItems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class ScrapBoxItem extends Item {
    public static final List<Pair<Supplier<ItemStack>,Float>> SCRAP_ITEMS = new ArrayList<>();
    public ScrapBoxItem(Properties properties) {
        super(properties);
        addScrapItem(Items.DIAMOND,0.01F);
        addScrapItemStack(new ItemStack(Items.EMERALD),0.012F);
        addScrapItemStack(new ItemStack(Items.REDSTONE),0.2F);
        addScrapItemStack(new ItemStack(Items.COAL),0.35F);
        addScrapItemStack(new ItemStack(Items.LAPIS_LAZULI),0.25F);
        addScrapItemStack(new ItemStack(Items.IRON_INGOT),0.08F);
        addScrapItemStack(new ItemStack(Items.RAW_IRON),0.09F);
        addScrapItemStack(new ItemStack(Items.COPPER_INGOT),0.12F);
        addScrapItemStack(new ItemStack(Items.RAW_COPPER),0.13F);
        addScrapItemStack(new ItemStack(Items.GOLD_INGOT),0.04F);
        addScrapItemStack(new ItemStack(Items.RAW_GOLD),0.05F);
        addScrapItem(FactocraftyItems.IRIDIUM::get,0.001F);
        addScrapItemStack(new ItemStack(Items.STICK),0.4F);
        addScrapItemStack(new ItemStack(Items.DIRT),0.6F);
        addScrapItemStack(new ItemStack(Items.DIRT,3),0.4F);
        addScrapItemStack(new ItemStack(Items.STICK,3),0.35F);
        addScrapItemStack(new ItemStack(Items.WOODEN_AXE),0.15F);
        addScrapItemStack(new ItemStack(Items.WOODEN_PICKAXE),0.15F);
        addScrapItemStack(new ItemStack(Items.WOODEN_SHOVEL),0.25F);
        addScrapItemStack(new ItemStack(Items.WOODEN_SWORD),0.2F);
        addScrapItemStack(new ItemStack(Items.WOODEN_HOE),0.2F);
        addScrapItemStack(new ItemStack(Items.OAK_PLANKS),0.3F);
        addScrapItemStack(new ItemStack(Items.OAK_PLANKS,2),0.15F);
        addScrapItemStack(new ItemStack(Items.OAK_BUTTON),0.3F);
        addScrapItemStack(new ItemStack(Items.OAK_PRESSURE_PLATE),0.2F);
        addScrapItemStack(new ItemStack(Items.OAK_LOG),0.1F);
    }
    public static void addScrapItem(Supplier<Item> item, float chance){
        addScrapItemStack(()->new ItemStack(item.get()),chance);
    }
    public static void addScrapItem(Item item, float chance){
        addScrapItem(()->item,chance);
    }
    public static void addScrapItemStack(ItemStack stack, float chance){
        addScrapItemStack(()->stack,chance);
    }
    public static void addScrapItemStack(Supplier<ItemStack> stack, float chance){
        SCRAP_ITEMS.add(Pair.of(stack,chance));
    }
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack hand = player.getItemInHand(interactionHand);
        if (hand.getItem() instanceof ScrapBoxItem && player instanceof ServerPlayer sp){
            ItemStack drop = ItemStack.EMPTY;

            Pair<Supplier<ItemStack>, Float> entry =  SCRAP_ITEMS.get(level.random.nextInt(0,SCRAP_ITEMS.size() - 1));
            if (level.random.nextFloat() <= entry.second()) {
                drop = entry.first().get();
            }

            if (!drop.isEmpty()){
                sp.spawnAtLocation(drop,sp.getEyeHeight());
            }
            hand.shrink(1);
        }
        return  InteractionResultHolder.sidedSuccess(hand,level.isClientSide());
    }
}
