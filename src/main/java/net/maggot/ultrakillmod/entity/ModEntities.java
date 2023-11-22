package net.maggot.ultrakillmod.entity;

import net.maggot.ultrakillmod.UltrakillHell;
import net.maggot.ultrakillmod.entity.custom.FilthEntity;
import net.maggot.ultrakillmod.entity.custom.MauriceEntity;
import net.maggot.ultrakillmod.entity.custom.StrayUltrakillEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, UltrakillHell.MOD_ID);

    public static final RegistryObject<EntityType<FilthEntity>> FILTH = ENTITY_TYPES.register("filth",
                    () -> EntityType.Builder.of(FilthEntity::new, MobCategory.MONSTER).sized(0.5f, 2.0f)
                            .build(new ResourceLocation(UltrakillHell.MOD_ID, "filth").toString()));

    public static final RegistryObject<EntityType<StrayUltrakillEntity>> STRAY_ULTRAKILL = ENTITY_TYPES.register("stray_ultrakill",
            () -> EntityType.Builder.of(StrayUltrakillEntity::new, MobCategory.MONSTER).sized(0.5f, 2.0f)
                    .build(new ResourceLocation(UltrakillHell.MOD_ID, "stray_ultrakill").toString()));


    public static final RegistryObject<EntityType<MauriceEntity>> MAURICE = ENTITY_TYPES.register("maurice",
                    () -> EntityType.Builder.of(MauriceEntity::new, MobCategory.MONSTER).sized(1.0f, 1.0f)
                            .build(new ResourceLocation(UltrakillHell.MOD_ID, "maurice").toString()));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
