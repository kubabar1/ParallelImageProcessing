package ParallelImageProcessing;

import ParallelImageProcessing.transformations.Filtering;
import org.apache.commons.io.FilenameUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static ParallelImageProcessing.transformations.Filtering.*;


public class Run {

    public static void main(String[] args) {
        Run run = new Run();
        String fileName = "lena_gray.jpg";
        String outputFileName = FilenameUtils.removeExtension(fileName) + ".png";
        String resourcePath = "/" + fileName;
        String blurDestinationPath = "results/blurred_" + outputFileName;
        String sobelHorizontalDestinationPath = "results/sobel_horizontal_" + outputFileName;
        String sobelVerticalDestinationPath = "results/sobel_vertical_" + outputFileName;
        String laplacianDestinationPath = "results/laplacian_" + outputFileName;
        String laplacianDiagonalDestinationPath = "results/laplacian_diagonal_" + outputFileName;
        String laplacianGaussianDestinationPath = "results/laplacian_gaussian_" + outputFileName;

        try {
            BufferedImage inputImage = run.getImageFromResources(resourcePath);

            BufferedImage blurredImage = blur(inputImage, 9);
            BufferedImage sobelHorizontalImage = sobel(inputImage, Filtering.SobelType.HORIZONTAL);
            BufferedImage sobelVerticalImage = sobel(inputImage, Filtering.SobelType.VERTICAL);
            BufferedImage laplacianImage = laplacian(inputImage, Filtering.LaplacianType.LAPLACIAN);
            BufferedImage laplacianDiagonalImage = laplacian(inputImage, Filtering.LaplacianType.LAPLACIAN_DIAGONAL);
            BufferedImage laplacianGaussianImage = laplacian(inputImage, Filtering.LaplacianType.LAPLACIAN_GAUSSIAN);

            run.saveImage(blurredImage, blurDestinationPath);
            run.saveImage(sobelHorizontalImage, sobelHorizontalDestinationPath);
            run.saveImage(sobelVerticalImage, sobelVerticalDestinationPath);
            run.saveImage(laplacianImage, laplacianDestinationPath);
            run.saveImage(laplacianDiagonalImage, laplacianDiagonalDestinationPath);
            run.saveImage(laplacianGaussianImage, laplacianGaussianDestinationPath);
        } catch (Exception e) {
            System.err.println("Cannot process image: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public BufferedImage getImageFromResources(String resourceName) throws IOException {
        return ImageIO.read(getClass().getResourceAsStream(resourceName));
    }

    public void saveImage(BufferedImage outputImage, String outputFilePath) throws IOException {
        File dstFile = new File(outputFilePath);
        File parent = dstFile.getParentFile();
        if (!parent.exists() && !parent.mkdirs()) {
            throw new IOException("Couldn't create dir: " + parent);
        }
        try {
            ImageIO.write(outputImage, "png", dstFile);
        } catch (IOException e) {
            throw new IOException("Cannot write image to file");
        }
    }

}
