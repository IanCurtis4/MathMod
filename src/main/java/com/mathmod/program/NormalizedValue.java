package com.mathmod.program;

import net.minecraft.world.phys.Vec3;

public sealed interface NormalizedValue permits NormalizedValue.NumberValue, NormalizedValue.BoolValue, NormalizedValue.VectorValue {
    Object runtimeValue();

    record NumberValue(double value) implements NormalizedValue {
        public NumberValue {
            if (!Double.isFinite(value)) {
                throw new IllegalArgumentException("Normalized numbers must be finite");
            }
        }

        @Override
        public Object runtimeValue() {
            return value;
        }
    }

    record BoolValue(boolean value) implements NormalizedValue {
        @Override
        public Object runtimeValue() {
            return value;
        }
    }

    record VectorValue(double x, double y, double z) implements NormalizedValue {
        public VectorValue {
            if (!Double.isFinite(x) || !Double.isFinite(y) || !Double.isFinite(z)) {
                throw new IllegalArgumentException("Normalized vectors must be finite");
            }
        }

        public static VectorValue from(Vec3 vector) {
            return new VectorValue(vector.x, vector.y, vector.z);
        }

        public Vec3 vector() {
            return new Vec3(x, y, z);
        }

        @Override
        public Object runtimeValue() {
            return vector();
        }
    }
}
