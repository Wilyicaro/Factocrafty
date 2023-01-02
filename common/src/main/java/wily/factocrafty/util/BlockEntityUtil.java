package wily.factocrafty.util;

import com.mojang.datafixers.types.Type;
import net.minecraft.Util;
import net.minecraft.util.datafix.fixes.References;

public class BlockEntityUtil {
    public static Type<?> blockEntityType(String name){
        return Util.fetchChoiceType(References.BLOCK_ENTITY, name);
    }
}
