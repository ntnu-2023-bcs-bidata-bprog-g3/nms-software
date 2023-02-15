package no.ntnu.nms.file_handler;
import no.ntnu.nms.exception.FileHandlerException;
import no.ntnu.nms.logging.Logging;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

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
            Logging.getLogger().warning("Failed to create directory: " + path + ". " + e.getMessage());
            throw new FileHandlerException("Failed to create directory: " + path + ". " + e.getMessage());
        }

        // Write to file
        try (FileOutputStream fileOut = new FileOutputStream(path);
ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
            out.writeObject(data);
        } catch (IOException e) {
            Logging.getLogger().warning("Failed to write to file: " + e.getMessage());
            throw new FileHandlerException("Failed to write to file: " + e.getMessage());
        }
    }

  /**
     * Reads a byte array from a file.
     * @return The byte array read from the file.
     */
    public static byte[] readFromFile(String path) throws FileHandlerException {
        try (FileInputStream fileIn = new FileInputStream(path); ObjectInputStream in = new ObjectInputStream(fileIn)) {
            return (byte[]) in.readObject();
        } catch (IOException e) {
            Logging.getLogger().warning("Failed to open and read file: " + e.getMessage());
            throw new FileHandlerException("Failed to open and read file: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            Logging.getLogger().warning("File contains serialized object of non-Poolregiostry: " + e.getMessage());
            throw new FileHandlerException("File contains serialized object of non-Poolregiostry: " + e.getMessage());
        }
    }  

    /**
     * Creates a backup of a file
     * @param path The path to the file to back up
     * @throws IOException if there is an error writing the backup file
     */
    public static void backup(String path) throws IOException {
        Path source = Path.of(path);
        if (Files.exists(source)) {
            Path backupPath = Path.of(path + ".bak");
            Files.move(source, backupPath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public static void deleteBackup(String path) {
        Path source = Path.of(path);
        if (Files.exists(source)) {
            Path backupPath = Path.of(path + ".bak");
            try {
                Files.deleteIfExists(backupPath);
            } catch (IOException e) {
                Logging.getLogger().info("Unable to delete backup. It wont affect core functionality");
            }
        }
    }

}
