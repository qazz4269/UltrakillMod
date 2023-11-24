package net.maggot.ultrakillmod.entity.custom;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;

import java.util.EnumSet;

public class StrayUltrakillEntity extends Monster implements GeoEntity {
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(StrayUltrakillEntity.class, EntityDataSerializers.BYTE);

    public StrayUltrakillEntity(EntityType<? extends StrayUltrakillEntity> entityType, Level level) {
        super(entityType, level);
        this.xpReward = 2;
    }

    public static AttributeSupplier setAttributes() {
        return Monster.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20D)
                .add(Attributes.ATTACK_DAMAGE, 3.0f)
                .add(Attributes.ATTACK_SPEED, 0.8f)
                .add(Attributes.MOVEMENT_SPEED, 0.4f)
                .add(Attributes.FOLLOW_RANGE, 48.0D).build();
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(1, new StrayUltrakillEntity.StrayUltrakillEntityAttackGoal(this));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 0, this::predicate));
        controllerRegistrar.add(new AnimationController<>(this, "attackController", 0, this::attackPredicate));
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

    static class StrayUltrakillEntityAttackGoal extends Goal {
        private final StrayUltrakillEntity strayUltrakillEntity;
        private int attackStep;
        private int attackTime;
        private int lastSeen;

        public StrayUltrakillEntityAttackGoal(StrayUltrakillEntity pStrayUltrakillEntity) {
            this.strayUltrakillEntity = pStrayUltrakillEntity;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        /**
         * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
         * method as well.
         */
        public boolean canUse() {
            LivingEntity livingentity = this.strayUltrakillEntity.getTarget();
            return livingentity != null && livingentity.isAlive() && this.strayUltrakillEntity.canAttack(livingentity);
        }

        /**
         * Execute a one shot task or start executing a continuous task
         */
        public void start() {
            this.attackStep = 0;
        }

        /**
         * Reset the task's internal state. Called when this task is interrupted by another one
         */
        public void stop() {
            this.strayUltrakillEntity.setCharged(false);
            this.lastSeen = 0;
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        /**
         * Keep ticking a continuous task that has already been started
         */
        public void tick() {
            --this.attackTime;
            LivingEntity livingentity = this.strayUltrakillEntity.getTarget();
            if (livingentity != null) {
                boolean flag = this.strayUltrakillEntity.getSensing().hasLineOfSight(livingentity);
                if (flag) {
                    this.lastSeen = 0;
                } else {
                    ++this.lastSeen;
                }

                double d0 = this.strayUltrakillEntity.distanceToSqr(livingentity);
                if (d0 < 4.0D) {
                    if (!flag) {
                        return;
                    }

                    if (this.attackTime <= 0) {
                        this.attackTime = 5;
                        this.strayUltrakillEntity.doHurtTarget(livingentity);
                    }

                    this.strayUltrakillEntity.getMoveControl().setWantedPosition(livingentity.getX(), livingentity.getY(), livingentity.getZ(), 1.0D);
                }
                else if (d0 < this.getFollowDistance() * this.getFollowDistance() && flag) {
                    double d1 = livingentity.getX() - this.strayUltrakillEntity.getX();
                    double d2 = livingentity.getY(0.5D) - this.strayUltrakillEntity.getY(0.5D);
                    double d3 = livingentity.getZ() - this.strayUltrakillEntity.getZ();
                    if (this.attackTime <= 0) {
                        ++this.attackStep;
                        if (this.attackStep == 1) {
                            this.attackTime = 5;
                            this.strayUltrakillEntity.setCharged(true);
                        }
                        else if (this.attackStep <= 4) {
                            this.attackTime = 5;
                        }
                        else {
                            this.attackTime = 5;
                            this.attackStep = 0;
                            this.strayUltrakillEntity.setCharged(false);
                        }

                        if (this.attackStep > 1) {
                            double d4 = Math.sqrt(Math.sqrt(d0)) * 0.1D;
                            if (!this.strayUltrakillEntity.isSilent()) {
                                this.strayUltrakillEntity.level.levelEvent(null, 1018, this.strayUltrakillEntity.blockPosition(), 0);
                            }

                            SmallFireball smallfireball = new SmallFireball(this.strayUltrakillEntity.level, this.strayUltrakillEntity, this.strayUltrakillEntity.getRandom().triangle(d1, 0.1D * d4), d2, this.strayUltrakillEntity.getRandom().triangle(d3, 0.1D * d4));
                            smallfireball.setPos(smallfireball.getX(), this.strayUltrakillEntity.getY(0.5D) + 0.5D, smallfireball.getZ());
                            this.strayUltrakillEntity.level.addFreshEntity(smallfireball);
                        }
                    }
                    this.strayUltrakillEntity.getLookControl().setLookAt(livingentity, 10.0F, 10.0F);
                }
                else if (this.lastSeen < 5) {
                    this.strayUltrakillEntity.getMoveControl().setWantedPosition(livingentity.getX(), livingentity.getY(), livingentity.getZ(), 1.0D);
                }

                super.tick();
            }
        }

        private double getFollowDistance() {
            return this.strayUltrakillEntity.getAttributeValue(Attributes.FOLLOW_RANGE);
        }
    }

    private <T extends GeoAnimatable> PlayState attackPredicate(AnimationState<T> tAnimationState) {
        if(this.swinging && tAnimationState.getController().getAnimationState().equals(AnimationController.State.STOPPED)) {
            tAnimationState.getController().forceAnimationReset();
            tAnimationState.getController().setAnimation(RawAnimation.begin().then("animation.stray_ultrakill.attack", Animation.LoopType.PLAY_ONCE));
            this.swinging = false;
        }
        return PlayState.CONTINUE;
    }

    protected boolean shouldDespawnInPeaceful() {
        return true;
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> tAnimationState) {
        if (tAnimationState.isMoving()) {
            tAnimationState.getController().setAnimation(RawAnimation.begin().then("animation.stray_ultrakill.walk", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }

        tAnimationState.getController().setAnimation(RawAnimation.begin().then("animation.stray_ultrakill.idle", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
