package no.ntnu.nms.domain_model;


/**
 * A Pool object represents a pool of a media function.
 *
 */
public class Pool implements java.io.Serializable {

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
     */
    public Pool(String mediaFunction, int timeLeftSeconds, String description) {
        setMediaFunction(mediaFunction);
        setId((long) (Math.random() * 1000000000000000000L));
        setDescription(description);
        try {
            setTimeLeftSeconds(timeLeftSeconds);
        } catch (IllegalArgumentException e) {
            // TODO: Add logging
            setTimeLeftSeconds(0);
        }
    }

    /**
     * Set the id of the pool.
     * @param id {@link Long} id of the pool.
     */
    public void setId(Long id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTimeLeftSeconds(int timeLeftSeconds) {
        if (timeLeftSeconds < 0)
            throw new IllegalArgumentException("Time left in seconds cannot be negative");
        this.timeLeftSeconds = timeLeftSeconds;
    }

    /**
     * Set the media function of the pool.
     * @param mediaFunction {@link String} media function of the pool.
     */
    public void setMediaFunction(String mediaFunction) {
        this.mediaFunction = mediaFunction;
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
     */
    public boolean subtractSeconds(int seconds) {
        if (seconds > 0 && seconds <= this.timeLeftSeconds) {
            this.timeLeftSeconds -= seconds;
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
                "Pool[id=%d, media function=%s]",
                this.getId(), this.getMediaFunction()
        );
    }

    /**
     * Get a JSON representation of the pool.
     * @return {@link String} JSON representation of the pool.
     */
    public String jsonify() {
        return String.format(
                "{\"id\": %d,\"mediaFunction\": \"%s\"}",
                this.getId(), this.getMediaFunction()
        );
    }
}
