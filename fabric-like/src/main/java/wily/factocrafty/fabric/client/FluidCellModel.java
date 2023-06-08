package wily.factocrafty.fabric.client;

import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import wily.factocrafty.Factocrafty;

public class FluidCellModel extends DynamicFluidHandlerModel {


    @Override
    public ModelResourceLocation getBaseModel() {
        return new ModelResourceLocation(new ResourceLocation(Factocrafty.MOD_ID,"fluid_cell_base"),"inventory");
    }

    @Override
    public ModelResourceLocation getFluidModel() {
        return new ModelResourceLocation(new ResourceLocation(Factocrafty.MOD_ID, "fluid_model"),"inventory");
    }
}
