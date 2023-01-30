package wily.factocrafty.util;


import com.mojang.math.Axis;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;

public class DirectionUtil {
    public static Direction nearestRotation(float rotateX, float rotateY, boolean invertYAxis){
        Entity entity = new ItemEntity(EntityType.ITEM,null);
        entity.setXRot(rotateX);
        entity.setYRot(rotateY);
        Direction d =Direction.orderedByNearest(entity)[0];
        return Direction.Plane.VERTICAL.test(d) && invertYAxis ? d.getOpposite() : d;
    }

    public static float rotationCyclic(float rotation){
        if (rotation> 180) return rotation - 360;
        else if ( rotation < 180) return 360 + rotation;
        return rotation;
    }
    public static float unCyclicRotation(float rotation){
        return rotation < 0 ? 360 + rotation : rotation;
    }

    public static Quaternionf getRotation(Direction direction) {
        switch (direction) {
            case DOWN -> {return Axis.XP.rotationDegrees(180.0F);}
            case UP -> {return new Quaternionf();
            }
            case NORTH -> {
                return Axis.XP.rotationDegrees(-90.0F);

            }
            case SOUTH -> {
                return Axis.XP.rotationDegrees(90.0F);
            }
            case WEST -> {
                return Axis.ZP.rotationDegrees(90.0F);

            }
            case EAST -> {
               return Axis.ZP.rotationDegrees(-90.0F);

            }
            default -> throw new IncompatibleClassChangeError();
        }
    }
    public static Quaternionf getRotationByInitial(Direction initial, Direction direction) {
        Direction.Axis axis1 = initial.getAxis() == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X;
        Direction.Axis axis2 = initial.getAxis() == Direction.Axis.Y ? Direction.Axis.Z : Direction.Axis.Y;
        if (direction == initial.getOpposite()) {return Axis.XP.rotationDegrees(180.0F);}
        else if(direction == initial) return new Quaternionf();
        else if (direction == initial.getClockWise(Direction.Axis.X)) return Axis.XP.rotationDegrees(-90.0F);
        else if (direction == initial.getCounterClockWise(Direction.Axis.X)) return Axis.XP.rotationDegrees(90.0F);
        if (direction == initial.getCounterClockWise(Direction.Axis.Z)) return Axis.ZP.rotationDegrees(90.0F);
        if (direction == initial.getClockWise(Direction.Axis.Z)) return Axis.ZP.rotationDegrees(-90.0F);
        return new Quaternionf();
    }
    public static Quaternionf getHorizontalRotation(Direction direction) {
        switch (direction) {
            case NORTH -> {
                return new Quaternionf();

            }
            case SOUTH -> {
                return Axis.YP.rotationDegrees(180.0F);
            }
            case WEST -> {
                return Axis.YP.rotationDegrees(90.0F);

            }
            case EAST -> {
                return Axis.YP.rotationDegrees(-90.0F);

            }
            default -> throw new IncompatibleClassChangeError();
        }
    }
}
