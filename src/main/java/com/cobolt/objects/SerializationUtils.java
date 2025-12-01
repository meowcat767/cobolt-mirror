package com.cobolt.objects;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Object serialization and deserialization utilities
 */
public class SerializationUtils {

    /**
     * Serialize object to bytes
     */
    public static byte[] serialize(Serializable obj) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(obj);
        }
        return baos.toByteArray();
    }

    /**
     * Deserialize object from bytes
     */
    @SuppressWarnings("unchecked")
    public static <T> T deserialize(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        try (ObjectInputStream ois = new ObjectInputStream(bais)) {
            return (T) ois.readObject();
        }
    }

    /**
     * Write lines to output stream
     */
    public static void writeLines(OutputStream os, String... lines) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
        for (String line : lines) {
            writer.write(line);
            writer.newLine();
        }
        writer.flush();
    }

    /**
     * Read all lines from input stream
     */
    public static String readAllLines(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        return sb.toString();
    }
}
