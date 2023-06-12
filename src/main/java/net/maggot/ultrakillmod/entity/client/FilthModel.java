package net.maggot.ultrakillmod.entity.client;

import net.maggot.ultrakillmod.UltrakillHell;
import net.maggot.ultrakillmod.entity.custom.FilthEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class FilthModel extends GeoModel<FilthEntity> {
    @Override
    public ResourceLocation getModelResource(FilthEntity animatable) {
        return new ResourceLocation(UltrakillHell.MOD_ID, "geo/filth.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(FilthEntity animatable) {
        return new ResourceLocation(UltrakillHell.MOD_ID, "textures/entity/filth.png");
    }

    @Override
    public ResourceLocation getAnimationResource(FilthEntity animatable) {
        return new ResourceLocation(UltrakillHell.MOD_ID, "animations/filth.animation.json");
    }

    @Override
    public void setCustomAnimations(FilthEntity animatable, long instanceId, AnimationState<FilthEntity> animationState) {
        CoreGeoBone head = getAnimationProcessor().getBone("head");

        if (head != null) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

            head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
            head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
        }
    }
}
