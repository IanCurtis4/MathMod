package com.mathmod.field;

@FunctionalInterface
public interface FieldSampler {
    FieldSampleValue sample(SamplePoint point) throws FieldSampleException;
}
