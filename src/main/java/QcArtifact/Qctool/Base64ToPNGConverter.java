package QcArtifact.Qctool;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

import javax.imageio.ImageIO;

public class Base64ToPNGConverter {

    public static void convertBase64ToPNG(String base64Image, String outputPath) {
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(base64Image);

            BufferedImage image = ImageIO.read(new ByteArrayInputStream(decodedBytes));
            saveImage(image, outputPath);

            System.out.println("Conversion successful. PNG image saved at: " + outputPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveImage(BufferedImage image, String outputPath) throws IOException {
        Path outputFilePath = Paths.get(outputPath);
        Files.createDirectories(outputFilePath.getParent());

        try (FileOutputStream fos = new FileOutputStream(outputFilePath.toFile())) {
            ImageIO.write(image, "png", fos);
        }
    }
}
