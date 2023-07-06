package wily.factocrafty.util.registering;

import java.util.function.Supplier;

public interface IFactocraftyLazyRegistry<T> extends Supplier<T> {

    T get();


    default String getName(){
        return this instanceof Enum<?> e ? e.name().toLowerCase() :  "";
    }

}
