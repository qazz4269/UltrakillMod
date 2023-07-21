package net.maggot.ultrakillmod.entity.client;

import net.maggot.ultrakillmod.UltrakillHell;
import net.maggot.ultrakillmod.entity.custom.MauriceEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class MauriceRenderer extends GeoEntityRenderer<MauriceEntity> {
    public MauriceRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new MauriceModel());
    }

    @Override
    public ResourceLocation getTextureLocation(MauriceEntity animatable) {
        return new ResourceLocation(UltrakillHell.MOD_ID, "textures/entity/maurice.png");
    }
}
