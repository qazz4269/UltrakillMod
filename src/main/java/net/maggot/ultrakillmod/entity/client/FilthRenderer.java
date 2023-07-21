package net.maggot.ultrakillmod.entity.client;

import net.maggot.ultrakillmod.UltrakillHell;
import net.maggot.ultrakillmod.entity.custom.FilthEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class FilthRenderer extends GeoEntityRenderer<FilthEntity> {
    public FilthRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new FilthModel());
    }

    @Override
    public ResourceLocation getTextureLocation(FilthEntity animatable) {
        return new ResourceLocation(UltrakillHell.MOD_ID, "textures/entity/filth.png");
    }

}
