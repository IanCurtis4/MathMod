package com.mathmod.acquisition;

import com.mathmod.manuscript.ManuscriptDefinitions;
import com.mathmod.registry.ModDataComponents;
import com.mathmod.registry.ModItems;
import com.mathmod.util.NamespacedId;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.LootModifier;

import java.util.random.RandomGenerator;

public final class ManuscriptLootModifier extends LootModifier {
    public static final MapCodec<ManuscriptLootModifier> CODEC = RecordCodecBuilder.mapCodec(instance ->
            codecStart(instance).and(NamespacedId.CODEC.fieldOf("pool").forGetter(ManuscriptLootModifier::pool))
                    .apply(instance, ManuscriptLootModifier::new)
    );

    private final NamespacedId pool;

    public ManuscriptLootModifier(LootItemCondition[] conditions, NamespacedId pool) {
        super(conditions);
        this.pool = pool;
    }

    public NamespacedId pool() {
        return pool;
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        RandomGenerator random = context.getRandom()::nextLong;
        ManuscriptLootPlanner.selectManuscript(
                ManuscriptDefinitions.acquisitionSnapshot(),
                ManuscriptDefinitions.acquisitionConfig(),
                pool,
                random
        ).ifPresent(manuscriptId -> {
            ItemStack manuscript = new ItemStack(ModItems.FIELD_MANUSCRIPT.get());
            manuscript.set(ModDataComponents.MANUSCRIPT_ID.get(), manuscriptId.toString());
            generatedLoot.add(manuscript);
        });
        return generatedLoot;
    }

    @Override
    public MapCodec<ManuscriptLootModifier> codec() {
        return CODEC;
    }
}
