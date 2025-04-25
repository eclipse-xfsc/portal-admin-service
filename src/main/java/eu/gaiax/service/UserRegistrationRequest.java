package eu.gaiax.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;

/**
 * ...
 */
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserRegistrationRequest implements Serializable {
  @JsonProperty(value = "first_name")
  @NonNull String firstname;
  @JsonProperty(value = "last_name")
  @NonNull String lastname;
  @NonNull String email;
  @NonNull String phone;
  @NonNull String address;
  @JsonProperty(value = "zip_code")
  @NonNull String zip;
  @NonNull String city;
  @NonNull String country;
}
