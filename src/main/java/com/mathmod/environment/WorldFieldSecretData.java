package com.mathmod.environment;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.security.SecureRandom;
import java.util.Arrays;

/** Persistent, server-only key material for P13 noise. It is never synchronized. */
public final class WorldFieldSecretData extends SavedData {
    private static final String DATA_ID = "mathmod_environmental_field_secret";
    private static final String SECRET_TAG = "secret";
    private static final int SECRET_BYTES = 32;
    private static final Factory<WorldFieldSecretData> FACTORY = new Factory<>(
            WorldFieldSecretData::new, WorldFieldSecretData::load
    );

    private final byte[] secret;
    private final boolean valid;

    private WorldFieldSecretData() {
        this.secret = new byte[SECRET_BYTES];
        new SecureRandom().nextBytes(this.secret);
        this.valid = true;
        setDirty();
    }

    private WorldFieldSecretData(byte[] secret, boolean valid) {
        this.secret = secret;
        this.valid = valid;
    }

    public static WorldFieldSecretData get(ServerLevel level) {
        return level.getServer().overworld().getDataStorage().computeIfAbsent(FACTORY, DATA_ID);
    }

    public byte[] secret() {
        if (!valid) {
            throw new IllegalStateException("Environmental world secret is malformed");
        }
        return Arrays.copyOf(secret, secret.length);
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putByteArray(SECRET_TAG, secret);
        return tag;
    }

    private static WorldFieldSecretData load(CompoundTag tag, HolderLookup.Provider registries) {
        byte[] secret = tag.getByteArray(SECRET_TAG);
        if (secret.length != SECRET_BYTES) {
            return new WorldFieldSecretData(new byte[SECRET_BYTES], false);
        }
        return new WorldFieldSecretData(Arrays.copyOf(secret, SECRET_BYTES), true);
    }
}
