package no.ntnu.nms.persistence;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import no.ntnu.nms.domain_model.PoolRegistry;
/**
 * Utility class for serializing and deserializing PoolRegistry objects.
 */
public class SerializationUtil {

        /**
         * Serializes the given PoolRegistry object to the given file.
         * @param registry The PoolRegistry object to serialize.
         * @param file The file to serialize to.
         * @throws IOException is thrown if the file cannot be created.
         */
        public static void serialize(PoolRegistry registry, String file) throws IOException {
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(registry);
    
            fos.close();
        }

        /**
         * Deserializes a PoolRegistry object from the given file.
         * @param file The file to deserialize from.
         * @return The deserialized PoolRegistry object.
         * @throws IOException is thrown if the file does not exist.
         * @throws ClassNotFoundException is thrown if the file does not contain a PoolRegistry object.
         */
        public static PoolRegistry deserialize(String file) throws IOException, ClassNotFoundException {
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            Object obj = ois.readObject();
            ois.close();

            return (PoolRegistry) obj;
        }
}
