package ParallelImageProcessing;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.logging.Logger;

import static ParallelImageProcessing.ForkBlur.blur;

public class Run {

    private static final Logger logger = Logger.getLogger(Run.class.getName());

    public static void main(String[] args) throws Exception {
        String sourceImagePath = "red-tulips.jpg";
        String outputFormat = "png";
        String destinationImageName = "results/blurred-tulips." + outputFormat;

        BufferedImage sourceImage = readImage(sourceImagePath);
        logger.info(() -> "Source image  " + sourceImagePath + " loaded.");

        BufferedImage blurredImage = blur(sourceImage);
        saveImage(blurredImage, outputFormat, destinationImageName);
        logger.info(() -> "Saved blurred image as " + destinationImageName);
    }

    private static BufferedImage readImage(String imagePath) throws URISyntaxException, IOException {
        URL res = Run.class.getClassLoader().getResource(imagePath);
        File resourceFile = Paths
                .get(Objects.requireNonNull(res, "Resource " + imagePath + " does not exist").toURI())
                .toFile();
        return ImageIO.read(resourceFile);
    }

    private static void saveImage(BufferedImage image, String format, String path) throws IOException {
        File dstFile = new File(path);
        File parent = dstFile.getParentFile();
        if (!parent.exists() && !parent.mkdirs()) {
            throw new IllegalStateException("Couldn't create dir: " + parent);
        }
        ImageIO.write(image, format, dstFile);
    }


}
