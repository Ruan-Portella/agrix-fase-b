package com.betrybe.agrix.solution;

import com.betrybe.agrix.ebytr.staff.entity.Person;
import com.betrybe.agrix.ebytr.staff.exception.PersonNotFoundException;
import com.betrybe.agrix.ebytr.staff.repository.PersonRepository;
import com.betrybe.agrix.ebytr.staff.service.PersonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
public class PersonServiceTest {

	@Mock
	private PersonRepository personRepository;

	@InjectMocks
	private PersonService personService;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	public void getPersonById() {
      Person mockPerson = new Person();
      when(personRepository.findById(0L)).thenReturn(Optional.of(mockPerson));

      Person result = personService.getPersonById(0L);

      assertEquals(mockPerson, result);
	}

	@Test
	public void getPersonByIdNotFound() {
      when(personRepository.findById(0L)).thenReturn(Optional.empty());

      assertThrows(PersonNotFoundException.class, () -> personService.getPersonById(0L));
	}

	@Test
	public void getPersonByUserName() {
      Person mockPerson = new Person();
      when(personRepository.findByUsername("ruan")).thenReturn(Optional.of(mockPerson));

      Person result = personService.getPersonByUsername("ruan");

      assertEquals(mockPerson, result);
	}

	@Test
	public void getPersonByUserNameNotFound() {
      when(personRepository.findByUsername("ruan")).thenReturn(Optional.empty());

      assertThrows(PersonNotFoundException.class, () -> personService.getPersonByUsername("ruan"));
	}

	@Test
	public void createPerson() {
      Person mockPerson = new Person();
      when(personRepository.save(any())).thenReturn(mockPerson);

      Person result = personService.create(new Person());

      assertEquals(mockPerson, result);
	}
}