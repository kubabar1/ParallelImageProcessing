package ParallelImageProcessing.transformations.actions;

import java.util.concurrent.RecursiveAction;

public class BlurAction extends RecursiveAction {

    private final int[] source;

    private final int start;

    private final int length;

    private final int[] dst;

    private final int blurWidth;

    private final int threshold;


    public BlurAction(int[] source, int start, int length, int[] dst, int blurWidth, int threshold) {
        this.source = source;
        this.start = start;
        this.length = length;
        this.dst = dst;
        this.blurWidth = blurWidth;
        this.threshold = threshold;
    }

    protected void computeDirectly() {
        int sidePixels = (this.blurWidth - 1) / 2;
        for (int index = this.start; index < this.start + this.length; index++) {
            float reed = 0, green = 0, blue = 0;
            for (int mi = -sidePixels; mi <= sidePixels; mi++) {
                int idx = Math.min(Math.max(mi + index, 0), this.source.length - 1);
                int pixel = this.source[idx];
                reed += (float) ((pixel & 0x00ff0000) >> 16) / this.blurWidth;
                green += (float) ((pixel & 0x0000ff00) >> 8) / this.blurWidth;
                blue += (float) ((pixel & 0x000000ff)) / this.blurWidth; // >> 0
            }

            int destinationPixel = (0xff000000)
                    | (((int) reed) << 16)
                    | (((int) green) << 8)
                    | (((int) blue)); // << 0
            this.dst[index] = destinationPixel;
        }
    }


    @Override
    protected void compute() {
        if (this.length < this.threshold) {
            computeDirectly();
        } else {
            int split = length / 2;
            invokeAll(new BlurAction(this.source, this.start, split, this.dst, this.blurWidth, this.threshold),
                    new BlurAction(this.source, this.start + split, this.length - split, this.dst, this.blurWidth, this.threshold));
        }
    }

}
