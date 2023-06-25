package gtorr;

import gtorr.Persons;
import jakarta.persistence.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

@Service
public class PersonsDoa {

    private PersonsRepository personsRepository;

    @Autowired
    public PersonsDoa(PersonsRepository personRepository) {
        this.personsRepository = personRepository;
    }


    @Autowired
    public void save(){
        Persons persons1 = new Persons(1,"a","b","c","d");
        personsRepository.save(persons1);
    }
}
