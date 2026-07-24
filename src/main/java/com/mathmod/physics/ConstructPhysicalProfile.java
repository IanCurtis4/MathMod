package com.mathmod.physics;

import java.util.List;

/** Mass-weighted aggregate kept separate from P8 abstract massEquivalent. */
public record ConstructPhysicalProfile(double totalMass, PhysicsVector centerOfMass, InertiaTensor inertia, double gamma) {
    private static final double EPSILON = 1.0E-9D;
    public ConstructPhysicalProfile {
        if (!Double.isFinite(totalMass) || totalMass < 0 || totalMass > 32768 || centerOfMass == null || inertia == null
                || !Double.isFinite(gamma) || gamma < 0 || gamma > 3) throw new IllegalArgumentException("Invalid construct physical profile");
    }
    public static ConstructPhysicalProfile aggregate(List<MassPoint> points, double gamma) {
        if (points == null || points.isEmpty()) throw new IllegalArgumentException("Construct requires source points");
        if (!Double.isFinite(gamma) || gamma < 0 || gamma > 3) throw new IllegalArgumentException("Invalid gamma");
        double mass = sum(points.stream().mapToDouble(MassPoint::mass).toArray());
        PhysicsVector center = mass <= EPSILON ? arithmeticCenter(points) : weightedCenter(points, mass);
        InertiaTensor tensor = mass > EPSILON ? tensor(points, center) : InertiaTensor.ZERO;
        return new ConstructPhysicalProfile(Math.min(32768, mass), center, tensor, gamma);
    }
    public ConstructPhysicalProfile compress(double scale) {
        if (!Double.isFinite(scale) || scale < .25D || scale > 1D) throw new IllegalArgumentException("Invalid compression scale");
        return new ConstructPhysicalProfile(totalMass * Math.pow(scale, gamma), centerOfMass,
                inertia.scale(Math.pow(scale, gamma + 2D)), gamma);
    }
    public double scalarInertia(PhysicsVector axis) { return inertia.project(axis); }
    private static PhysicsVector weightedCenter(List<MassPoint> points, double mass) {
        return new PhysicsVector(
                sum(points.stream().mapToDouble(point -> point.position().x() * point.mass()).toArray()) / mass,
                sum(points.stream().mapToDouble(point -> point.position().y() * point.mass()).toArray()) / mass,
                sum(points.stream().mapToDouble(point -> point.position().z() * point.mass()).toArray()) / mass
        );
    }
    private static PhysicsVector arithmeticCenter(List<MassPoint> points) {
        return new PhysicsVector(
                sum(points.stream().mapToDouble(point -> point.position().x()).toArray()) / points.size(),
                sum(points.stream().mapToDouble(point -> point.position().y()).toArray()) / points.size(),
                sum(points.stream().mapToDouble(point -> point.position().z()).toArray()) / points.size()
        );
    }
    private static InertiaTensor tensor(List<MassPoint> points, PhysicsVector center) {
        double[] xx = new double[points.size()], yy = new double[points.size()], zz = new double[points.size()];
        double[] xy = new double[points.size()], xz = new double[points.size()], yz = new double[points.size()];
        for (int index = 0; index < points.size(); index++) {
            MassPoint point = points.get(index);
            InertiaTensor term = InertiaTensor.pointMass(point.mass(), point.position().subtract(center));
            xx[index] = term.xx(); yy[index] = term.yy(); zz[index] = term.zz();
            xy[index] = term.xy(); xz[index] = term.xz(); yz[index] = term.yz();
        }
        return new InertiaTensor(sum(xx), sum(yy), sum(zz), sum(xy), sum(xz), sum(yz));
    }
    private static double sum(double[] values) {
        double sum = 0;
        double compensation = 0;
        for (double value : values) {
            double corrected = value - compensation;
            double next = sum + corrected;
            compensation = (next - sum) - corrected;
            sum = next;
        }
        return sum;
    }
}
