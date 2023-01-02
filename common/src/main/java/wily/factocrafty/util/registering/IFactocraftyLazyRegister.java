package wily.factocrafty.util.registering;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.function.Consumer;

public interface IFactocraftyLazyRegister<T> {

    T get();


    default String getName(){
        return this instanceof Enum<?> e ? e.name().toLowerCase() :  "";
    }

}
