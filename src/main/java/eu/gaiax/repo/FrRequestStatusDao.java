package eu.gaiax.repo;

import eu.gaiax.repo.entities.FrRequestStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FrRequestStatusDao extends JpaRepository<FrRequestStatusEntity, Long> {
    Optional<FrRequestStatusEntity> findByName(String name);
}
