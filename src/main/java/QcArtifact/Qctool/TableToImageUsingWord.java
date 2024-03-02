package QcArtifact.Qctool;

import com.aspose.words.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Base64;
import java.util.Collections;
import java.util.Map;


public class TableToImageUsingWord implements TableToImageConverterutil {
    private static final Map<String, String> IMAGE_MIME_TYPES = Collections.singletonMap("png", "image/png");
    @Override
    public Image renderTableAsImage(Table table) {
        try {
            byte[] image = renderShape(table);
            String format = "png";
            InputStream is = new ByteArrayInputStream(image);
            BufferedImage out = ImageIO.read(is);
            int width = out.getWidth();
            int height = out.getHeight();
            return createImage(image, format, width, height);
        } catch (Exception e) {
            throw new ConversionException(e);
        }
    }
    private Image createImage(byte[] imageBuffer, String format, int width, int height) {

        String base64Image = imgToBase64String(imageBuffer);
        final Image image = new Image();
        image.setFormat(format);
        String mimeType = IMAGE_MIME_TYPES.get(format);
        image.setWidth(width + "px");
        image.setHeight(height + "px");
        image.setSrc("data:" + mimeType + ";base64, " + base64Image);
        image.setBase64(base64Image);
        return image;
    }

    private static byte[] renderShape(Table table) throws Exception {
        Document doc = ((Document) table.getDocument()).deepClone();
        table = (Table) doc.getChild(NodeType.TABLE, table.getDocument().getChildNodes(NodeType.ANY, true).indexOf(table), true);
        ByteArrayOutputStream outputStream1 = new ByteArrayOutputStream();
        doc.save(outputStream1, SaveFormat.PNG);
        ByteArrayInputStream out = new ByteArrayInputStream(outputStream1.toByteArray());
        BufferedImage tempInputBufferImage = ImageIO.read(out);
        Rectangle cropRectangle = FindBoundingBoxAroundNode(tempInputBufferImage);
        BufferedImage tempOutputBufferImage = tempInputBufferImage.getSubimage(cropRectangle.x, cropRectangle.y, cropRectangle.width, cropRectangle.height);
        ByteArrayOutputStream outputStream2 = new ByteArrayOutputStream();
        ImageIO.write(tempOutputBufferImage, "PNG", outputStream2);
        return outputStream2.toByteArray();
    }
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
        String a = "C:\\Users\\tgaur\\OneDrive\\Desktop\\Qctool\\Qctool\\src\\main\\resources\\output.docx";
        Document doc = new Document(a);
        NodeCollection<Table> tables = doc.getChildNodes(NodeType.TABLE, true);
        System.out.println(tables.getCount());
        TableToImageUsingWord tm = new TableToImageUsingWord();
        for (int i = 0; i < tables.getCount(); i++) {
            Table table = (Table) tables.get(i);
            Image img = tm.renderTableAsImage(table);
            System.out.println(img.getHeight() +" "+img.getWidth());
            System.out.println(img.getBase64());
            System.out.println("______________________________________________________");
            System.out.println("Parsing completed. Output saved to: ");
        }
    }
}
