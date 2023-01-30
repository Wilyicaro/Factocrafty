package wily.factocrafty.entity;

import dev.architectury.platform.Platform;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile
        ;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.FurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import wily.factocrafty.init.Registration;

import java.util.Optional;

public class LaserProjectile extends ThrowableProjectile {

    protected int life;
    public LaserProjectile(EntityType<LaserProjectile> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void tick() {
        life++;
        super.tick();
        if (life >= 7.5) this.remove(RemovalReason.KILLED);
    }

    @Override
    public void shoot(double d, double e, double f, float g, float h) {
        super.shoot(d, e, f, g, h);
        life = 0;
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    public LaserProjectile(Level level, LivingEntity livingEntity) {
        super(Registration.LASER.get(), livingEntity, level);

    }
    public static TagKey<Item> ore = getItemTag(new ResourceLocation(Platform.isForge() ? "forge" : "c", "ores"));


    private static TagKey<Item> getItemTag(ResourceLocation resourceLocation) {
        return TagKey.create(Registries.ITEM,resourceLocation);
    }
    @Override
    protected void onHitBlock(BlockHitResult blockHitResult) {
        super.onHitBlock(blockHitResult);
        BlockPos blockPos = blockHitResult.getBlockPos();
        BlockState blockState =  level.getBlockState(blockPos);

        if (blockState.is(BlockTags.DIRT) || blockState.is(BlockTags.BASE_STONE_OVERWORLD) || blockState.is(BlockTags.STONE_ORE_REPLACEABLES)|| blockState.is(BlockTags.SAND) || blockState.getBlock().asItem().builtInRegistryHolder().is(ore)){
            if (level.random.nextFloat() >= 0.25 )level.destroyBlock(blockHitResult.getBlockPos(),true,this);
            else {
                RecipeManager.CachedCheck<Container, ? extends AbstractCookingRecipe> quickCheck = RecipeManager.createCheck(RecipeType.SMELTING);
                Optional<? extends AbstractCookingRecipe> rcp = quickCheck.getRecipeFor(new SimpleContainer(new ItemStack(blockState.getBlock())), level);
                rcp.ifPresent(abstractCookingRecipe -> level.addFreshEntity(new ItemEntity(level, blockPos.getX(), blockPos.getY(), blockPos.getZ(), abstractCookingRecipe.getResultItem())));
                level.destroyBlock(blockHitResult.getBlockPos(), rcp.isEmpty(), this);

            }
        }
        else if(blockState.is(BlockTags.LEAVES) || blockState.is(BlockTags.REPLACEABLE_PLANTS) || blockState.is(BlockTags.LOGS_THAT_BURN)) {
        if (level.random.nextFloat() >= 0.5) level.destroyBlock(blockPos,true,this);
        else level.setBlock(blockPos, Blocks.FIRE.defaultBlockState(),3);
        }else{
                this.level.broadcastEntityEvent(this, (byte)3);
                this.discard();
            }


    }
    public void handleEntityEvent(byte b) {
        if (b == 3) {

            for(int i = 0; i < 8; ++i) {
                this.level.addParticle(ParticleTypes.SMOKE, this.getX(), this.getY(), this.getZ(), 0.0, 0.0, 0.0);
            }
        }

    }
    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        super.onHitEntity(entityHitResult);
        Entity entity = entityHitResult.getEntity();
        entity.hurt(DamageSource.thrown(this, this.getOwner()), 4.5F);
        entity.setSecondsOnFire(3);
        this.level.broadcastEntityEvent(this, (byte)3);

        if (level.random.nextFloat() >= 0.2) discard();
        else entity.setSecondsOnFire(3);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putShort("life", (short) this.life);
    }

    @Override
    public void load(CompoundTag compoundTag) {
        super.load(compoundTag);
        life = compoundTag.getShort("life");
    }

    @Override
    protected void defineSynchedData() {

    }
}
