import no.ntnu.nms.domain_model.Pool;
import no.ntnu.nms.domain_model.PoolRegistry;
import no.ntnu.nms.persistence.Controller;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PoolControllerAndSerializationTest {


    /**
     * Tests the pool controller and serialization.
     */
    @Test
    public void testPoolControllerAndSerialization() {
        PoolRegistry originalPoolreg = PoolRegistry.getInstance();
        originalPoolreg.addPool(new Pool("test1", 23
                , "A tester pool"));
        originalPoolreg.addPool(new Pool("test2", 23
                , "A tester pool"));
        originalPoolreg.addPool(new Pool("test3", 23
                , "A tester pool"));

        assertEquals("A tester pool", originalPoolreg.getPoolByMediaFunction("test1")
                .getDescription());

        Controller.savePoolRegAndChecksum(originalPoolreg);

        PoolRegistry secondPoolreg = Controller.loadPoolRegAndCheckChecksum();
        if (secondPoolreg == null) {
            fail("Loading failed");
        }

        assertEquals("A tester pool", secondPoolreg.getPoolByMediaFunction("test1")
                .getDescription());

        secondPoolreg.getPoolByMediaFunction("test1").setDescription("A new description");

        assertEquals("A new description", secondPoolreg.getPoolByMediaFunction("test1")
                .getDescription());

        Controller.savePoolRegAndChecksum(secondPoolreg);

        PoolRegistry thirdPoolreg = Controller.loadPoolRegAndCheckChecksum();
        if (thirdPoolreg == null) {
            fail("Loading failed");
        }

        assertEquals("A new description", thirdPoolreg.getPoolByMediaFunction("test1")
                .getDescription());
    }
}
