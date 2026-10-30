package mrtjp.projectred.core.part;

import codechicken.lib.data.MCDataByteBuf;
import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import codechicken.multipart.api.part.BaseMultipart;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.function.Consumer;

/**
 * Base for Project Red's parts. CB Multipart 3.6 moved parts to {@link ValueOutput}/{@link ValueInput} and
 * {@link RegistryFriendlyByteBuf}; this keeps the {@code CompoundTag} / {@code MCDataOutput} API Project Red's parts
 * are written against and bridges it to the new one.
 */
public abstract class ProjectRedPart extends BaseMultipart {

    //region Project Red API (CB Multipart 3.5 style)
    public void save(CompoundTag tag, HolderLookup.Provider registries) {
    }

    public void load(CompoundTag tag, HolderLookup.Provider registries) {
    }

    public void writeDesc(MCDataOutput packet) {
    }

    public void readDesc(MCDataInput packet) {
    }

    public void readUpdate(MCDataInput packet) {
        readDesc(packet);
        tile().markRender();
    }

    public void sendUpdateData(Consumer<MCDataOutput> func) {
        sendUpdateData(buf -> func.accept(new MCDataByteBuf(buf)));
    }
    //endregion

    //region CB Multipart 3.6 API
    @Override
    public final void save(ValueOutput output) {
        CompoundTag tag = new CompoundTag();
        save(tag, hasTile() && hasLevel() ? level().registryAccess() : RegistryAccess.EMPTY);
        output.store(tag);
    }

    @Override
    public final void load(ValueInput input) {
        CompoundTag tag = input.read(MapCodec.assumeMapUnsafe(CompoundTag.CODEC)).orElseGet(CompoundTag::new);
        load(tag, input.lookup());
    }

    @Override
    public final void writeDesc(RegistryFriendlyByteBuf packet) {
        writeDesc(new MCDataByteBuf(packet));
    }

    @Override
    public final void readDesc(RegistryFriendlyByteBuf packet) {
        readDesc(new MCDataByteBuf(packet));
    }

    @Override
    public final void readUpdate(RegistryFriendlyByteBuf packet) {
        readUpdate(new MCDataByteBuf(packet));
    }
    //endregion
}
