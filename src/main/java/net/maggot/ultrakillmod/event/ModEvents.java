package net.maggot.ultrakillmod.event;

import net.maggot.ultrakillmod.UltrakillHell;
import net.maggot.ultrakillmod.entity.ModEntities;
import net.maggot.ultrakillmod.entity.custom.FilthEntity;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = UltrakillHell.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEvents {
    @SubscribeEvent
    public static void entityAttributeEvent(EntityAttributeCreationEvent event) {
        event.put(ModEntities.FILTH.get(), FilthEntity.setAttributes());
    }
}
