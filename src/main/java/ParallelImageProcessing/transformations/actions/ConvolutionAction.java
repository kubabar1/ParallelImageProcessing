package ParallelImageProcessing.transformations.actions;

import java.awt.image.BufferedImage;
import java.util.concurrent.RecursiveAction;

import static java.lang.Math.sqrt;

public class ConvolutionAction extends RecursiveAction {

    private final BufferedImage source;

    private final int startX;

    private final int startY;

    private final int lengthX;

    private final int lengthY;

    private final int[] dst;

    private final int[] sobelMatrix;

    private final int threshold;

    private static final int black = 0xff000000;

    public ConvolutionAction(BufferedImage source, int startX, int startY, int lengthX, int lengthY, int[] dst, int[] sobelMatrix, int threshold) {
        this.source = source;
        this.startX = startX;
        this.startY = startY;
        this.lengthX = lengthX;
        this.lengthY = lengthY;
        this.dst = dst;
        this.sobelMatrix = sobelMatrix;
        this.threshold = threshold;
    }

    private void computeDirectly() {
        int sourceWidth = this.source.getWidth();
        int sourceHeight = this.source.getHeight();
        int sidePixels = ((int) sqrt(sobelMatrix.length) - 1) / 2;
        int sobelMatrixWidth = (int) sqrt(sobelMatrix.length);

        for (int idY = this.startY; idY < this.startY + this.lengthY; idY++) {
            for (int idX = this.startX; idX < this.startX + this.lengthX; idX++) {
                float reed = 0, green = 0, blue = 0;
                for (int miY = -sidePixels; miY <= sidePixels; miY++) {
                    for (int miX = -sidePixels; miX <= sidePixels; miX++) {
                        int mmiX = idX + miX;
                        int mmiY = idY + miY;
                        int pixel = mmiX < 0 || mmiX >= sourceWidth || mmiY < 0 || mmiY >= sourceHeight ? black : this.source.getRGB(mmiX, mmiY);
                        int sobelPixel = this.sobelMatrix[(miX + sidePixels) * sobelMatrixWidth + (miY + sidePixels)];
                        reed += (float) ((pixel & 0x00ff0000) >> 16) * sobelPixel;
                        green += (float) ((pixel & 0x0000ff00) >> 8) * sobelPixel;
                        blue += (float) ((pixel & 0x000000ff)) * sobelPixel; // >> 0
                    }
                }
                int destinationPixel = (0xff000000)
                        | (((int) reed) << 16)
                        | (((int) green) << 8)
                        | (((int) blue));
                this.dst[idY * sourceWidth + idX] = destinationPixel;
            }
        }
    }

    /**
     * Perform sobel directly or split it into four smaller tasks.
     */
    @Override
    protected void compute() {
        if (this.lengthX < this.threshold || this.lengthY < this.threshold) {
            computeDirectly();
        } else {
            int splitX = this.lengthX / 2;
            int splitY = this.lengthY / 2;
            invokeAll(
                    new ConvolutionAction(this.source, this.startX, this.startY, splitX, splitY, this.dst, this.sobelMatrix, this.threshold),
                    new ConvolutionAction(this.source, this.startX + splitX, this.startY, this.lengthX - splitX, splitY, this.dst, this.sobelMatrix, this.threshold),
                    new ConvolutionAction(this.source, this.startX, this.startY + splitY, splitX, this.lengthY - splitY, this.dst, this.sobelMatrix, this.threshold),
                    new ConvolutionAction(this.source, this.startX + splitX, this.startY + splitY, this.lengthX - splitX, this.lengthY - splitY, this.dst, this.sobelMatrix, this.threshold)
            );
        }
    }

}
