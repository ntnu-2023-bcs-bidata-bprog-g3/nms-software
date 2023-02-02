package no.ntnu.nms.persistence;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
/**
 * Utility class for serializing and deserializing PoolRegistry objects.
 */
public class FileHandler {

    /**
     * Writes a byte array to a file.
     * @param data The byte array to write.
     */
    public static void writeToFile(byte[] data) {
        try {
            FileOutputStream fileOut = new FileOutputStream("poolreg.ser");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(data);
            out.close();
            fileOut.close();
        } catch (IOException i) {
            return;
        }
    }
    /**
     * Reads a byte array from a file.
     * @return The byte array read from the file.
     */
    public static byte[] readFromFile() {
        try {
            FileInputStream fileIn = new FileInputStream("poolreg.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            byte[] data = (byte[]) in.readObject();
            in.close();
            fileIn.close();
            return data;
        } catch (IOException i) {
            return null;
        } catch (ClassNotFoundException c) {
            return null;
        }
    }
}
