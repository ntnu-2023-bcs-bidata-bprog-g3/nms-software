package no.ntnu.nms.database.repositories;

import no.ntnu.nms.domain_model.Pool;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.CrudRepository;

@EnableJpaRepositories
public interface PoolRepository extends CrudRepository<Pool, String> {

    @Query("from Pool p where p.mediaFunction = :mediaFunction")
    Pool findByMediaFunction(String mediaFunction);

}
