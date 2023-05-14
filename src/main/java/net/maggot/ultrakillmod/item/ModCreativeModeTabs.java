package net.maggot.ultrakillmod.item;

import net.maggot.ultrakillmod.UltrakillHell;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = UltrakillHell.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModCreativeModeTabs {
    public static CreativeModeTab ULTRAKILL_TAB;

    @SubscribeEvent
    public static void registerCreativeModeTabs(CreativeModeTabEvent.Register event) {
        ULTRAKILL_TAB = event.registerCreativeModeTab(new ResourceLocation(UltrakillHell.MOD_ID, "ultrakill_tab"),
                builder -> builder.icon(() -> new ItemStack(ModItems.BLUE_SKULL.get()))
                        .title(Component.translatable( "creativemodetab.ultrakill_tab")));
    }
}
