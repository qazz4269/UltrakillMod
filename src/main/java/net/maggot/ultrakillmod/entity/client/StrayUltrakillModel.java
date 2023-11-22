package net.maggot.ultrakillmod.entity.client;

import net.maggot.ultrakillmod.UltrakillHell;
import net.maggot.ultrakillmod.entity.custom.StrayUltrakillEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class StrayUltrakillModel extends GeoModel<StrayUltrakillEntity> {
    @Override
    public ResourceLocation getModelResource(StrayUltrakillEntity animatable) {
        return new ResourceLocation(UltrakillHell.MOD_ID, "geo/stray_ultrakill.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(StrayUltrakillEntity animatable) {
        return new ResourceLocation(UltrakillHell.MOD_ID, "textures/entity/stray_ultrakill.png");
    }

    @Override
    public ResourceLocation getAnimationResource(StrayUltrakillEntity animatable) {
        return new ResourceLocation(UltrakillHell.MOD_ID, "animations/stray_ultrakill.animation.json");
    }
    @Override
    public void setCustomAnimations(StrayUltrakillEntity animatable, long instanceId, AnimationState<StrayUltrakillEntity> animationState) {
        CoreGeoBone head = getAnimationProcessor().getBone("head");

        if (head != null) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

            head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
            head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
        }
    }
}
