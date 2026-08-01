package com.mathmod.program;

import org.junit.jupiter.api.Test;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class P8AuthoritySurfaceTest {
    private static final List<String> REGISTERED_SERVER_PAYLOADS = List.of(
            "com.mathmod.network.ApplyCustomSpellInvocationPayload",
            "com.mathmod.network.UpdateCustomSpellNamePayload",
            "com.mathmod.network.OpenProgrammerHelpPayload",
            "com.mathmod.network.OpenResourceHelpPayload",
            "com.mathmod.network.OpenManuscriptManualPayload"
    );

    @Test
    void registeredServerPayloadsCarryNoP8Authority() {
        ClassShape registration = shape("com.mathmod.network.ModNetworking");
        for (String payload : REGISTERED_SERVER_PAYLOADS) {
            assertTrue(registration.utf8().contains(payload.replace('.', '/')),
                    () -> "ModNetworking must register " + payload);
            ClassShape shape = shape(payload);
            for (Member field : shape.fields()) {
                if (Set.of("TYPE", "STREAM_CODEC", "INSTANCE").contains(field.name())) continue;
                assertFalse(field.name().matches(".*(?i: candidate|position|count|fill|block|state|mass|snapshot|flight|chunk|load).*".replace(" ", "")),
                        () -> payload + " must not accept P8 authority field " + field.name());
                assertFalse(field.descriptor().matches(".*(BlockPos|BlockState|ConstructBody|CapturedConstructPhysics).*"),
                        () -> payload + " must not accept P8 authority type " + field.descriptor());
            }
        }
    }

    @Test
    void p8ExecutionBoundaryRequiresServerOwnedInputs() {
        ClassShape fill = shape("com.mathmod.program.ConstructionFillService");
        assertTrue(fill.hasMethod("fill", "Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/server/level/ServerPlayer;Lcom/mathmod/program/SpatialRegion;Ljava/lang/String;"),
                "fill must receive server level, server player, server-owned region, and material id");

        ClassShape planner = shape("com.mathmod.program.RegionCandidatePlanner");
        assertTrue(planner.hasMethod("plan", "Lcom/mathmod/program/SpatialRegion;"), "candidate planning must remain server-side");

        ClassShape flight = shape("com.mathmod.program.ConstructFlightManager");
        assertTrue(flight.hasMethod("launch", "Lnet/minecraft/server/level/ServerPlayer;Lcom/mathmod/program/ConstructBody;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;"),
                "flight launch must receive authoritative owner and materialized body");
        assertFalse(flight.methodDescriptors("launch").stream().anyMatch(descriptor -> descriptor.contains("I")),
                "launch must not accept a client count/payment field");
        assertTrue(shape("com.mathmod.program.ConstructBody").fields().stream().anyMatch(field -> field.name().equals("massEquivalent")),
                "body mass-equivalent must remain inside the server-owned body");
    }

    private static ClassShape shape(String binaryName) {
        String resource = "/" + binaryName.replace('.', '/') + ".class";
        try (InputStream stream = P8AuthoritySurfaceTest.class.getResourceAsStream(resource);
             DataInputStream input = new DataInputStream(stream)) {
            if (input == null || input.readInt() != 0xCAFEBABE) throw new AssertionError("missing class bytes: " + binaryName);
            input.readUnsignedShort(); input.readUnsignedShort();
            Object[] pool = constantPool(input);
            input.readUnsignedShort(); input.readUnsignedShort(); input.readUnsignedShort();
            int interfaces = input.readUnsignedShort();
            for (int index = 0; index < interfaces; index++) input.readUnsignedShort();
            List<Member> fields = members(input, pool);
            List<Member> methods = members(input, pool);
            List<String> utf8 = new ArrayList<>();
            for (Object entry : pool) if (entry instanceof String value) utf8.add(value);
            return new ClassShape(fields, methods, utf8);
        } catch (IOException exception) {
            throw new AssertionError("cannot inspect class bytes: " + binaryName, exception);
        }
    }

    private static Object[] constantPool(DataInputStream input) throws IOException {
        Object[] pool = new Object[input.readUnsignedShort()];
        for (int index = 1; index < pool.length; index++) switch (input.readUnsignedByte()) {
            case 1 -> pool[index] = input.readUTF();
            case 3, 4 -> input.skipBytes(4);
            case 5, 6 -> { input.skipBytes(8); index++; }
            case 7, 8, 16, 19, 20 -> input.skipBytes(2);
            case 9, 10, 11, 12, 17, 18 -> input.skipBytes(4);
            case 15 -> input.skipBytes(3);
            default -> throw new IOException("unknown class constant");
        };
        return pool;
    }

    private static List<Member> members(DataInputStream input, Object[] pool) throws IOException {
        List<Member> members = new ArrayList<>();
        for (int count = input.readUnsignedShort(); count > 0; count--) {
            input.readUnsignedShort();
            members.add(new Member((String) pool[input.readUnsignedShort()], (String) pool[input.readUnsignedShort()]));
            skipAttributes(input);
        }
        return members;
    }

    private static void skipAttributes(DataInputStream input) throws IOException {
        for (int count = input.readUnsignedShort(); count > 0; count--) {
            input.readUnsignedShort();
            input.skipNBytes(Integer.toUnsignedLong(input.readInt()));
        }
    }

    private record Member(String name, String descriptor) { }
    private record ClassShape(List<Member> fields, List<Member> methods, List<String> utf8) {
        boolean hasMethod(String name, String parameters) { return methodDescriptors(name).stream().anyMatch(descriptor -> descriptor.startsWith("(" + parameters + ")")); }
        List<String> methodDescriptors(String name) { return methods.stream().filter(method -> method.name().equals(name)).map(Member::descriptor).toList(); }
    }
}
