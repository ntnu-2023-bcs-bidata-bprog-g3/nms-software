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
    public static boolean writeToFile(byte[] data, String path) {
        // Check if directory exists, if not create it
        if (path.contains("/") || path.contains("\\")) {
            Path directory = Paths.get(path.substring(0, path.lastIndexOf("/")));
            if (Files.notExists(directory)) {
                try {
                    Files.createDirectories(directory);
                } catch (IOException e) {
                    return false;
                }
            }
        }
        // Write to file
        try {
            FileOutputStream fileOut = new FileOutputStream(path);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(data);
            out.flush();        // with or without flush??
            out.close();
            fileOut.flush();    // with or without flush??
            fileOut.close();
        } catch (IOException i) {
            return false;
        }
        return true;
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
