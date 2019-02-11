package com.upgrade.campside.domain.model;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

/**
 * Person is the entity class for representing simple information about a person that makes a
 * reservation on the Campside. As this service is not responsible for managing persons, therefore,
 * the info hold by this class is pretty basic. A Person holds information about its email and full
 * name.
 *
 * @author Olavo Holanda
 * @version 0.1
 * @since 0.1
 */
@Getter
@Setter
@Entity
public class Person {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(nullable = false)
  private String email;

  @Column(nullable = false)
  private String fullName;

  @OneToMany(mappedBy = "owner")
  private List<Reservation> reservations = new ArrayList<>();

  /**
   * Protected constructor, please use the one with parameters, that are required for this object.
   */
  protected Person() {
  }

  /**
   * <code>Person</code> constructor, creates a new person instance with
   * the mandatory parameters.
   *
   * @param email the <code>String</code> representing the person email.
   * @param fullName the <code>String</code> with the user's full name.
   */
  public Person(String email, String fullName) {
    this.email = email;
    this.fullName = fullName;
  }

  @Override
  public String toString() {
    return String.format(
        "Person[id=%d, email='%s', fullName='%s']",
        id, email, fullName);
  }
}
