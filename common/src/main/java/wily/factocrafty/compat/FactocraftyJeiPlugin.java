package wily.factocrafty.compat;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import net.minecraft.resources.ResourceLocation;
import wily.factocrafty.Factocrafty;
import wily.factocrafty.client.screens.FactocraftyMachineScreen;

@JeiPlugin
public class FactocraftyJeiPlugin  implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(Factocrafty.MOD_ID,"jei");
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGenericGuiContainerHandler(FactocraftyMachineScreen.class,new FactocraftyMachineGuiHandler());
    }
}
