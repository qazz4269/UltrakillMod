package net.maggot.ultrakillmod.datagen;

import net.maggot.ultrakillmod.UltrakillHell;
import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, UltrakillHell.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {

    }


}
