package net.maggot.ultrakillmod.entity.custom;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;

import java.util.EnumSet;

public class MauriceEntity extends FlyingMob implements GeoEntity{
    private static final EntityDataAccessor<Boolean> DATA_IS_CHARGING = SynchedEntityData.defineId(MauriceEntity.class, EntityDataSerializers.BOOLEAN);
    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private int explosionPower = 1;

    public MauriceEntity(EntityType<? extends MauriceEntity> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier setAttributes() {
        return FlyingMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 100D)
                .add(Attributes.ATTACK_DAMAGE, 1.0f)
                .add(Attributes.ATTACK_SPEED, 100.0f)
                .add(Attributes.FLYING_SPEED, 0.6f).build();
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(5, new MauriceEntity.RandomFloatAroundGoal(this));
        this.goalSelector.addGoal(7, new MauriceEntity.MauriceEntityLookGoal(this));
        this.goalSelector.addGoal(7, new MauriceEntity.MauriceEntityShootFireballGoal(this));

        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> tAnimationState) {
        if (tAnimationState.isMoving()) {
            tAnimationState.getController().setAnimation(RawAnimation.begin().then("animation.maurice.walk", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }

        tAnimationState.getController().setAnimation(RawAnimation.begin().then("animation.maurice.idle", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
// RandomFloatAroundGoal
    static class RandomFloatAroundGoal extends Goal {
        private final MauriceEntity mauriceEntity;

        public RandomFloatAroundGoal(MauriceEntity pMauriceEntity) {
            this.mauriceEntity = pMauriceEntity;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        public boolean canUse() {
            MoveControl movecontrol = this.mauriceEntity.getMoveControl();
            if (!movecontrol.hasWanted()) {
                return true;
            } else {
                double d0 = movecontrol.getWantedX() - this.mauriceEntity.getX();
                double d1 = movecontrol.getWantedY() - this.mauriceEntity.getY();
                double d2 = movecontrol.getWantedZ() - this.mauriceEntity.getZ();
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                return d3 < 1.0D || d3 > 3600.0D;
            }
        }

        public boolean canContinueToUse() {
            return false;
        }

        public void start() {
            RandomSource randomsource = this.mauriceEntity.getRandom();
            double d0 = this.mauriceEntity.getX() + (double)((randomsource.nextFloat() * 2.0F - 1.0F) * 16.0F);
            double d1 = this.mauriceEntity.getY() + (double)((randomsource.nextFloat() * 2.0F - 1.0F) * 16.0F);
            double d2 = this.mauriceEntity.getZ() + (double)((randomsource.nextFloat() * 2.0F - 1.0F) * 16.0F);
            this.mauriceEntity.getMoveControl().setWantedPosition(d0, d1, d2, 1.0D);
        }
    }
//GhostLookGoal
    static class MauriceEntityLookGoal extends Goal {
        private final MauriceEntity mauriceEntity;

        public MauriceEntityLookGoal(MauriceEntity pMauriceEntity) {
            this.mauriceEntity = pMauriceEntity;
            this.setFlags(EnumSet.of(Goal.Flag.LOOK));
        }

        public boolean canUse() {
            return true;
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            if (this.mauriceEntity.getTarget() == null) {
                Vec3 vec3 = this.mauriceEntity.getDeltaMovement();
                this.mauriceEntity.setYRot(-((float) Mth.atan2(vec3.x, vec3.z)) * (180F / (float)Math.PI));
                this.mauriceEntity.yBodyRot = this.mauriceEntity.getYRot();
            } else {
                LivingEntity livingentity = this.mauriceEntity.getTarget();
                double d0 = 64.0D;
                if (livingentity.distanceToSqr(this.mauriceEntity) < 4096.0D) {
                    double d1 = livingentity.getX() - this.mauriceEntity.getX();
                    double d2 = livingentity.getZ() - this.mauriceEntity.getZ();
                    this.mauriceEntity.setYRot(-((float)Mth.atan2(d1, d2)) * (180F / (float)Math.PI));
                    this.mauriceEntity.yBodyRot = this.mauriceEntity.getYRot();
                }
            }

        }
    }
//GhostShootFireballGoal
    static class MauriceEntityShootFireballGoal extends Goal {
        private final MauriceEntity mauriceEntity;
        public int chargeTime;

        public MauriceEntityShootFireballGoal(MauriceEntity pMauriceEntity) {
            this.mauriceEntity = pMauriceEntity;
        }

        public boolean canUse() {
            return this.mauriceEntity.getTarget() != null;
        }


        public void start() {
            this.chargeTime = 0;
        }

        public void stop() {
            this.mauriceEntity.setCharging(false);
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            LivingEntity livingentity = this.mauriceEntity.getTarget();
            if (livingentity != null) {
                double d0 = 64.0D;
                if (livingentity.distanceToSqr(this.mauriceEntity) < 4096.0D && this.mauriceEntity.hasLineOfSight(livingentity)) {
                    Level level = this.mauriceEntity.level;
                    ++this.chargeTime;
                    if (this.chargeTime == 10 && !this.mauriceEntity.isSilent()) {
                        level.levelEvent((Player)null, 1015, this.mauriceEntity.blockPosition(), 0);
                    }

                    if (this.chargeTime == 20) {
                        double d1 = 4.0D;
                        Vec3 vec3 = this.mauriceEntity.getViewVector(1.0F);
                        double d2 = livingentity.getX() - (this.mauriceEntity.getX() + vec3.x * 4.0D);
                        double d3 = livingentity.getY(0.5D) - (0.5D + this.mauriceEntity.getY(0.5D));
                        double d4 = livingentity.getZ() - (this.mauriceEntity.getZ() + vec3.z * 4.0D);
                        if (!this.mauriceEntity.isSilent()) {
                            level.levelEvent((Player)null, 1016, this.mauriceEntity.blockPosition(), 0);
                        }

                        LargeFireball largefireball = new LargeFireball(level, this.mauriceEntity, d2, d3, d4, this.mauriceEntity.getExplosionPower());
                        largefireball.setPos(this.mauriceEntity.getX() + vec3.x * 4.0D, this.mauriceEntity.getY(0.5D) + 0.5D, largefireball.getZ() + vec3.z * 4.0D);
                        level.addFreshEntity(largefireball);
                        this.chargeTime = -40;
                    }
                } else if (this.chargeTime > 0) {
                    --this.chargeTime;
                }

                this.mauriceEntity.setCharging(this.chargeTime > 10);
            }
        }
    }
    public void setCharging(boolean pCharging) {
        this.entityData.set(DATA_IS_CHARGING, pCharging);
    }

    public int getExplosionPower() {
        return this.explosionPower;
    }
}
