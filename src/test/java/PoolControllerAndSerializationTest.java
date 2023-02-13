//TODO: Fix this test
public class PoolControllerAndSerializationTest {

    /**
     * Test that the pool registry can be loaded from file and that the state of the registry is
     * preserved because of the change listeners.
     */
    /*
    @Test
    public void listenerAndStateChangeTest() {
        PoolRegistry originalPoolreg = PoolRegistry.getInstance();
        originalPoolreg.addPool(new Pool("test1", 23,
                "A tester pool"));

        assertEquals("A tester pool", originalPoolreg.getPoolByMediaFunction("test1")
                .getDescription());

        originalPoolreg.getPoolByMediaFunction("test1").setDescription("A new description");

        PoolRegistry secondPoolreg = null;
        Controller.loadPoolRegAndCheckChecksum();
        if (secondPoolreg == null) {
            fail("Loading of PoolReg failed");
        }

        assertNotSame(originalPoolreg, secondPoolreg);

        assertEquals("A new description", secondPoolreg.getPoolByMediaFunction("test1")
                .getDescription());
    }
    */
}
