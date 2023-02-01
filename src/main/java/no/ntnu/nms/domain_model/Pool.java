package no.ntnu.nms.domain_model;


public class Pool implements java.io.Serializable {

    private Long id;
    private String mediaFunction;

    public Pool(String mediaFunction) {
        setMediaFunction(mediaFunction);
        setId((long) (Math.random() * 1000000000000000000L));
    }

    public String getMediaFunction() {
        return this.mediaFunction;
    }

    public Long getId() {
        return this.id;
    }

    public void setMediaFunction(String mediaFunction) {
        this.mediaFunction = mediaFunction;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return String.format(
                "Pool[id=%d, media function=%s]",
                this.getId(), this.getMediaFunction()
        );
    }

}
