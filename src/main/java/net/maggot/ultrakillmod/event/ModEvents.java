package net.maggot.ultrakillmod.event;

import net.maggot.ultrakillmod.UltrakillHell;
import net.maggot.ultrakillmod.entity.ModEntities;
import net.maggot.ultrakillmod.entity.custom.FilthEntity;
import net.maggot.ultrakillmod.entity.custom.MauriceEntity;
import net.maggot.ultrakillmod.entity.custom.StrayUltrakillEntity;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = UltrakillHell.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEvents {
    @SubscribeEvent
    public static void entityAttributeEvent(EntityAttributeCreationEvent event) {
        event.put(ModEntities.FILTH.get(), FilthEntity.setAttributes());
        event.put(ModEntities.MAURICE.get(), MauriceEntity.setAttributes());
        event.put(ModEntities.STRAY_ULTRAKILL.get(), StrayUltrakillEntity.setAttributes());
    }

    @SubscribeEvent
    public static void entitySpawnRestrictions(SpawnPlacementRegisterEvent event) {
        event.register(ModEntities.FILTH.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                FilthEntity::checkMobSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
        event.register(ModEntities.MAURICE.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                MauriceEntity::checkMobSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
        event.register(ModEntities.STRAY_ULTRAKILL.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                StrayUltrakillEntity::checkMobSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
    }
}
