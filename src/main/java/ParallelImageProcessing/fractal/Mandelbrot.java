package ParallelImageProcessing.fractal;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;

import java.util.concurrent.RecursiveTask;

class Mandelbrot extends RecursiveTask<Pixmap> {

    public static int threadCount = 0;

    private final int xStart;

    private final int yStart;

    private final int width;

    private final int height;

    private final Pixmap pixmap;

    private final double calcXStart;

    private final double calcYStart;

    private final double calcXStep;

    private final double calcYStep;

    private final int maxIterations;

    private final int depth;

    private final int calcDepth;

    private final Color debug;


    public Mandelbrot(final Pixmap pixmap, int calcDepth) {
        this.pixmap = pixmap;
        this.xStart = 0;
        this.yStart = 0;
        this.width = pixmap.getWidth();
        this.height = pixmap.getHeight();
        this.calcXStart = -1.5;
        this.calcYStart = -0.2;
        this.calcXStep = (0.2 * 2) / width;
        this.calcYStep = (0.2 * 2) / height;
        this.maxIterations = 50; //4096;
        this.depth = 0;
        this.calcDepth = calcDepth;
        this.debug = Color.GREEN;
        threadCount = 0;
    }

    public Mandelbrot(final Pixmap pixmap, int xStart, int yStart, int width, int height,
                      double calcXStart, double calcYStart, double calcXStep, double calcYStep,
                      int maxIterations, int depth, int calcDepth, Color debug) {
        this.pixmap = pixmap;
        this.xStart = xStart;
        this.yStart = yStart;
        this.width = width;
        this.height = height;
        this.calcXStart = calcXStart;
        this.calcYStart = calcYStart;
        this.calcXStep = calcXStep;
        this.calcYStep = calcYStep;
        this.maxIterations = maxIterations;
        this.depth = depth;
        this.calcDepth = calcDepth;
        this.debug = debug;
    }

    @Override
    public Pixmap compute() {
        if (depth >= calcDepth) {
            threadCount++;
            generate();
        } else {
            int xOffset = width / 2;
            int yOffset = height / 2;
            int level = depth + 1;

            double calcOffsetX = calcXStart + (calcXStep * xStart);
            double calcOffsetY = calcYStart + (calcYStep * yStart);

            double calcOffsetX2 = calcXStart + (calcXStep * xStart) + (calcXStep * xOffset);
            double calcOffsetY2 = calcYStart + (calcYStep * yStart) + (calcYStep * yOffset);

            invokeAll(
                    new Mandelbrot(pixmap, xStart, yStart, xOffset, yOffset, calcOffsetX, calcOffsetY, calcXStep,
                            calcYStep, maxIterations, level, calcDepth, Color.RED),
                    new Mandelbrot(pixmap, xStart + xOffset, yStart, xOffset, yOffset, calcOffsetX2, calcOffsetY,
                            calcXStep, calcYStep, maxIterations, level, calcDepth, Color.YELLOW),
                    new Mandelbrot(pixmap, xStart, yStart + yOffset, xOffset, yOffset, calcOffsetX, calcOffsetY2,
                            calcXStep, calcYStep, maxIterations, level, calcDepth, Color.BLUE),
                    new Mandelbrot(pixmap, xStart + xOffset, yStart + yOffset, xOffset, yOffset,
                            calcOffsetX2, calcOffsetY2, calcXStep, calcYStep, maxIterations, level, calcDepth, Color.GREEN)
            );
        }

        debug();

        return pixmap;
    }

    /**
     * Rysowanie danego fragmentu fraktala
     */
    public void generate() {
        double px = 0, py = 0;
        double zx = 0.0, zy = 0.0, zx2 = 0.0, zy2 = 0.0;
        int value = 0;
        float grey = 0;

        py = calcYStart;
        for (int y = yStart; y < yStart + height; y++) {
            px = calcXStart;
            for (int x = xStart; x < xStart + width; x++) {
                zx = 0;
                zy = 0;
                value = 0;

                for (int m = 0; m < maxIterations; m++) {
                    System.out.println("###########################################");
                    System.out.println(x);
                    System.out.println(y);
                    System.out.println(m);
                    System.out.println("###########################################");
                    zx2 = zx;
                    zx = zx * zx - zy * zy + px;
                    zy = 2 * zx2 * zy + py;
                    if ((zx * zx + zy * zy) > 4) {
                        break;
                    }
                    value++;
                }
                grey = ((maxIterations - value) * (54654564 / maxIterations));
                pixmap.drawPixel(x, y, ((int) grey << 24) | ((int) grey << 16) | ((int) grey << 8) | 255);

                px += calcXStep;
            }
            py += calcYStep;
        }
    }

    protected void debug() {
        if (debug != null) {
            pixmap.setColor(debug);
            pixmap.drawRectangle(xStart, yStart, width, height);
        }
    }
}
