package mrtjp.projectred.transmission.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

import static mrtjp.projectred.transmission.ProjectRedTransmission.MOD_ID;

public class TransmissionBlockTagsProvider extends BlockTagsProvider {

    public TransmissionBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {

    }
}
