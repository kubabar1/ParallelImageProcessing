package ParallelImageProcessing;

import java.awt.image.BufferedImage;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.logging.Logger;

public class ForkBlur extends RecursiveAction {

    private static final Logger logger = Logger.getLogger(ForkBlur.class.getName());

    protected static final int DIRECT_COMPUTING_THRESHOLD = 10000;
    protected static final int BLUR_WIDTH = 15;

    private final int[] sourcePixels;
    private final int startIndex;
    private final int numOfPixelsToBlur;
    private final int[] blurredPixels;

    public ForkBlur(int[] sourcePixels, int numOfPixelsToBlur, int[] blurredPixels) {
        this(sourcePixels, 0, numOfPixelsToBlur, blurredPixels);
    }

    public ForkBlur(int[] sourcePixels, int startIndex, int numOfPixelsToBlur, int[] blurredPixels) {
        this.sourcePixels = sourcePixels;
        this.startIndex = startIndex;
        this.numOfPixelsToBlur = numOfPixelsToBlur;
        this.blurredPixels = blurredPixels;
    }

    protected void computeDirectly() {
        int sidePixels = (BLUR_WIDTH - 1) / 2;
        for (int index = startIndex; index < startIndex + numOfPixelsToBlur; index++) {

            // Calculate average.
            float red = 0, green = 0, blue = 0;
            for (int i = -sidePixels; i <= sidePixels; i++) {
                int idx = Math.min(Math.max(i + index, 0), sourcePixels.length - 1);
                int pixel = sourcePixels[idx];
                red += (float) ((pixel & 0x00ff0000) >> 16) / BLUR_WIDTH;
                green += (float) ((pixel & 0x0000ff00) >> 8) / BLUR_WIDTH;
                blue += (float) (pixel & 0x000000ff) / BLUR_WIDTH;
            }

            // Re-assemble blurred pixel.
            int blurredPixel = (0xff000000)
                    | (((int) red) << 16)
                    | (((int) green) << 8)
                    | ((int) blue);
            blurredPixels[index] = blurredPixel;
        }
    }

    /**
     * compute() method performs the blur directly or splits it into two smaller tasks.
     * A simple array length threshold helps determine whether the work is performed or split.
     */
    @Override
    protected void compute() {
        if (numOfPixelsToBlur < DIRECT_COMPUTING_THRESHOLD) {
            computeDirectly();
            return;
        }
        int numOfPixelsToBeBlurred = this.numOfPixelsToBlur / 2;
        invokeAll(new ForkBlur(sourcePixels, startIndex, numOfPixelsToBeBlurred, blurredPixels),
                new ForkBlur(sourcePixels, startIndex + numOfPixelsToBeBlurred, this.numOfPixelsToBlur - numOfPixelsToBeBlurred, blurredPixels));
    }


    public static BufferedImage blur(BufferedImage srcImage) {
        int width = srcImage.getWidth();
        int height = srcImage.getHeight();

        int[] sourcePixels = srcImage.getRGB(0, 0, width, height, null, 0, width);
        int[] blurredPixels = new int[sourcePixels.length];

        logger.info(() -> "Image array size: " + sourcePixels.length);
        logger.info(() -> "Direct computing threshold: " + DIRECT_COMPUTING_THRESHOLD);
        logger.info(() -> "Blur width: " + BLUR_WIDTH);

        int processors = Runtime.getRuntime().availableProcessors();
        logger.info(() -> "Running blurring using " + processors + (processors != 1 ? " processors" : " processor"));
        long startTime = System.currentTimeMillis();
        new ForkJoinPool().invoke(new ForkBlur(sourcePixels, sourcePixels.length, blurredPixels));
        long endTime = System.currentTimeMillis();
        logger.info(() -> "Image blur took " + (endTime - startTime) + " milliseconds.");

        BufferedImage blurredImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        blurredImage.setRGB(0, 0, width, height, blurredPixels, 0, width);
        return blurredImage;
    }

}
