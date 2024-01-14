package QcArtifact.Qctool;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.zip.*;

public class ImageCompression {

    // Compresses a byte array using Deflater
    public static byte[] compress(byte[] data,int compressionLevel) {
        Deflater deflater = new Deflater(compressionLevel);
        deflater.setInput(data);
        deflater.finish();

        byte[] buffer = new byte[1024];
        int bytesRead;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        while (!deflater.finished()) {
            bytesRead = deflater.deflate(buffer);
            outputStream.write(buffer, 0, bytesRead);
        }

        deflater.end();
        return outputStream.toByteArray();
    }

    // Decompresses a byte array using Inflater
    public static byte[] decompress(byte[] compressedData) {
        try {
            Inflater inflater = new Inflater();
            inflater.setInput(compressedData);

            byte[] buffer = new byte[1024];
            int bytesRead;
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            while (!inflater.finished()) {
                bytesRead = inflater.inflate(buffer);
                outputStream.write(buffer, 0, bytesRead);
            }

            inflater.end();
            return outputStream.toByteArray();
        } catch (DataFormatException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Convert Base64 string to byte array
    public static byte[] base64ToBytes(String base64String) {
        return Base64.getDecoder().decode(base64String);
    }

    // Convert byte array to Base64 string
    public static String bytesToBase64(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }


}

