package net.maggot.ultrakillmod.entity.client;

import net.maggot.ultrakillmod.UltrakillHell;
import net.maggot.ultrakillmod.entity.custom.FilthEntity;
import net.maggot.ultrakillmod.entity.custom.StrayUltrakillEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class StrayUltrakillRenderer extends GeoEntityRenderer<StrayUltrakillEntity> {
    public StrayUltrakillRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new StrayUltrakillModel());
    }

    @Override
    public ResourceLocation getTextureLocation(StrayUltrakillEntity animatable) {
        return new ResourceLocation(UltrakillHell.MOD_ID, "textures/entity/stray_ultrakill.png");
    }

}
