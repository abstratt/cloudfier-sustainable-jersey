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
public class Resident extends Person {
	
    public static final String ROLE_ID = "Resident";
    
    
    
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
        public static boolean canRead(Resident subject, Resident target) {
            return false;
        }
        public static boolean canUpdate(Resident subject, Resident target) {
            return subject.getId().equals(target.getId());
        }
        public static boolean canDelete(Resident subject, Resident target) {
            return false;
        }
        public static boolean canRead(CityOfficial subject, Resident target) {
            return true;
        }
        public static boolean canUpdate(CityOfficial subject, Resident target) {
            return true;
        }
        public static boolean canDelete(CityOfficial subject, Resident target) {
            return true;
        }
        public static boolean canRead(SystemAdministrator subject, Resident target) {
            return true;
        }
        public static boolean canUpdate(SystemAdministrator subject, Resident target) {
            return true;
        }
        public static boolean canDelete(SystemAdministrator subject, Resident target) {
            return true;
        }
    }
    
}
