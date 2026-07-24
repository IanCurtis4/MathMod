package com.mathmod.field;

@FunctionalInterface
public interface FieldSamplerFactory {
    FieldSampler create(FieldSamplingContext context);
}
