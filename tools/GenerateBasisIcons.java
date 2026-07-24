import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class GenerateBasisIcons {
    private static final int TEAL = 0xFF53D6C7;
    private static final int GOLD = 0xFFE2B85B;
    private static final int BLUE = 0xFF70A7E8;
    private static final int MUTED = 0xFF596579;

    private GenerateBasisIcons() {
    }

    public static void main(String[] args) throws IOException {
        Path output = args.length == 0
                ? Path.of("src/main/resources/assets/mathmod/textures/gui/runes")
                : Path.of(args[0]);
        Files.createDirectories(output);
        write(output.resolve("right_basis_vector.png"), Direction.RIGHT);
        write(output.resolve("forward_basis_vector.png"), Direction.FORWARD);
        write(output.resolve("oblique_basis_vector.png"), Direction.OBLIQUE);
    }

    private static void write(Path path, Direction direction) throws IOException {
        BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        drawLine(image, 3, 12, 3, 3, MUTED);
        drawLine(image, 3, 12, 13, 12, MUTED);
        set(image, 2, 11, GOLD);
        set(image, 3, 11, GOLD);
        set(image, 2, 12, GOLD);
        set(image, 3, 12, GOLD);

        switch (direction) {
            case RIGHT -> {
                drawLine(image, 4, 11, 12, 11, TEAL);
                set(image, 12, 11, TEAL);
                set(image, 10, 9, TEAL);
                set(image, 10, 13, TEAL);
                set(image, 11, 10, TEAL);
                set(image, 11, 12, TEAL);
                set(image, 5, 9, BLUE);
            }
            case FORWARD -> {
                drawLine(image, 4, 11, 4, 3, TEAL);
                set(image, 4, 3, TEAL);
                set(image, 2, 5, TEAL);
                set(image, 6, 5, TEAL);
                set(image, 3, 4, TEAL);
                set(image, 5, 4, TEAL);
                set(image, 6, 11, BLUE);
            }
            case OBLIQUE -> {
                drawLine(image, 4, 11, 11, 4, TEAL);
                set(image, 11, 4, TEAL);
                set(image, 8, 4, TEAL);
                set(image, 11, 7, TEAL);
                set(image, 9, 5, TEAL);
                set(image, 10, 6, TEAL);
                set(image, 5, 12, BLUE);
            }
        }
        ImageIO.write(image, "png", path.toFile());
    }

    private static void drawLine(BufferedImage image, int x0, int y0, int x1, int y1, int color) {
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
        image.setRGB(x, y, color);
    }

    private enum Direction {
        RIGHT,
        FORWARD,
        OBLIQUE
    }
}
