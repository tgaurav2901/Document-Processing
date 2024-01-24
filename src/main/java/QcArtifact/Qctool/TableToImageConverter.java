package QcArtifact.Qctool;
import com.aspose.words.*;
import com.aspose.words.Shape;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.*;
import java.util.List;
import static QcArtifact.Qctool.Base64ToPNGConverter.convertBase64ToPNG;
import static QcArtifact.Qctool.ImageCompression.*;
import static com.aspose.words.NodeType.SECTION;

public class TableToImageConverter implements TableToImageConverterutil {

    private static final Logger LOG = LoggerFactory.getLogger(TableToImageConverter.class);
    private static final Map<String, String> IMAGE_MIME_TYPES = Collections.singletonMap("png", "image/png");


    public TableToImageConverter() {

    }

    @Override
    public Image renderTableAsImage(Table table) {
        try {
            //Shape is generated
            Shape imageBuffer = generateShape(table);
            byte[] image = renderShape(imageBuffer);
            String format = "png";
            InputStream is = new ByteArrayInputStream(image);
            BufferedImage out = ImageIO.read(is);
            int width = out.getWidth();
            int height = out.getHeight();
            return createImage(image, format, width, height, imageBuffer.getAlternativeText());

        } catch (Exception e) {
            throw new ConversionException(e);
        }
    }

    private Image createImage(byte[] imageBuffer, String format, int width, int height, String altText) {

        String base64Image = imgToBase64String(imageBuffer);

        final Image image = new Image();
        image.setFormat(format);
        String mimeType = IMAGE_MIME_TYPES.get(format);
        image.setWidth(width + "px");
        image.setHeight(height + "px");
        image.setSrc("data:" + mimeType + ";base64, " + base64Image);
        image.setBase64(base64Image);
        image.setAltText(altText);

        return image;
    }


    public static Shape generateShape(Table table) throws Exception {
        try {
            Document doc = ((Document) table.getDocument()).deepClone();
            table = (Table) doc.getChild(NodeType.ANY, table.getDocument().getChildNodes(NodeType.ANY, true).indexOf(table), true);

            // Create a temporary shape to store the target node in.
            Shape shape = new Shape(table.getDocument(), ShapeType.TEXT_BOX);
            Section parentSection = (Section) table.getAncestor(SECTION);
            // Assume that the table cannot be larger than the page in size.
            shape.setWidth(parentSection.getPageSetup().getPageWidth());
            shape.setHeight(parentSection.getPageSetup().getPageHeight());
            shape.setFillColor(new Color(255, 255, 255));
            shape.setStroked(false);

            // Add a copy of the table to the shape.
            shape.appendChild(table.deepClone(true));
            // Add the shape to the document tree to have it rendered.
            parentSection.getBody().getFirstParagraph().appendChild(shape);
            return shape;
        } catch (Exception e) {
            throw new ConversionException(e);
        }

    }

    private static byte[] renderShape(Shape shape) throws Exception {
        try {
            ImageSaveOptions options = new ImageSaveOptions(SaveFormat.PNG);
            options.getMetafileRenderingOptions().setRenderingMode(MetafileRenderingMode.BITMAP);
            options.setResolution(100);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            shape.getShapeRenderer().save(outputStream, options);
            ByteArrayInputStream out = new ByteArrayInputStream(outputStream.toByteArray());
            BufferedImage tempInputBufferImage = ImageIO.read(out);
            Rectangle cropRectangle = FindBoundingBoxAroundNode(tempInputBufferImage);
            BufferedImage tempOutputBufferImage = tempInputBufferImage.getSubimage(cropRectangle.x, cropRectangle.y, cropRectangle.width, cropRectangle.height);
            ByteArrayOutputStream outputStream1 = new ByteArrayOutputStream();
            ImageIO.write(tempOutputBufferImage, "PNG", outputStream1);
            return outputStream1.toByteArray();
        } catch (Exception e) {
            throw new ConversionException(e);
        }

    }

    // Cropping the unwanted space around the table image
    public static Rectangle FindBoundingBoxAroundNode(BufferedImage originalBitmap) {
        Point min = new Point(Integer.MAX_VALUE, Integer.MAX_VALUE);
        Point max = new Point(Integer.MIN_VALUE, Integer.MIN_VALUE);
        for (int x = 0; x < originalBitmap.getWidth(); ++x) {
            for (int y = 0; y < originalBitmap.getHeight(); ++y) {
                int argb = originalBitmap.getRGB(x, y);
                // searching for non-white Pixels
                if (argb != new Color(255, 255, 255).getRGB()) {
                    min.x = Math.min(x, min.x);
                    min.y = Math.min(y, min.y);
                    max.x = Math.max(x, max.x);
                    max.y = Math.max(y, max.y);

                }
            }
        }

        return new Rectangle(min.x, min.y, (max.x - min.x) + 1, (max.y - min.y) + 1);
    }


    private static String imgToBase64String(byte[] img) {
        return new String(Base64.getEncoder().encode(img));
    }

    public static void main(String[] args) throws Exception {
        String a = "C:\\Users\\tgaur\\OneDrive\\Desktop\\Qctool\\Qctool\\src\\main\\resources\\new.docx";
        Document doc = new Document(a);
        NodeCollection<Table> tables = doc.getChildNodes(NodeType.TABLE, true);
        System.out.println(tables.getCount());
        TableToImageConverter ty = new TableToImageConverter();
        DocumentBuilder builder = new DocumentBuilder();
        for (int i = 0; i < tables.getCount(); i++) {
            Table table = (Table) tables.get(i);
            Image img = ty.renderTableAsImage(table);
            byte[] originalBytes = base64ToBytes(img.getBase64());
            byte[] compressedBytes = compress(originalBytes,1);
            // Decompress the compressed byte array
            byte[] decompressedBytes = decompress(compressedBytes);
            // Convert the decompressed byte array back to Base64 string
            String decompressedBase64 = bytesToBase64(decompressedBytes);
            System.out.println(img.getHeight() +" "+img.getWidth());
            convertBase64ToPNG(decompressedBase64, "C:\\Users\\tgaur\\OneDrive\\Desktop\\Qctool\\Qctool\\src\\main\\resources" + i);
           System.out.println(img.getBase64());
            System.out.println("______________________________________________________");
            System.out.println("Parsing completed. Output saved to: ");
        }
    }
}