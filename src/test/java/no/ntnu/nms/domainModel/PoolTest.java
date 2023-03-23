package no.ntnu.nms.domainModel;

import no.ntnu.nms.logging.Logging;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.beans.PropertyChangeListener;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class PoolTest {

    public static Pool POOL;

    @BeforeAll
    public static void setUpAll() {
        try {
            Logging.setUpLogger("ALL");
        } catch (IOException ignore) {}
    }

    @BeforeEach
    public void setUpEach() {
        POOL = new Pool("J2KHD", 30, "A test pool");
    }


    @Test
    public void TestCreation() {
        assertEquals("J2KHD", POOL.getMediaFunction());
        assertEquals(30, POOL.getTimeLeftSeconds());
        assertEquals("A test pool", POOL.getDescription());
    }

    @Test
    public void TestCreationIllegalArgument() {
        Pool pool = new Pool("J2KHD", -30, "A test pool");
        assertEquals(0, pool.getTimeLeftSeconds());
    }

    @Test
    public void TestSetDescription() {
        String oldDescription = POOL.getDescription();
        POOL.setDescription("A new description");
        assertNotEquals(oldDescription, POOL.getDescription());
        assertEquals("A new description", POOL.getDescription());
    }

    @Test
    public void TestSetTimeLeftSeconds() {
        int oldTimeLeftSeconds = POOL.getTimeLeftSeconds();
        POOL.setTimeLeftSeconds(20);
        assertNotEquals(oldTimeLeftSeconds, POOL.getTimeLeftSeconds());
        assertEquals(20, POOL.getTimeLeftSeconds());
    }

    @Test
    public void TestSetTimeLeftSecondsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> POOL.setTimeLeftSeconds(-1));
    }

    @Test
    public void TestSetMediaFunction() {
        String oldMediaFunction = POOL.getMediaFunction();
        POOL.setMediaFunction("J2KHD2");
        assertNotEquals(oldMediaFunction, POOL.getMediaFunction());
        assertEquals("J2KHD2", POOL.getMediaFunction());
    }

    @Test
    public void TestJsonify() {
        String json = "{" +
                "\"mediaFunction\": \"J2KHD\"," +
                "\"timeLeftSeconds\": 30," +
                "\"description\": \"A test pool\"" +
                "}";
        assertEquals(json, POOL.jsonify());
    }

    @Test
    public void TestSetEventListener() {
        PropertyChangeListener pcl = evt -> {};
        assertEquals(0, POOL.getChanges().getPropertyChangeListeners().length);
        POOL.addPropertyChangeListener(pcl);
        POOL.setDescription("A new description");
        assertTrue(POOL.getChanges().getPropertyChangeListeners().length > 0);
    }

    @Test
    public void TestRemoveEventListener() {
        PropertyChangeListener pcl = evt -> {};
        assertEquals(0, POOL.getChanges().getPropertyChangeListeners().length);
        POOL.addPropertyChangeListener(pcl);
        POOL.setDescription("A new description");
        assertTrue(POOL.getChanges().getPropertyChangeListeners().length > 0);

        POOL.removePropertyChangeListener(pcl);
        assertEquals(0, POOL.getChanges().getPropertyChangeListeners().length);
    }

    @Test
    public void TestAddSeconds() {
        int oldSeconds = POOL.getTimeLeftSeconds();
        assertTrue(POOL.addSeconds(10));
        assertEquals(oldSeconds + 10, POOL.getTimeLeftSeconds());
    }

    @Test
    public void TestAddSecondsNegative() {
        int oldSeconds = POOL.getTimeLeftSeconds();
        assertFalse(POOL.addSeconds(-10));
        assertEquals(oldSeconds, POOL.getTimeLeftSeconds());
    }

    @Test
    public void TestSubtractSecondsLarge() {
        int oldSeconds = POOL.getTimeLeftSeconds();
        assertFalse(POOL.subtractSeconds(9999));
        assertEquals(oldSeconds, POOL.getTimeLeftSeconds());
    }

    @Test
    public void TestSubtractSeconds() {
        int oldSeconds = POOL.getTimeLeftSeconds();
        assertTrue(POOL.subtractSeconds(10));
        assertEquals(oldSeconds - 10, POOL.getTimeLeftSeconds());
    }

    @Test
    public void TestSubtractSecondsNegative() {
        int oldSeconds = POOL.getTimeLeftSeconds();
        assertFalse(POOL.subtractSeconds(-10));
        assertEquals(oldSeconds, POOL.getTimeLeftSeconds());
    }

    @Test
    public void TeatToString() {
        assertEquals("Pool[media function=J2KHD, time left=30, description=A test pool]",
                POOL.toString());
    }
}
