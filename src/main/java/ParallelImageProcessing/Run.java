package ParallelImageProcessing;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.nio.file.Paths;

import static ParallelImageProcessing.ForkBlur.blur;

public class Run {

//    public static void main(String[] args) {
//        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
//        config.width = 128;
//        config.height = 256;
//        config.title = "Mandelbrot";
//        new LwjglApplication(new Thread7ForkJoinMadelbrot(), config);
//    }

    // Plumbing follows.
    public static void main(String[] args) throws Exception {
        String resourceName = "red-tulips.jpg";
        URL res = Run.class.getClassLoader().getResource(resourceName);
        File resourceFile = Paths.get(res.toURI()).toFile();

        BufferedImage image = ImageIO.read(resourceFile);

        System.out.println("Source image: " + resourceName);

        BufferedImage blurredImage = blur(image);

        String dstName = "results/blurred-tulips.png";
        File dstFile = new File(dstName);
        File parent = dstFile.getParentFile();
        if (!parent.exists() && !parent.mkdirs()) {
            throw new IllegalStateException("Couldn't create dir: " + parent);
        }
        ImageIO.write(blurredImage, "png", dstFile);

        System.out.println("Output image: " + dstName);
    }


}
