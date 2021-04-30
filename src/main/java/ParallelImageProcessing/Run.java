package ParallelImageProcessing;

import ParallelImageProcessing.transformations.Filtering;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Objects;

import static ParallelImageProcessing.transformations.Filtering.blur;
import static ParallelImageProcessing.transformations.Filtering.sobel;


public class Run {

    public static void main(String[] args) {
        String resourceName = "red_tulips.jpg";
        String blurDestinationPath = "results/blurred_red_tulips.png";
        String sobelDestinationPath = "results/sobel_red_tulips.png";

        try {
            BufferedImage inputImage = getImageFromResources(resourceName);
            BufferedImage blurredImage = blur(inputImage, 9);
            BufferedImage sobelImage = sobel(inputImage, Filtering.SobelType.VERTICAL);
            saveImage(blurredImage, blurDestinationPath);
            saveImage(sobelImage, sobelDestinationPath);
        } catch (Exception e) {
            System.err.println("Cannot process image: " + e.getMessage());
        }
    }

    private static BufferedImage getImageFromResources(String resourceName) throws IOException {
        URL res = Run.class.getClassLoader().getResource(resourceName);
        try {
            if (Objects.isNull(res)) {
                throw new IOException("Cannot receive resource");
            }
            File resourceFile = Paths.get(res.toURI()).toFile();
            return ImageIO.read(resourceFile);
        } catch (URISyntaxException e) {
            throw new IOException("Cannot create URI");
        }
    }

    private static void saveImage(BufferedImage outputImage, String outputFilePath) throws IOException {
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
