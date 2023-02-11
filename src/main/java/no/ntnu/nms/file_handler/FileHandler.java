package no.ntnu.nms.file_handler;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Utility class for serializing and deserializing PoolRegistry objects.
 */
public class FileHandler {

    /**
     * Writes a byte array to a file.
     * @param data The byte array to write.
     */
    public static void writeToFile(byte[] data, String path) throws FileHandlerException {
        try {
            // Check if directory exists, if not create it
            Path directory = Paths.get(path.substring(0, path.lastIndexOf(File.separator)));
            if (Files.notExists(directory)) {
                Files.createDirectories(directory);
            }
        } catch (IOException e) {
            throw new FileHandlerException("Failed to create directory: " + path + ". " + e.getMessage());
        }

        // Write to file
        try (FileOutputStream fileOut = new FileOutputStream(path);
ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
            out.writeObject(data);
        } catch (IOException e) {
            //TODO: Add logging.
            throw new FileHandlerException("Failed to write to file: " + e.getMessage());
        }
    }

    /**
     * Reads a byte array from a file.
     * @return The byte array read from the file.
     */
    public static byte[] readFromFile(String path) {
        try {
            FileInputStream fileIn = new FileInputStream(path);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            byte[] data = (byte[]) in.readObject();
            in.close();
            fileIn.close();
            return data;
        } catch (IOException | ClassNotFoundException i) {
            return null;
        }
    }
}
