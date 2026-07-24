package com.mathmod.environment;

import java.util.Comparator;
import java.util.List;

/** Bounded, continuous piecewise-linear contribution over normalized build height. */
public record EnvironmentalHeightCurve(List<Point> points) {
    public EnvironmentalHeightCurve {
        points = List.copyOf(points);
        if (points.isEmpty() || points.size() > 8) {
            throw new IllegalArgumentException("Height curve must contain between 1 and 8 points");
        }
        if (!points.equals(points.stream().sorted(Comparator.comparingDouble(Point::x)).toList())) {
            throw new IllegalArgumentException("Height curve points must be sorted by x");
        }
        for (int index = 1; index < points.size(); index++) {
            if (points.get(index - 1).x() >= points.get(index).x()) {
                throw new IllegalArgumentException("Height curve x values must be strictly increasing");
            }
        }
    }

    public double valueAt(double height01) {
        if (!Double.isFinite(height01)) {
            throw new IllegalArgumentException("Height must be finite");
        }
        double height = Math.max(0.0D, Math.min(1.0D, height01));
        Point first = points.getFirst();
        Point last = points.getLast();
        if (height <= first.x()) return first.y();
        if (height >= last.x()) return last.y();
        for (int index = 1; index < points.size(); index++) {
            Point upper = points.get(index);
            if (height <= upper.x()) {
                Point lower = points.get(index - 1);
                double fraction = (height - lower.x()) / (upper.x() - lower.x());
                return lower.y() + (upper.y() - lower.y()) * fraction;
            }
        }
        return last.y();
    }

    public record Point(double x, double y) {
        public Point {
            if (!Double.isFinite(x) || x < 0.0D || x > 1.0D || !Double.isFinite(y)) {
                throw new IllegalArgumentException("Height point must be finite and x must be in [0, 1]");
            }
        }
    }
}
