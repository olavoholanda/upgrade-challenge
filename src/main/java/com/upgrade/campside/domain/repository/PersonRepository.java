package com.upgrade.campside.domain.repository;

import com.upgrade.campside.domain.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * The <code>PersonRepository</code> interface extends Spring <code>JpaRepository</code providing
 * useful additional account related queries on the database. This is interface should be used in
 * the service layer for create, retrieve, update and delete operations on persons.
 *
 * @author Olavo Holanda
 * @version 0.1
 * @since 0.1
 */
public interface PersonRepository extends JpaRepository<Person, Long> {

}
