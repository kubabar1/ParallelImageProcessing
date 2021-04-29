package ParallelImageProcessing;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Objects;

import static ParallelImageProcessing.transformations.Filtering.blur;


public class Run {

    public static void main(String[] args) {
        String resourceName = "red-tulips.jpg";
        String destinationPath = "results/blurred-tulips.png";

        try {
            BufferedImage inputImage = getImageFromResources(resourceName);
            BufferedImage blurredImage = blur(inputImage, 19);
            saveImage(blurredImage, destinationPath);
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
