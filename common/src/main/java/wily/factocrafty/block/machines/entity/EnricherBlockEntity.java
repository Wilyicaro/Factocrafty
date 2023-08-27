package wily.factocrafty.block.machines.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import wily.factocrafty.init.Registration;
import wily.factocrafty.util.registering.FactocraftyOre;
import wily.factocrafty.recipes.EnricherRecipe;
import wily.factocrafty.recipes.FactocraftyMachineRecipe;
import wily.factocrafty.tag.Items;
import wily.factoryapi.FactoryAPIPlatform;
import wily.factoryapi.base.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static wily.factocrafty.util.registering.FactocraftyOre.Material.ingCache;

public class EnricherBlockEntity extends ChangeableInputMachineBlockEntity {

    protected static int MATTER_SLOT = 3;

    public final Progress matterAmount;

    public Bearer<Integer> matterInt = Bearer.of(0);

    public EnricherBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(Registration.ENRICHER_MENU.get(), Registration.ENRICHER_RECIPE.get(),Registration.ENRICHER_BLOCK_ENTITY.get(), blockPos, blockState);
        resultTank = FactoryAPIPlatform.getFluidHandlerApi(getTankCapacity(), this, f -> true, SlotsIdentifier.OUTPUT, TransportState.EXTRACT);
        matterAmount = new Progress(Progress.Identifier.MATTER,1,800);
        additionalSyncInt.add(matterInt);
    }

    public NonNullList<FactoryItemSlot> getSlots(@Nullable Player player) {
        NonNullList<FactoryItemSlot> slots = super.getBasicSlots(player);
        slots.add(new FactoryItemSlot(inventory,SlotsIdentifier.INPUT,TransportState.INSERT, MATTER_SLOT, 56, 35){
            @Override
            public boolean mayPlace(ItemStack itemStack) {
                return (getItem().isEmpty() || ingCache.getUnchecked(getMatterMaterial()).test(itemStack)) && Arrays.stream(FactocraftyOre.Material.values()).anyMatch((m)-> m.getIngredient().test(itemStack));
            }
        });
        return slots;
    }
    protected int getMatterSpace(){
        return matterAmount.first().maxProgress - matterAmount.first().get();
    }
    public static int getMatterValue( ItemStack stack){
        for (TagKey<Item> i : customMatterValues.keySet()) {
            if (stack.is(i)) return customMatterValues.get(i);
        }
        return 10;
    }
    public FactocraftyOre.Material getMatterMaterial(){
        return FactocraftyOre.Material.values()[matterInt.get()];
    }
    public static Map<TagKey<Item>,Integer> customMatterValues = new HashMap<>(Map.of(Items.PLATES,15, Items.STORAGE_BLOCKS,90,Items.NUGGETS,1));


    @Override
    public void tick() {
        super.tick();
        if (!level.isClientSide){
            ItemStack matter = inventory.getItem(MATTER_SLOT);
            if (getMatterSpace() != 0 && (!matter.isEmpty())){
                FactocraftyOre.Material m = FactocraftyOre.Material.findItemMaterial(matter);
                if ((getMatterMaterial().isEmpty() || getMatterMaterial() == m)){
                int i = getMatterValue(matter);
                if(getMatterMaterial().isEmpty()) {
                    matterInt.set(m.ordinal());
                }
                matterAmount.first().add(inventory.extractItem(MATTER_SLOT,getMatterSpace() / i ,false).getCount() * i);
                }
            }
            if (matterAmount.first().get() == 0 && !getMatterMaterial().isEmpty()) matterInt.set(0);
        }
    }

    @Override
    public void addTanks(List<IPlatformFluidHandler<?>> list) {
        super.addTanks(list);
        list.add(resultTank);
    }

    @Override
    public List<Progress> getProgresses() {
        List<Progress> progresses = super.getProgresses();
        progresses.add(matterAmount);
        return progresses;
    }

    @Override
    protected SoundEvent getMachineSound() {
        return Registration.MACERATOR_ACTIVE.get();
    }

    @Override
    protected void processResults(FactocraftyMachineRecipe recipe) {
        super.processResults(recipe);
        if (recipe instanceof EnricherRecipe rcp){
            if (rcp.hasFluidResult() && !rcp.getResultFluid().isEmpty()) resultTank.fill(rcp.getResultFluid(),false);
            matterAmount.first().shrink(rcp.getMatter().second);
        }
    }

    @Override
    protected boolean canMachineProcess(@Nullable Recipe<?> recipe) {
        return super.canMachineProcess(recipe) && recipe instanceof EnricherRecipe r && r.matchesMatter(getMatterMaterial(),matterAmount);
    }
}
