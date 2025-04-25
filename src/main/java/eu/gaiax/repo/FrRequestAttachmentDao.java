package eu.gaiax.repo;

import eu.gaiax.repo.entities.FrRequestAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface FrRequestAttachmentDao extends JpaRepository<FrRequestAttachment, Long> {
  @NonNull
  Optional<FrRequestAttachment> findByFileNameAndFrRequest_Id(String fileName, Long id);


}
