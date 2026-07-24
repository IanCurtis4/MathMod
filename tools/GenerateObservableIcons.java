import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class GenerateObservableIcons {
    private static final int TEAL = 0xFF53D6C7;
    private static final int GOLD = 0xFFE2B85B;
    private static final int BLUE = 0xFF70A7E8;
    private static final int CORAL = 0xFFE77C72;
    private static final int MUTED = 0xFF596579;

    private GenerateObservableIcons() {
    }

    public static void main(String[] args) throws IOException {
        Path output = args.length == 0
                ? Path.of("src/main/resources/assets/mathmod/textures/gui/runes")
                : Path.of(args[0]);
        Files.createDirectories(output);
        write(output.resolve("sense_nearby_entities.png"), Glyph.SENSOR);
        write(output.resolve("entity_velocities.png"), Glyph.VELOCITY);
        write(output.resolve("vector_lengths.png"), Glyph.LENGTHS);
        write(output.resolve("sum_numbers.png"), Glyph.SUM);
        write(output.resolve("mean_number.png"), Glyph.MEAN);
        write(output.resolve("max_number.png"), Glyph.MAX);
        write(output.resolve("number_round.png"), Glyph.ROUND);
        write(output.resolve("emit_anchor_redstone.png"), Glyph.REDSTONE);
    }

    private static void write(Path path, Glyph glyph) throws IOException {
        BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        switch (glyph) {
            case SENSOR -> {
                set(image, 7, 7, GOLD);
                set(image, 8, 7, GOLD);
                ring(image, 7, 7, 3, TEAL);
                ring(image, 7, 7, 6, BLUE);
            }
            case VELOCITY -> {
                entity(image, 4, 8);
                line(image, 7, 6, 13, 6, TEAL);
                arrow(image, 13, 6, TEAL);
                line(image, 7, 10, 11, 10, BLUE);
            }
            case LENGTHS -> {
                line(image, 3, 12, 12, 3, TEAL);
                set(image, 2, 12, GOLD);
                set(image, 3, 13, GOLD);
                set(image, 12, 2, GOLD);
                set(image, 13, 3, GOLD);
                line(image, 3, 5, 3, 10, MUTED);
            }
            case SUM -> {
                line(image, 4, 3, 11, 3, GOLD);
                line(image, 4, 12, 11, 12, GOLD);
                line(image, 4, 3, 9, 7, TEAL);
                line(image, 9, 7, 4, 12, TEAL);
            }
            case MEAN -> {
                line(image, 2, 11, 13, 11, MUTED);
                line(image, 3, 9, 3, 6, BLUE);
                line(image, 6, 9, 6, 3, TEAL);
                line(image, 9, 9, 9, 7, BLUE);
                line(image, 12, 9, 12, 4, TEAL);
                line(image, 2, 6, 13, 6, GOLD);
            }
            case MAX -> {
                line(image, 2, 12, 13, 12, MUTED);
                line(image, 3, 11, 3, 8, BLUE);
                line(image, 7, 11, 7, 5, TEAL);
                line(image, 11, 11, 11, 2, GOLD);
                set(image, 10, 2, CORAL);
                set(image, 12, 2, CORAL);
            }
            case ROUND -> {
                ring(image, 7, 7, 5, TEAL);
                line(image, 3, 4, 7, 4, GOLD);
                line(image, 7, 4, 10, 7, GOLD);
                set(image, 10, 8, CORAL);
            }
            case REDSTONE -> {
                line(image, 2, 12, 2, 9, MUTED);
                line(image, 2, 9, 5, 9, CORAL);
                line(image, 5, 9, 5, 4, CORAL);
                line(image, 5, 4, 10, 4, GOLD);
                line(image, 10, 4, 10, 9, CORAL);
                line(image, 10, 9, 13, 9, CORAL);
                set(image, 7, 12, TEAL);
                set(image, 8, 12, TEAL);
            }
        }
        ImageIO.write(image, "png", path.toFile());
    }

    private static void entity(BufferedImage image, int x, int y) {
        set(image, x, y - 4, GOLD);
        set(image, x + 1, y - 4, GOLD);
        line(image, x, y - 2, x, y + 2, BLUE);
        line(image, x + 1, y - 2, x + 1, y + 2, BLUE);
        set(image, x - 1, y, BLUE);
        set(image, x + 2, y, BLUE);
    }

    private static void ring(BufferedImage image, int cx, int cy, int radius, int color) {
        int x = radius;
        int y = 0;
        int error = 1 - x;
        while (x >= y) {
            plotOctants(image, cx, cy, x, y, color);
            y++;
            if (error < 0) {
                error += 2 * y + 1;
            } else {
                x--;
                error += 2 * (y - x + 1);
            }
        }
    }

    private static void plotOctants(BufferedImage image, int cx, int cy, int x, int y, int color) {
        set(image, cx + x, cy + y, color);
        set(image, cx + y, cy + x, color);
        set(image, cx - y, cy + x, color);
        set(image, cx - x, cy + y, color);
        set(image, cx - x, cy - y, color);
        set(image, cx - y, cy - x, color);
        set(image, cx + y, cy - x, color);
        set(image, cx + x, cy - y, color);
    }

    private static void arrow(BufferedImage image, int x, int y, int color) {
        set(image, x - 2, y - 2, color);
        set(image, x - 1, y - 1, color);
        set(image, x - 2, y + 2, color);
        set(image, x - 1, y + 1, color);
    }

    private static void line(BufferedImage image, int x0, int y0, int x1, int y1, int color) {
        int dx = Math.abs(x1 - x0);
        int dy = -Math.abs(y1 - y0);
        int stepX = x0 < x1 ? 1 : -1;
        int stepY = y0 < y1 ? 1 : -1;
        int error = dx + dy;
        while (true) {
            set(image, x0, y0, color);
            if (x0 == x1 && y0 == y1) {
                return;
            }
            int doubledError = error * 2;
            if (doubledError >= dy) {
                error += dy;
                x0 += stepX;
            }
            if (doubledError <= dx) {
                error += dx;
                y0 += stepY;
            }
        }
    }

    private static void set(BufferedImage image, int x, int y, int color) {
        if (x >= 0 && x < 16 && y >= 0 && y < 16) {
            image.setRGB(x, y, color);
        }
    }

    private enum Glyph {
        SENSOR,
        VELOCITY,
        LENGTHS,
        SUM,
        MEAN,
        MAX,
        ROUND,
        REDSTONE
    }
}
