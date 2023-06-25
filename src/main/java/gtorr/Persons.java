package gtorr;

import jakarta.persistence.*;

@Entity
@Table(name = "Persons")
public class Persons {
    @Id
    Integer PersonID ;
    @Column
    String LastName;
 public Persons(){}
    public Persons(int personID, String lastName, String firstName, String address, String city) {
        PersonID = personID;
        LastName = lastName;
        FirstName = firstName;
        Address = address;
        City = city;
    }

    public int getPersonID() {
        return PersonID;
    }

    public void setPersonID(int personID) {
        PersonID = personID;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    @Column
    String FirstName;
    @Column
    String Address;
    @Column
    String City;
}
