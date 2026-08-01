package com.mathmod.program;

import com.mathmod.knowledge.KnowledgeService;
import com.mathmod.runes.MathModRuneBootstrap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.BooleanSupplier;

/** Internal reachability bridge; it is not a supported functional-inscription API. */
@ApiStatus.Internal
public final class ScopedFunctionalInscriptionEntryPoint {
    private ScopedFunctionalInscriptionEntryPoint() { }

    public static boolean tryInscribeFactoredLeap(ServerPlayer player, InteractionHand hand, BooleanSupplier requestStillCurrent) {
        if (player == null || hand == null || requestStillCurrent == null || !requestStillCurrent.getAsBoolean()) return false;
        MathModRuneBootstrap.bootstrap();
        ScopedCommitResult result = new ScopedFunctionalInscriptionService(MathModRuneBootstrap.registry()).inscribe(
                FactoredLeapTheorem.source(), "", new ScopedCommitAuthority(
                        () -> player.getItemInHand(hand), () -> KnowledgeService.get(player), () -> !requestStillCurrent.getAsBoolean()));
        return result == ScopedCommitResult.SUCCESS;
    }
}
