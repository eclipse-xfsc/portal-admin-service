package eu.gaiax.repo;

import eu.gaiax.repo.entities.FrRequestTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FrRequestTypeDao extends JpaRepository<FrRequestTypeEntity, Long> {
    Optional<FrRequestTypeEntity> findByName(String name);
}
