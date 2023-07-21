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
import wily.factocrafty.util.registering.FactocraftyMenus;
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
        super(FactocraftyMenus.ENRICHER, Registration.ENRICHER_RECIPE.get(),Registration.ENRICHER_BLOCK_ENTITY.get(), blockPos, blockState);
        inputType = InputType.FLUID;
        resultTank = FactoryAPIPlatform.getFluidHandlerApi(getTankCapacity(), this, f -> true, SlotsIdentifier.OUTPUT, TransportState.EXTRACT);
        matterAmount = new Progress(Progress.Identifier.MATTER,1,800);
        additionalSyncInt.add(matterInt);
    }

    public void addSlots(NonNullList<FactoryItemSlot> slots, @Nullable Player player) {
        addBasicSlots(slots,player);
        slots.add(new FactoryItemSlot(inventory,SlotsIdentifier.INPUT,TransportState.INSERT, MATTER_SLOT, 56, 35){
            @Override
            public boolean mayPlace(ItemStack itemStack) {
                return (getItem().isEmpty() || ingCache.getUnchecked(getMatterMaterial()).test(itemStack)) && Arrays.stream(FactocraftyOre.Material.values()).anyMatch((m)-> m.getIngredient().test(itemStack));
            }
        });
    }
    protected int getMatterSpace(){
        return matterAmount.maxProgress - matterAmount.getInt(0);
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
                matterAmount.setInt(0,matterAmount.getInt(0) + inventory.extractItem(MATTER_SLOT,getMatterSpace() / i ,false).getCount() * i);
                }
            }
            if (matterAmount.getInt(0) == 0 && !getMatterMaterial().isEmpty()) matterInt.set(0);
        }
    }

    @Override
    public void addTanks(List<IPlatformFluidHandler> list) {
        super.addTanks(list);
        list.add(resultTank);
    }

    @Override
    public void addProgresses(List<Progress> list) {
        super.addProgresses(list);
        list.add(matterAmount);
    }

    @Override
    protected SoundEvent getMachineSound() {
        return Registration.MACERATOR_ACTIVE.get();
    }

    @Override
    protected void setOtherResults(FactocraftyMachineRecipe recipe, IPlatformItemHandler inv, int i) {
        super.setOtherResults(recipe,inv,i);
        if (recipe instanceof EnricherRecipe rcp){
            if (rcp.hasFluidResult() && !rcp.getResultFluid().isEmpty()) resultTank.fill(rcp.getResultFluid(),false);
            matterAmount.setInt(0, matterAmount.getInt(0) - rcp.getMatter().second);
        }

    }

    @Override
    protected boolean canMachineProcess(@Nullable Recipe<?> recipe) {
        return super.canMachineProcess(recipe) && recipe instanceof EnricherRecipe r && r.matchesMatter(getMatterMaterial(),matterAmount);
    }
}
