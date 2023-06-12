package net.maggot.ultrakillmod.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.maggot.ultrakillmod.UltrakillHell;
import net.maggot.ultrakillmod.entity.custom.FilthEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class FilthRenderer extends GeoEntityRenderer<FilthEntity> {
    public FilthRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new FilthModel());
    }

    @Override
    public ResourceLocation getTextureLocation(FilthEntity animatable) {
        return new ResourceLocation(UltrakillHell.MOD_ID, "textures/entity/tiger.png");
    }

    @Override
    public void render(FilthEntity entity, float entityYaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight) {
        if(entity.isBaby()) {
            poseStack.scale(0.4f, 0.4f, 0.4f);
        }

        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
