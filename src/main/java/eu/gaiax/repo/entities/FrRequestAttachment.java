package eu.gaiax.repo.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "fr_request_attachment")
public class FrRequestAttachment {
  @Id
  @GeneratedValue
  @Column(name = "id", nullable = false)
  private Long id;
  private String fileName;
  private byte[] fileData;

  @ManyToOne(fetch=FetchType.LAZY, optional = false, cascade=CascadeType.ALL)
  @JoinColumn(name = "fr_request_id", nullable = false)
  private FrRequest frRequest;
}