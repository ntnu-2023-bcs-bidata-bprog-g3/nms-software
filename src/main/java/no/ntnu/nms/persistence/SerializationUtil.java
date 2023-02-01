package no.ntnu.nms.persistence;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import no.ntnu.nms.domain_model.PoolRegistry;

public class SerializationUtil {
    
        public static void serialize(PoolRegistry registry, String file) throws IOException {
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(registry);
    
            fos.close();
        }

        public static PoolRegistry deserialize(String file) throws IOException, ClassNotFoundException {
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            Object obj = ois.readObject();
            ois.close();

            return (PoolRegistry) obj;
        }
}
