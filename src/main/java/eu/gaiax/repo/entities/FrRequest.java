package eu.gaiax.repo.entities;

import eu.gaiax.repo.JSONDetails;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.sql.Date;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "fr_request")
public class FrRequest {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", nullable = false)
  private Long id;
  private Date requestDate;

  @Column(nullable = false)
  private String participantName;

  @Column(nullable = false)
  private String email;

  @Column(nullable = false)
  private String location;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  private FrRequestTypeEntity requestType;

  @ManyToOne(fetch = FetchType.LAZY)
  private FrRequestStatusEntity requestStatus;

  @OneToMany(mappedBy = "frRequest", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @ToString.Exclude
  private List<FrRequestAttachment> frRequestAttachments;

  @Type(type = "com.vladmihalcea.hibernate.type.json.JsonType")
  @Column(columnDefinition = "json")
  private JSONDetails details;
}
