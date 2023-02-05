import no.ntnu.nms.domain_model.Pool;
import no.ntnu.nms.domain_model.PoolRegistry;
import no.ntnu.nms.persistence.Controller;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PoolControllerAndSerializationTest {

    /**
     * Test that the pool registry can be loaded from file and that the state of the registry is
     * preserved because of the change listeners.
     */
    @Test
    public void listenerAndStateChangeTest() {
        PoolRegistry originalPoolreg = PoolRegistry.getInstance();
        originalPoolreg.addPool(new Pool("test1", 23,
                "A tester pool"));

        assertEquals("A tester pool", originalPoolreg.getPoolByMediaFunction("test1")
                .getDescription());

        originalPoolreg.getPoolByMediaFunction("test1").setDescription("A new description");

        PoolRegistry secondPoolreg = Controller.loadPoolRegAndCheckChecksum();
        if (secondPoolreg == null) {
            fail("Loading of PoolReg failed");
        }

        assertEquals("A new description", secondPoolreg.getPoolByMediaFunction("test1")
                .getDescription());
    }
}
