package eu.gaiax.repo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class FileAttachment {
  final String name;
  final byte[] data;
}
