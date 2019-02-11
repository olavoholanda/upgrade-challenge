package com.upgrade.campside.controller.dto;

import com.upgrade.campside.domain.model.Person;
import javax.validation.constraints.NotNull;
import lombok.Getter;

/**
 * Data Transfer Object for the Person Entity
 *
 * @author Olavo Holanda
 * @version 0.1
 * @since 0.1
 */
@Getter
public class PersonDTO implements DTOMapper<Person> {

  private Long id;
  @NotNull
  private String email;
  @NotNull
  private String fullName;

  @Override
  public String toString() {
    return String.format("PersonDTO[id=%d, email='%s', fullName='%s']", id, email, fullName);
  }

  /**
   * Fills the DTO with information from the <code>Person</code> entity.
   *
   * @param person the <code>Person</code> entity
   */
  @Override
  public void buildFromEntity(Person person) {
    this.id = person.getId();
    this.email = person.getEmail();
    this.fullName = person.getFullName();
  }
}
