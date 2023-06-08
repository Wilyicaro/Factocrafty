package wily.factocrafty.util.registering;

public interface IFactocraftyLazyRegistry<T> {

    T get();


    default String getName(){
        return this instanceof Enum<?> e ? e.name().toLowerCase() :  "";
    }

}
