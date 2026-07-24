package com.mathmod.program;

import com.mathmod.MathMod;
import com.mathmod.physics.PhysicalProfileSnapshot;
import com.mathmod.physics.PhysicalProfiles;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/** Bounded server-owned projectile trajectory. It creates no entity, chunk ticket, or terrain mutation. */
public final class ConstructFlightManager {
    private static final List<Flight> FLIGHTS = new ArrayList<>();

    private ConstructFlightManager() {
    }

    static boolean launch(ServerPlayer owner, ConstructBody body, Vec3 origin, Vec3 velocity) {
        if (velocity.length() > 2.0D || velocity.lengthSqr() <= VoxelRegion.EPSILON) return false;
        if (FLIGHTS.stream().anyMatch(flight -> flight.owner().equals(owner.getUUID()))) return false;
        try {
            var item = ItemSelectors.exactItem(body.materialId());
            if (!(item instanceof net.minecraft.world.item.BlockItem blockItem)) return false;
            int required = body.massEquivalent();
            int available = owner.getInventory().items.stream().filter(stack -> stack.is(item)).mapToInt(net.minecraft.world.item.ItemStack::getCount).sum()
                    + owner.getInventory().offhand.stream().filter(stack -> stack.is(item)).mapToInt(net.minecraft.world.item.ItemStack::getCount).sum();
            if (!owner.getAbilities().instabuild && available < required) return false;
            net.minecraft.world.level.block.state.BlockState state = blockItem.getBlock().defaultBlockState();
            PhysicalProfileSnapshot physicsSnapshot = PhysicalProfiles.snapshot();
            CapturedConstructPhysics physics = CapturedConstructPhysics.capture(
                    physicsSnapshot.version(),
                    body,
                    physicsSnapshot.resolve(com.mathmod.physics.CanonicalBlockPhysicalInputAdapter.from(state))
            );
            if (!owner.getAbilities().instabuild) consume(owner, item, required);
            FLIGHTS.add(new Flight(owner.serverLevel(), owner.getUUID(), owner, origin, velocity, body, state, physics, 0));
            return true;
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }

    public static void onServerTick(ServerTickEvent.Post event) {
        tickServer(event.getServer());
    }

    static void tickServer(MinecraftServer server) {
        Iterator<Flight> iterator = FLIGHTS.iterator();
        while (iterator.hasNext()) {
            Flight flight = iterator.next();
            if (flight.level().getServer() != server || !tick(flight)) iterator.remove();
        }
    }

    static int activeFlightCount() {
        return FLIGHTS.size();
    }

    static java.util.OptionalLong activeSnapshotVersion(UUID owner) {
        return FLIGHTS.stream()
                .filter(flight -> flight.owner().equals(owner))
                .findFirst()
                .map(flight -> java.util.OptionalLong.of(flight.physics().snapshotVersion()))
                .orElseGet(java.util.OptionalLong::empty);
    }

    static void clearForTests() {
        FLIGHTS.clear();
    }

    private static boolean tick(Flight flight) {
        if (flight.age() >= 100 || !flight.level().hasChunkAt(net.minecraft.core.BlockPos.containing(flight.position()))) return false;
        Vec3 next = flight.position().add(flight.velocity());
        HitResult blockHit = flight.level().clip(new ClipContext(flight.position(), next,
                ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, flight.collisionEntity()));
        if (blockHit.getType() != HitResult.Type.MISS) return false;
        AABB sweep = new AABB(flight.position(), next).inflate(flight.body().collisionRadius());
        List<LivingEntity> targets = flight.level().getEntitiesOfClass(LivingEntity.class, sweep,
                entity -> entity.isAlive() && !entity.getUUID().equals(flight.owner())).stream().limit(8).toList();
        if (!targets.isEmpty()) {
            Vec3 impulse = flight.velocity().normalize().scale(Math.min(1.25D,
                    0.12D + Math.sqrt(flight.body().massEquivalent()) * flight.velocity().length() * 0.05D));
            for (LivingEntity target : targets) {
                target.push(impulse.x, impulse.y, impulse.z);
                target.hasImpulse = true;
                if (target instanceof ServerPlayer player) player.hurtMarked = true;
            }
            return false;
        }
        flight.level().sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, flight.state()),
                next.x, next.y, next.z, Math.max(2, flight.body().massEquivalent() / 8),
                flight.body().collisionRadius() * 0.25D, flight.body().collisionRadius() * 0.25D,
                flight.body().collisionRadius() * 0.25D, 0.01D);
        flight.advance(next);
        return true;
    }

    private static void consume(ServerPlayer player, net.minecraft.world.item.Item item, int count) {
        int remaining = count;
        for (var slots : List.of(player.getInventory().items, player.getInventory().offhand)) {
            for (var stack : slots) {
                if (!stack.is(item) || remaining == 0) continue;
                int amount = Math.min(remaining, stack.getCount());
                stack.shrink(amount);
                remaining -= amount;
            }
        }
        player.getInventory().setChanged();
        player.containerMenu.broadcastChanges();
    }

    private static final class Flight {
        private final ServerLevel level; private final UUID owner; private final net.minecraft.world.entity.Entity collisionEntity;
        private Vec3 position; private final Vec3 velocity;
        private final ConstructBody body; private final net.minecraft.world.level.block.state.BlockState state;
        private final CapturedConstructPhysics physics; private int age;
        private Flight(ServerLevel level, UUID owner, net.minecraft.world.entity.Entity collisionEntity,
                       Vec3 position, Vec3 velocity, ConstructBody body,
                       net.minecraft.world.level.block.state.BlockState state, CapturedConstructPhysics physics, int age) {
            this.level = level; this.owner = owner; this.collisionEntity = collisionEntity;
            this.position = position; this.velocity = velocity; this.body = body;
            this.state = state; this.physics = physics; this.age = age;
        }
        ServerLevel level() { return level; } UUID owner() { return owner; }
        net.minecraft.world.entity.Entity collisionEntity() { return collisionEntity; } Vec3 position() { return position; }
        Vec3 velocity() { return velocity; } ConstructBody body() { return body; }
        net.minecraft.world.level.block.state.BlockState state() { return state; } int age() { return age; }
        CapturedConstructPhysics physics() { return physics; }
        void advance(Vec3 next) { position = next; age++; }
    }
}
