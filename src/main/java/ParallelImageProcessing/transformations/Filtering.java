package ParallelImageProcessing.transformations;

import ParallelImageProcessing.transformations.actions.BlurAction;
import ParallelImageProcessing.transformations.actions.ConvolutionAction;

import java.awt.image.BufferedImage;
import java.util.concurrent.ForkJoinPool;

public class Filtering {

    public enum SobelType {
        HORIZONTAL(new int[]{
                -1, -2, -1,
                0, 0, 0,
                1, 2, 1
        }),
        VERTICAL(new int[]{
                -1, 0, 1,
                -2, 0, 2,
                -1, 0, 1
        });

        int[] kernel;

        SobelType(int[] kernel) {
            this.kernel = kernel;
        }
    }

    public enum LaplacianType {
        LAPLACIAN(new int[]{
                0, -1, 0,
                -1, 4, -1,
                0, -1, 0
        }),
        LAPLACIAN_DIAGONAL(new int[]{
                -1, -1, -1,
                -1, 8, -1,
                -1, -1, -1
        }),
        LAPLACIAN_GAUSSIAN(new int[]{
                0, 0, -1, 0, 0,
                0, -1, -2, -1, 0,
                -1, -2, 16, -2, -1,
                0, -1, -2, -1, 0,
                0, 0, -1, 0, 0
        });

        int[] kernel;

        LaplacianType(int[] kernel) {
            this.kernel = kernel;
        }
    }

    public static BufferedImage sobel(BufferedImage srcImage, SobelType sobelMatrix) {
        return convolution(srcImage, sobelMatrix.kernel);
    }

    public static BufferedImage laplacian(BufferedImage srcImage, LaplacianType laplacianMatrix) {
        return convolution(srcImage, laplacianMatrix.kernel);
    }

    public static BufferedImage blur(BufferedImage srcImage, int blurWidth) {
        int width = srcImage.getWidth();
        int height = srcImage.getHeight();
        int threshold = 1000;

        int[] src = srcImage.getRGB(0, 0, width, height, null, 0, width);
        int[] dst = new int[src.length];

        BlurAction blurAction = new BlurAction(src, 0, src.length, dst, blurWidth, threshold);

        long startTime = System.currentTimeMillis();
        ForkJoinPool.commonPool().invoke(blurAction);
        long endTime = System.currentTimeMillis();

        BufferedImage dstImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        dstImage.setRGB(0, 0, width, height, dst, 0, width);

        System.out.println("Threshold: " + threshold);
        System.out.println("Array size: " + src.length);
        System.out.println("Available processors count: " + Runtime.getRuntime().availableProcessors());
        System.out.println("Image blur took " + (endTime - startTime) + " milliseconds.");

        return dstImage;
    }

    private static BufferedImage convolution(BufferedImage srcImage, int[] convolutionMatrix) {
        int width = srcImage.getWidth();
        int height = srcImage.getHeight();
        int threshold = 1000;

        int[] src = srcImage.getRGB(0, 0, width, height, null, 0, width);
        int[] dst = new int[src.length];

        ConvolutionAction convolutionAction = new ConvolutionAction(srcImage, 0, 0, width, height, dst, convolutionMatrix, threshold);

        long startTime = System.currentTimeMillis();
        ForkJoinPool.commonPool().invoke(convolutionAction);
        long endTime = System.currentTimeMillis();

        BufferedImage dstImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
        dstImage.setRGB(0, 0, width, height, dst, 0, width);

        System.out.println("Threshold: " + threshold);
        System.out.println("Array size: " + src.length);
        System.out.println("Available processors count: " + Runtime.getRuntime().availableProcessors());
        System.out.println("Convolution took " + (endTime - startTime) + " milliseconds.");

        return dstImage;
    }

}
