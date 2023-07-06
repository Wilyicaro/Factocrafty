package wily.factocrafty.util.registering;

import net.minecraft.world.inventory.MenuType;
import wily.factocrafty.init.Registration;
import wily.factocrafty.inventory.FactocraftyProcessMenu;

public enum FactocraftyMenus {
    GEOTHERMAL_GENERATOR,GENERATOR,ELECTRIC_FURNACE,MACERATOR,COMPRESSOR,EXTRACTOR,REFINER,ENRICHER,GAS_INFUSER,SOLAR_PANEL,ENERGY_CELL,FLUID_TANK;

    public MenuType<?> get(){
        return Registration.getRegistrarMenuEntry(getName());
    }

    public static MenuType<FactocraftyProcessMenu> getMachine(FactocraftyMenus menu){
        return (MenuType<FactocraftyProcessMenu>) Registration.getRegistrarMenuEntry(menu.getName());
    }


    public String getName(){
        return name().toLowerCase();
    }
}
