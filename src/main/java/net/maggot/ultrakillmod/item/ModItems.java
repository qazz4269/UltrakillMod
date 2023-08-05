package net.maggot.ultrakillmod.item;

import net.maggot.ultrakillmod.UltrakillHell;
import net.maggot.ultrakillmod.entity.ModEntities;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, UltrakillHell.MOD_ID);

    public static final RegistryObject<Item> BLUE_SKULL = ITEMS.register("blue_skull",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> RED_SKULL = ITEMS.register("red_skull",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> FILTH_SPAWN_EGG = ITEMS.register("filth_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.FILTH, 0x9BA456, 0x5E6435, new Item.Properties()));
    public static final RegistryObject<Item> MAURICE_SPAWN_EGG = ITEMS.register("maurice_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.MAURICE, 0x7A6A5D, 0x201811, new Item.Properties()));


    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}

