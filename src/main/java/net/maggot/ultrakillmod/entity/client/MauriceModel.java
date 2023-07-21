package net.maggot.ultrakillmod.entity.client;

import net.maggot.ultrakillmod.UltrakillHell;
import net.maggot.ultrakillmod.entity.custom.MauriceEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class MauriceModel extends GeoModel<MauriceEntity> {

    @Override
    public ResourceLocation getModelResource(MauriceEntity animatable) {
        return new ResourceLocation(UltrakillHell.MOD_ID, "geo/maurice.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(MauriceEntity animatable) {
        return new ResourceLocation(UltrakillHell.MOD_ID, "textures/entity/maurice.png");
    }

    @Override
    public ResourceLocation getAnimationResource(MauriceEntity animatable) {
        return new ResourceLocation(UltrakillHell.MOD_ID, "animations/maurice.animation.json");
    }
    @Override
    public void setCustomAnimations(MauriceEntity animatable, long instanceId, AnimationState<MauriceEntity> animationState) {
        CoreGeoBone head = getAnimationProcessor().getBone("head");

        if (head != null) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

            head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
            head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
        }
    }
}
