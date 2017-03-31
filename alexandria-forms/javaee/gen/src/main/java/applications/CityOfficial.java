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
@Inheritance(strategy=InheritanceType.JOINED)
@Table(schema="alexandria_forms")
public class CityOfficial extends Person {
	
    public static final String ROLE_ID = "CityOfficial";
    
    
    
    /*************************** RELATIONSHIPS ***************************/
    
    protected Profile userProfile;
    @OneToOne
    public Profile getUserProfile() {
    	return this.userProfile;
    }
    public void setUserProfile(Profile newUserProfile) {
        this.userProfile = newUserProfile;
    }
    /*************************** PERMISSIONS ********************/
    
    public static class Permissions {
        public static boolean canRead(CityOfficial subject, CityOfficial target) {
            return false;
        }
        public static boolean canUpdate(CityOfficial subject, CityOfficial target) {
            return subject.getId().equals(target.getId());
        }
        public static boolean canDelete(CityOfficial subject, CityOfficial target) {
            return false;
        }
        public static boolean canRead(SystemAdministrator subject, CityOfficial target) {
            return true;
        }
        public static boolean canUpdate(SystemAdministrator subject, CityOfficial target) {
            return true;
        }
        public static boolean canDelete(SystemAdministrator subject, CityOfficial target) {
            return true;
        }
        public static boolean canRead(Resident subject, CityOfficial target) {
            return false;
        }
        public static boolean canUpdate(Resident subject, CityOfficial target) {
            return false;
        }
        public static boolean canDelete(Resident subject, CityOfficial target) {
            return false;
        }
    }
    
}
