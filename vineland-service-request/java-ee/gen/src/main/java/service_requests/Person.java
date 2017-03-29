package service_requests;

import java.util.*;
import java.util.stream.*;
import java.util.function.*;
import java.io.Serializable;
import javax.persistence.*;
import javax.inject.*;
import javax.ejb.*;
import javax.enterprise.event.*;
import javax.enterprise.context.*;
import static util.PersistenceHelper.*;
import static util.SecurityHelper.*;
import userprofile.*;
import service_requests.Resident;
import service_requests.CityOfficial;
import service_requests.SystemAdministrator;

@Entity
@Inheritance(strategy=InheritanceType.JOINED)
@Table(schema="service_requests")
public abstract class Person {
	
    
    
    private Long id;
    @Id
    @SequenceGenerator(name="PersonSequence", sequenceName="person_id_seq") 
    @GeneratedValue(strategy=GenerationType.AUTO, generator="PersonSequence")
    public Long getId() {
        return id;
    }
    public void setId(Long newId) {
        this.id = newId;
    }
    
    /*************************** ATTRIBUTES ***************************/

    protected String firstName = "";
    @Column(nullable=false)
    public String getFirstName() {
        return this.firstName;
    }
    public void setFirstName(String newFirstName) {
        this.firstName = newFirstName;
    }
    
    protected String lastName = "";
    @Column(nullable=false)
    public String getLastName() {
        return this.lastName;
    }
    public void setLastName(String newLastName) {
        this.lastName = newLastName;
    }
    /*************************** PERMISSIONS ********************/
    
    public static class Permissions {
        public static boolean canRead(Resident subject, Person target) {
            return true;
        }
        public static boolean canUpdate(Resident subject, Person target) {
            return true;
        }
        public static boolean canDelete(Resident subject, Person target) {
            return true;
        }
        public static boolean canRead(CityOfficial subject, Person target) {
            return true;
        }
        public static boolean canUpdate(CityOfficial subject, Person target) {
            return true;
        }
        public static boolean canDelete(CityOfficial subject, Person target) {
            return true;
        }
        public static boolean canRead(SystemAdministrator subject, Person target) {
            return true;
        }
        public static boolean canUpdate(SystemAdministrator subject, Person target) {
            return true;
        }
        public static boolean canDelete(SystemAdministrator subject, Person target) {
            return true;
        }
    }
    
}
