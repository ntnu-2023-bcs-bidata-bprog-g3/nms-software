package no.ntnu.nms.database.entities;

import javax.persistence.*;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
public class Pool extends AbstractPersistable<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String mediaFunction;

    public Pool(String mediaFunction) {
        setMediaFunction(mediaFunction);
    }

    protected Pool() {}

    public String getMediaFunction() {
        return this.mediaFunction;
    }

    public void setMediaFunction(String mediaFunction) {
        this.mediaFunction = mediaFunction;
    }

    @Override
    public String toString() {
        return String.format(
                "Pool[id=%d, media function=%s]",
                this.id, this.getMediaFunction()
        );
    }

}
