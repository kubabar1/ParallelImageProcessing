package ParallelImageProcessing.transformations;

import ParallelImageProcessing.transformations.actions.BlurAction;

import java.awt.image.BufferedImage;
import java.util.concurrent.ForkJoinPool;

public class Filtering {

    public static BufferedImage blur(BufferedImage srcImage, int blurWidth) {
        int width = srcImage.getWidth();
        int height = srcImage.getHeight();
        int threshold = 10000;

        int[] src = srcImage.getRGB(0, 0, width, height, null, 0, width);
        int[] dst = new int[src.length];

        BlurAction fb = new BlurAction(src, 0, src.length, dst, blurWidth, threshold);
        ForkJoinPool pool = new ForkJoinPool();

        long startTime = System.currentTimeMillis();
        pool.invoke(fb);
        long endTime = System.currentTimeMillis();

        BufferedImage dstImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        dstImage.setRGB(0, 0, width, height, dst, 0, width);

        System.out.println("Threshold: " + threshold);
        System.out.println("Array size: " + src.length);
        System.out.println("Available processors count: " + Runtime.getRuntime().availableProcessors());
        System.out.println("Image blur took " + (endTime - startTime) + " milliseconds.");

        return dstImage;
    }
}
