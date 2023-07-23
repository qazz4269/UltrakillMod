package net.maggot.ultrakillmod.entity.custom;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import java.util.EnumSet;

public class MauriceEntity extends FlyingMob implements Enemy, GeoEntity {
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(MauriceEntity.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Boolean> DATA_IS_CHARGING = SynchedEntityData.defineId(MauriceEntity.class, EntityDataSerializers.BOOLEAN);
    private int explosionPower = 1;

    public MauriceEntity(EntityType<? extends MauriceEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.xpReward = 5;
        this.moveControl = new MauriceEntity.MauriceEntityMoveControl(this);
    }

    public static AttributeSupplier setAttributes() {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20D)
                .add(Attributes.ATTACK_DAMAGE, 3.0f)
                .add(Attributes.ATTACK_SPEED, 0.8f)
                .add(Attributes.MOVEMENT_SPEED, 0.4f).build();
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(5, new MauriceEntity.RandomFloatAroundGoal(this));
        this.goalSelector.addGoal(7, new MauriceEntity.MauriceEntityLookGoal(this));
        this.goalSelector.addGoal(7, new MauriceEntity.MauriceEntityShootFireballGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, (p_275993_) -> {
            return Math.abs(p_275993_.getY() - this.getY()) <= 4.0D;
        }));
    }

    protected boolean shouldDespawnInPeaceful() {
        return true;
    }

    private static boolean isReflectedFireball(DamageSource p_238408_) {
        return p_238408_.getDirectEntity() instanceof LargeFireball && p_238408_.getEntity() instanceof Player;
    }

    public boolean isInvulnerableTo(DamageSource pSource) {
        return !isReflectedFireball(pSource) && super.isInvulnerableTo(pSource);
    }

    public boolean hurt(DamageSource pSource, float pAmount) {
        if (isReflectedFireball(pSource)) {
            super.hurt(pSource, 1000.0F);
            return true;
        } else {
            return this.isInvulnerableTo(pSource) ? false : super.hurt(pSource, pAmount);
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

    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
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

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putByte("ExplosionPower", (byte)this.explosionPower);
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("ExplosionPower", 99)) {
            this.explosionPower = pCompound.getByte("ExplosionPower");
        }

    }

    protected float getStandingEyeHeight(Pose pPose, EntityDimensions pSize) {
        return 2.6F;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

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

    static class MauriceEntityMoveControl extends MoveControl {
        private final MauriceEntity mauriceEntity;
        private int floatDuration;

        public MauriceEntityMoveControl(MauriceEntity pMauriceEntity) {
            super(pMauriceEntity);
            this.mauriceEntity = pMauriceEntity;
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

        private boolean canReach(Vec3 pPos, int pLength) {
            AABB aabb = this.mauriceEntity.getBoundingBox();

            for(int i = 1; i < pLength; ++i) {
                aabb = aabb.move(pPos);
                if (!this.mauriceEntity.level.noCollision(this.mauriceEntity, aabb)) {
                    return false;
                }
            }

            return true;
        }
    }

    void setCharged(boolean pCharged) {
        byte b0 = this.entityData.get(DATA_FLAGS_ID);
        if (pCharged) {
            b0 = (byte)(b0 | 1);
        } else {
            b0 = (byte)(b0 & -2);
        }

        this.entityData.set(DATA_FLAGS_ID, b0);
    }

    static class MauriceEntityShootFireballGoal extends Goal {
        private final MauriceEntity mauriceEntity;
        private int attackStep;
        private int attackTime;
        private int lastSeen;

        public MauriceEntityShootFireballGoal(MauriceEntity pMauriceEntity) {
            this.mauriceEntity = pMauriceEntity;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        public boolean canUse() {
            LivingEntity livingentity = this.mauriceEntity.getTarget();
            return livingentity != null && livingentity.isAlive() && this.mauriceEntity.canAttack(livingentity);
        }

        public void start() {
            this.attackStep = 0;
        }

        public void stop() {
            this.mauriceEntity.setCharged(false);
            this.lastSeen = 0;
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            --this.attackTime;
            LivingEntity livingentity = this.mauriceEntity.getTarget();
            if (livingentity != null) {
                boolean flag = this.mauriceEntity.getSensing().hasLineOfSight(livingentity);
                if (flag) {
                    this.lastSeen = 0;
                } else {
                    ++this.lastSeen;
                }

                double d0 = this.mauriceEntity.distanceToSqr(livingentity);
                if (d0 < 4.0D) {
                    if (!flag) {
                        return;
                    }

                    if (this.attackTime <= 0) {
                        this.attackTime = 20;
                        this.mauriceEntity.doHurtTarget(livingentity);
                    }

                    this.mauriceEntity.getMoveControl().setWantedPosition(livingentity.getX(), livingentity.getY(), livingentity.getZ(), 1.0D);
                } else if (d0 < this.getFollowDistance() * this.getFollowDistance() && flag) {
                    double d1 = livingentity.getX() - this.mauriceEntity.getX();
                    double d2 = livingentity.getY(0.5D) - this.mauriceEntity.getY(0.5D);
                    double d3 = livingentity.getZ() - this.mauriceEntity.getZ();
                    if (this.attackTime <= 0) {
                        ++this.attackStep;
                        if (this.attackStep == 1) {
                            this.attackTime = 60;
                            this.mauriceEntity.setCharged(true);
                        } else if (this.attackStep <= 4) {
                            this.attackTime = 6;
                        } else {
                            this.attackTime = 100;
                            this.attackStep = 0;
                            this.mauriceEntity.setCharged(false);
                        }

                        if (this.attackStep > 1) {
                            double d4 = Math.sqrt(Math.sqrt(d0)) * 0.5D;
                            if (!this.mauriceEntity.isSilent()) {
                                this.mauriceEntity.level.levelEvent((Player)null, 1018, this.mauriceEntity.blockPosition(), 0);
                            }

                            for(int i = 0; i < 1; ++i) {
                                SmallFireball smallfireball = new SmallFireball(this.mauriceEntity.level, this.mauriceEntity, this.mauriceEntity.getRandom().triangle(d1, 2.297D * d4), d2, this.mauriceEntity.getRandom().triangle(d3, 2.297D * d4));
                                smallfireball.setPos(smallfireball.getX(), this.mauriceEntity.getY(0.5D) + 0.5D, smallfireball.getZ());
                                this.mauriceEntity.level.addFreshEntity(smallfireball);
                            }
                        }
                    }

                    this.mauriceEntity.getLookControl().setLookAt(livingentity, 10.0F, 10.0F);
                } else if (this.lastSeen < 5) {
                    this.mauriceEntity.getMoveControl().setWantedPosition(livingentity.getX(), livingentity.getY(), livingentity.getZ(), 1.0D);
                }

                super.tick();
            }
        }
        private double getFollowDistance() {
            return this.mauriceEntity.getAttributeValue(Attributes.FOLLOW_RANGE);
        }
    }

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
}