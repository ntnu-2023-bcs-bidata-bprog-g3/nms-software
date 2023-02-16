package no.ntnu.nms.domainModel;


import no.ntnu.nms.logging.Logging;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;

/**
 * A Pool object represents a pool of a media function.
 *
 */
public class Pool implements Serializable {

    private final PropertyChangeSupport changes = new PropertyChangeSupport(this);

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.changes.addPropertyChangeListener(listener);
        Logging.getLogger().info("Pool registry change listener successfully added");
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.changes.removePropertyChangeListener(listener);
        Logging.getLogger().info("Pool registry change listener successfully removed");
    }

    /**
     * The id of the pool with the given media function.
     */
    private Long id;
    /**
     * The media function of the pool.
     */
    private String mediaFunction;
    /**
     * The time left in the pool, in second.
     */
    private int timeLeftSeconds;
    /**
     * The description of the pool.
     */
    private String description;

    /**
     * Constructor for a pool.
     * @param mediaFunction {@link String} media function of the pool.
     * @param timeLeftSeconds {@link Integer} time left in the pool, in seconds.
     * @param description {@link String} description of the media function in the pool.
     */
    public Pool(String mediaFunction, int timeLeftSeconds, String description) {
        Logging.getLogger().info("Creating new pool for mediafunction " + mediaFunction);
        setMediaFunction(mediaFunction);
        setId((long) (Math.random() * 1000000000000000000L));
        setDescription(description);
        try {
            setTimeLeftSeconds(timeLeftSeconds);
        } catch (IllegalArgumentException e) {
            Logging.getLogger().warning("Time left in seconds cannot be negative. Setting 0");
            setTimeLeftSeconds(0);
        }
    }

    /**
     * Set the id of the pool.
     * @param id {@link Long} id of the pool.
     */
    public void setId(Long id) {
        Long oldId = this.id;
        this.id = id;
        changes.firePropertyChange("change", oldId, id);
    }

    /**
     * Set the description of the pool.
     * @param description {@link String} description of the pool.
     */
    public void setDescription(String description) {
        String oldDescription = this.description;
        this.description = description;
        changes.firePropertyChange("change", oldDescription, description);
    }

    /**
     * Set the time left in the pool, in seconds.
     * @param timeLeftSeconds {@link Integer} time left in the pool, in seconds.
     */
    public void setTimeLeftSeconds(int timeLeftSeconds) {
        if (timeLeftSeconds < 0) {
            Logging.getLogger().warning("Time left in seconds cannot be negative");
            throw new IllegalArgumentException("Time left in seconds cannot be negative");
        }
        int oldTimeLeftSeconds = this.timeLeftSeconds;
        this.timeLeftSeconds = timeLeftSeconds;
        changes.firePropertyChange("change", oldTimeLeftSeconds, timeLeftSeconds);
    }

    /**
     * Set the media function of the pool.
     * @param mediaFunction {@link String} media function of the pool.
     */
    public void setMediaFunction(String mediaFunction) {
        String oldMediaFunction = this.mediaFunction;
        this.mediaFunction = mediaFunction;
        changes.firePropertyChange("change", oldMediaFunction, mediaFunction);

    }

    /**
     * Get the media function of the pool.
     * @return {@link String} media function of the pool.
     */
    public String getMediaFunction() {
        return this.mediaFunction;
    }

    /**
     * Get the id of the pool.
     * @return {@link Long} id of the pool.
     */
    public Long getId() {
        return this.id;
    }

    /**
     * Get the time left in the pool, in seconds.
     * @return {@link Integer} time left in the pool, in seconds.
     */
    public int getTimeLeftSeconds() {
        return this.timeLeftSeconds;
    }

    /**
     * Get the description of the pool.
     * @return {@link String} description of the pool.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Subtract time from the time left in the pool.
     * @param seconds {@link Integer} number of seconds to subtract.
     *                               If negative, the time left will be set to 0.
     * @return {@link Boolean} {@code true} if the time was subtracted,
     */
    public boolean subtractSeconds(int seconds) {
        if (seconds > 0 && seconds <= this.timeLeftSeconds) {
            int oldTimeLeftSeconds = this.timeLeftSeconds;
            this.timeLeftSeconds -= seconds;
            changes.firePropertyChange("change", oldTimeLeftSeconds,
                    this.timeLeftSeconds);
            return true;
        }
        return false;
    }


    /**
     * Add time to the time left in the pool
     * @param seconds {@link Integer} number of seconds to add.
     */
    public boolean addSeconds(int seconds) {
        if (seconds > 0) {
            int oldTimeLeftSeconds = this.timeLeftSeconds;
            this.timeLeftSeconds += seconds;
            changes.firePropertyChange("change", oldTimeLeftSeconds,
                    this.timeLeftSeconds);
            return true;
        }
        return false;
    }

    /**
     * Get a string representation of the pool.
     * @return {@link String} string representation of the pool.
     */
    @Override
    public String toString() {
        return String.format(
                "Pool[id=%d, media function=%s, time left=%d, description=%s]",
                this.getId(),
                this.getMediaFunction(),
                this.getTimeLeftSeconds(),
                this.getDescription()
        );
    }

    /**
     * Get a JSON representation of the pool.
     * @return {@link String} JSON representation of the pool.
     */
    public String jsonify() {
        return String.format(
                "{\"id\": %d,\"mediaFunction\": \"%s\",\"timeLeftSeconds\": %d,"
                        + "\"description\": \"%s\"}",
                this.getId(),
                this.getMediaFunction(),
                this.getTimeLeftSeconds(),
                this.getDescription()
        );
    }
}
