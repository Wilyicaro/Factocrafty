package wily.factocrafty.item;

import java.util.Locale;

public enum UpgradeType {
    OVERCLOCK,ENERGY,EXPERIENCE,TRANSFORMER;

    public String getName(){
        return name().toLowerCase(Locale.ROOT);
    }
}
