package ParallelImageProcessing.transformations;

import ParallelImageProcessing.transformations.actions.BlurAction;
import ParallelImageProcessing.transformations.actions.SobelAction;

import java.awt.image.BufferedImage;
import java.util.concurrent.ForkJoinPool;

public class Filtering {

    public enum SobelType {
        HORIZONTAL(new int[]{-1, -2, -1, 0, 0, 0, 1, 2, 1}),
        VERTICAL(new int[]{-1, 0, 1, -2, 0, 2, -1, 0, 1});

        int[] kernel;

        SobelType(int[] kernel) {
            this.kernel = kernel;
        }
    }

    public static BufferedImage blur(BufferedImage srcImage, int blurWidth) {
        int width = srcImage.getWidth();
        int height = srcImage.getHeight();
        int threshold = 10000;

        int[] src = srcImage.getRGB(0, 0, width, height, null, 0, width);
        int[] dst = new int[src.length];

        BlurAction blurAction = new BlurAction(src, 0, src.length, dst, blurWidth, threshold);
        ForkJoinPool pool = new ForkJoinPool();

        long startTime = System.currentTimeMillis();
        pool.invoke(blurAction);
        long endTime = System.currentTimeMillis();

        BufferedImage dstImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        dstImage.setRGB(0, 0, width, height, dst, 0, width);

        System.out.println("Threshold: " + threshold);
        System.out.println("Array size: " + src.length);
        System.out.println("Available processors count: " + Runtime.getRuntime().availableProcessors());
        System.out.println("Image blur took " + (endTime - startTime) + " milliseconds.");

        return dstImage;
    }

    public static BufferedImage sobel(BufferedImage srcImage, SobelType sobelMatrix) {
        int width = srcImage.getWidth();
        int height = srcImage.getHeight();
        int threshold = 100;

        int[] src = srcImage.getRGB(0, 0, width, height, null, 0, width);
        int[] dst = new int[src.length];

        SobelAction sobelAction = new SobelAction(srcImage, 0, 0, width, height, dst, sobelMatrix.kernel, threshold);
        ForkJoinPool pool = new ForkJoinPool();

        long startTime = System.currentTimeMillis();
        pool.invoke(sobelAction);
        long endTime = System.currentTimeMillis();

        BufferedImage dstImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        dstImage.setRGB(0, 0, width, height, dst, 0, width);

        System.out.println("Threshold: " + threshold);
        System.out.println("Array size: " + src.length);
        System.out.println("Available processors count: " + Runtime.getRuntime().availableProcessors());
        System.out.println("Image sobel took " + (endTime - startTime) + " milliseconds.");

        return dstImage;
    }

}
