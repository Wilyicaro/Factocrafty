package wily.factocrafty.item;

import dev.architectury.registry.menu.ExtendedMenuProvider;
import dev.architectury.registry.menu.MenuRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import wily.factocrafty.block.entity.FactocraftyLedBlockEntity;
import wily.factocrafty.init.Registration;
import wily.factocrafty.inventory.FactocraftyItemMenuContainer;
import wily.factoryapi.base.FactoryCapacityTiers;
import wily.factoryapi.base.TransportState;

public class RGBControllerItem extends EnergyItem {
    public RGBControllerItem(Properties properties) {
        super(FactoryCapacityTiers.BASIC, TransportState.INSERT, properties);
    }


    @Override
    public InteractionResult useOn(UseOnContext useOnContext) {
        BlockPos blockPos = useOnContext.getClickedPos();
        Level level = useOnContext.getPlayer().level();
        ItemStack stack = useOnContext.getItemInHand();
        if (level.getBlockEntity(blockPos) instanceof FactocraftyLedBlockEntity be && be.energyStorage.getEnergyStored() > 0 && getCraftyEnergy(useOnContext.getItemInHand()).getEnergyStored() > 0){
            if (level.isClientSide) return InteractionResult.SUCCESS;
            MenuRegistry.openExtendedMenu((ServerPlayer) useOnContext.getPlayer(),new ExtendedMenuProvider(){

                @Nullable
                @Override
                public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
                    return new FactocraftyItemMenuContainer(Registration.RGB_MENU.get(),i,player,blockPos);
                }

                @Override
                public Component getDisplayName() {
                    return getName(stack);
                }

                @Override
                public void saveExtraData(FriendlyByteBuf buf) {
                    buf.writeBlockPos(blockPos);
                }
            });
        }
        return super.useOn(useOnContext);
    }
}
