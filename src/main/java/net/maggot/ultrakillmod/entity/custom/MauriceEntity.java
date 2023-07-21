package net.maggot.ultrakillmod.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;


import java.util.EnumSet;

public class MauriceEntity extends FlyingMob implements GeoEntity {
    private static final EntityDataAccessor<Boolean> DATA_IS_CHARGING = SynchedEntityData.defineId(Ghast.class, EntityDataSerializers.BOOLEAN);
    private int explosionPower = 1;

    protected MauriceEntity(EntityType<? extends FlyingMob> p_20806_, Level p_20807_) {
        super(p_20806_, p_20807_);
        this.xpReward = 5;
        this.moveControl = new MauriceEntity.GhastMoveControl(this);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(5, new MauriceEntity.RandomFloatAroundGoal(this));
        this.goalSelector.addGoal(7, new MauriceEntity.GhastLookGoal(this));
        this.goalSelector.addGoal(7, new MauriceEntity.GhastShootFireballGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, (p_275993_) -> {
            return Math.abs(p_275993_.getY() - this.getY()) <= 4.0D;
        }));
    }

    public boolean isCharging() {
        return this.entityData.get(DATA_IS_CHARGING);
    }

    public void setCharging(boolean p_32759_) {
        this.entityData.set(DATA_IS_CHARGING, p_32759_);
    }

    public int getExplosionPower() {
        return this.explosionPower;
    }

    protected boolean shouldDespawnInPeaceful() {
        return true;
    }

    private static boolean isReflectedFireball(DamageSource p_238408_) {
        return p_238408_.getDirectEntity() instanceof LargeFireball && p_238408_.getEntity() instanceof Player;
    }

    public boolean isInvulnerableTo(DamageSource p_238289_) {
        return !isReflectedFireball(p_238289_) && super.isInvulnerableTo(p_238289_);
    }

    public boolean hurt(DamageSource p_32730_, float p_32731_) {
        if (isReflectedFireball(p_32730_)) {
            super.hurt(p_32730_, 1000.0F);
            return true;
        } else {
            return this.isInvulnerableTo(p_32730_) ? false : super.hurt(p_32730_, p_32731_);
        }
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_IS_CHARGING, false);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 10.0D).add(Attributes.FOLLOW_RANGE, 100.0D);
    }

    public SoundSource getSoundSource() {
        return SoundSource.HOSTILE;
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.GHAST_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource p_32750_) {
        return SoundEvents.GHAST_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.GHAST_DEATH;
    }

    protected float getSoundVolume() {
        return 5.0F;
    }

    public int getMaxSpawnClusterSize() {
        return 1;
    }

    public void addAdditionalSaveData(CompoundTag p_32744_) {
        super.addAdditionalSaveData(p_32744_);
        p_32744_.putByte("ExplosionPower", (byte)this.explosionPower);
    }

    public void readAdditionalSaveData(CompoundTag p_32733_) {
        super.readAdditionalSaveData(p_32733_);
        if (p_32733_.contains("ExplosionPower", 99)) {
            this.explosionPower = p_32733_.getByte("ExplosionPower");
        }

    }

    protected float getStandingEyeHeight(Pose p_32741_, EntityDimensions p_32742_) {
        return 2.6F;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return null;
    }

    static class GhastLookGoal extends Goal {
        private final MauriceEntity mauriceEntity;

        public GhastLookGoal(MauriceEntity p_32762_) {
            this.mauriceEntity = p_32762_;
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

    static class GhastMoveControl extends MoveControl {
        private final MauriceEntity mauriceEntity;
        private int MauriceEntity;
        private int floatDuration;

        public GhastMoveControl(MauriceEntity p_32768_) {
            super(p_32768_);
            this.mauriceEntity = p_32768_;
        }

        public void tick() {
            if (this.operation == MoveControl.Operation.MOVE_TO) {
                if (this.floatDuration-- <= 0) {
                    this.floatDuration += this.mauriceEntity.getRandom().nextInt(5) + 2;
                    Vec3 vec3 = new Vec3(this.wantedX - this.mauriceEntity.getX(), this.wantedY - this.mauriceEntity.getY(), this.wantedZ - this.mauriceEntity.getZ());
                    double d0 = vec3.length();
                    vec3 = vec3.normalize();
                    if (this.canReach(vec3, Mth.ceil(d0))) {
                        this.mauriceEntity.setDeltaMovement(this.mauriceEntity.getDeltaMovement().add(vec3.scale(0.1D)));
                    } else {
                        this.operation = MoveControl.Operation.WAIT;
                    }
                }

            }
        }

        private boolean canReach(Vec3 p_32771_, int p_32772_) {
            AABB aabb = this.mauriceEntity.getBoundingBox();

            for(int i = 1; i < p_32772_; ++i) {
                aabb = aabb.move(p_32771_);
                if (!this.mauriceEntity.level.noCollision(this.mauriceEntity, aabb)) {
                    return false;
                }
            }

            return true;
        }
    }

    static class GhastShootFireballGoal extends Goal {
        private final MauriceEntity mauriceEntity;
        public int chargeTime;

        public GhastShootFireballGoal(MauriceEntity p_32776_) {
            this.mauriceEntity = p_32776_;
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

    static class RandomFloatAroundGoal extends Goal {
        private final MauriceEntity mauriceEntity;

        public RandomFloatAroundGoal(MauriceEntity p_32783_) {
            this.mauriceEntity = p_32783_;
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
}
