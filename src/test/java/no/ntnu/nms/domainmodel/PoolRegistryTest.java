package no.ntnu.nms.domainmodel;

import no.ntnu.nms.Constants;
import no.ntnu.nms.filehandler.FileHandler;
import no.ntnu.nms.logging.Logging;

import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

public class PoolRegistryTest {

    private static final String TEST_DIR = Constants.TEST_FILES_PATH + "poolreg/";

    @AfterEach
        public void tearDown() {
        PoolRegistry.getInstance(true).clear();
    }

    @BeforeAll
    public static void init() {
        try {
            Logging.setUpLogger("ALL");
        } catch (IOException ignore) {}
    }

    @BeforeEach
    public void setUp() {
        PoolRegistry.init(TEST_DIR + "poolreg.ser");
    }

    @AfterAll
    public static void deInit() {
        try {
            Files.deleteIfExists(Path.of(TEST_DIR + "poolreg.ser"));
            Files.deleteIfExists(Path.of(TEST_DIR + "poolreg.ser.md5"));
            Files.deleteIfExists(Path.of(TEST_DIR));
        } catch (IOException ignore) {}
    }

    @Test
    public void TestInit() {
        assertNotNull(PoolRegistry.getInstance(true));
        assertTrue(Files.exists(Path.of(TEST_DIR + "poolreg.ser")));
    }

    @Test
    public void TestGetInstance() {
        assertNotNull(PoolRegistry.getInstance(true));
    }

    @Test
    public void TestSingleton() {
        PoolRegistry poolRegistry = PoolRegistry.getInstance(true);
        PoolRegistry poolRegistry2 = PoolRegistry.getInstance(true);
        assertEquals(poolRegistry, poolRegistry2);
    }

    @Test
    public void TestAddPool() {
        PoolRegistry poolRegistry = PoolRegistry.getInstance(true);

        assertEquals(0, poolRegistry.getPoolCount());

        poolRegistry.addPool(new Pool("test", 2, "test"));

        assertEquals(1, poolRegistry.getPoolCount());
    }

    @Test
    public void TestRemovePool() {
        PoolRegistry poolRegistry = PoolRegistry.getInstance(true);
        assertEquals(0, poolRegistry.getPoolCount());
        Pool pool = new Pool("test", 2, "test");

        poolRegistry.addPool(pool);
        assertEquals(1, poolRegistry.getPoolCount());
        poolRegistry.removePool(pool);
        assertEquals(0, poolRegistry.getPoolCount());
    }

    @Test
    public void TestGetPoolList() {
        PoolRegistry poolRegistry = PoolRegistry.getInstance(true);
        Pool pool = new Pool("test", 2, "test");
        poolRegistry.addPool(pool);
        Iterator<Pool> poolIterator = poolRegistry.getPoolList();

        assertTrue(poolIterator.hasNext());
        assertEquals(pool, poolIterator.next());

        assertFalse(poolIterator.hasNext());
    }

    @Test
    public void TestGetPoolCount() {
        PoolRegistry poolRegistry = PoolRegistry.getInstance(true);
        assertEquals(0, poolRegistry.getPoolCount());
        poolRegistry.addPool(new Pool("test", 2, "test"));
        assertEquals(1, poolRegistry.getPoolCount());
    }

    @Test
    public void TestContainsPool() {
        PoolRegistry poolRegistry = PoolRegistry.getInstance(true);
        Pool pool = new Pool("test", 2, "test");
        Pool pool2 = new Pool("test2", 2, "test2");
        poolRegistry.addPool(pool);

        assertTrue(poolRegistry.containsPool(pool));
        assertFalse(poolRegistry.containsPool(pool2));
    }

    @Test
    public void TestContainsPoolByMediaFunction() {
        PoolRegistry poolRegistry = PoolRegistry.getInstance(true);
        Pool pool = new Pool("test", 2, "test");

        assertFalse(poolRegistry.containsPoolByMediaFunction("test"));

        poolRegistry.addPool(pool);

        assertTrue(poolRegistry.containsPoolByMediaFunction("test"));
    }

    @Test
    public void TestJsonify() {
        PoolRegistry poolRegistry = PoolRegistry.getInstance(true);
        Pool pool = new Pool("test", 2, "test");

        String jsonString = "{\"pools\":[]}";
        assertEquals(jsonString, poolRegistry.jsonify());

        poolRegistry.addPool(pool);

        jsonString =
                "{\"pools\":[{\"mediaFunction\": \"test\",\"timeLeftSeconds\": 2,\"description\": \"test\"}]}";

        assertEquals(jsonString, poolRegistry.jsonify());
    }

    @Test
    public void TestChangeListener() {
        PoolRegistry poolreg = PoolRegistry.getInstance(true);
        Pool pool = new Pool("test", 2, "test");
        poolreg.addPool(pool);

        pool.addSeconds(32);

        assertEquals(34, pool.getTimeLeftSeconds());
        assertEquals(34, PoolRegistry.getInstance(true)
                .getPoolList().next().getTimeLeftSeconds());

        FileHandler.readFromFile(TEST_DIR + "poolreg.ser");

        assertEquals(34, PoolRegistry.getInstance(true)
                .getPoolList().next().getTimeLeftSeconds());

    }

}
