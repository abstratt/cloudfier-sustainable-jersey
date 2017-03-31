package applications;

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
import alexandria_forms.*;
import userprofile.*;
import applications.CityOfficial;
import applications.SystemAdministrator;
import applications.Resident;

@Entity
@Table(schema="alexandria_forms")
public class Breed {
	
    
    
    private Long id;
    @Id
    @SequenceGenerator(name="BreedSequence", sequenceName="breed_id_seq") 
    @GeneratedValue(strategy=GenerationType.AUTO, generator="BreedSequence")
    public Long getId() {
        return id;
    }
    public void setId(Long newId) {
        this.id = newId;
    }
    
    /*************************** ATTRIBUTES ***************************/

    protected String breed = "";
    @Column(nullable=false)
    public String getBreed() {
        return this.breed;
    }
    public void setBreed(String newBreed) {
        this.breed = newBreed;
    }
    /*************************** PERMISSIONS ********************/
    
    public static class Permissions {
        public static boolean canRead(CityOfficial subject, Breed target) {
            return true;
        }
        public static boolean canUpdate(CityOfficial subject, Breed target) {
            return true;
        }
        public static boolean canDelete(CityOfficial subject, Breed target) {
            return true;
        }
        public static boolean canRead(SystemAdministrator subject, Breed target) {
            return true;
        }
        public static boolean canUpdate(SystemAdministrator subject, Breed target) {
            return true;
        }
        public static boolean canDelete(SystemAdministrator subject, Breed target) {
            return true;
        }
        public static boolean canRead(Resident subject, Breed target) {
            return false;
        }
        public static boolean canUpdate(Resident subject, Breed target) {
            return false;
        }
        public static boolean canDelete(Resident subject, Breed target) {
            return false;
        }
    }
    
}
