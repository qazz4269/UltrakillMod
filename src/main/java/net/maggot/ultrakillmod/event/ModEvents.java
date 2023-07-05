package net.maggot.ultrakillmod.event;

import net.maggot.ultrakillmod.UltrakillHell;
import net.maggot.ultrakillmod.entity.ModEntities;
import net.maggot.ultrakillmod.entity.custom.FilthEntity;
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
    }

    @SubscribeEvent
    public static void entitySpawnRestrictions(SpawnPlacementRegisterEvent event) {
        event.register(ModEntities.FILTH.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                FilthEntity::checkMonsterSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
    }
}
