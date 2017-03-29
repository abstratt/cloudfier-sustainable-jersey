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
import userprofile.*;
import applications.CityOfficial;
import applications.SystemAdministrator;
import applications.Resident;

@Entity
@Inheritance(strategy=InheritanceType.JOINED)
@Table(schema="alexandria_forms")
public class Resident extends Person {
	
    public static final String ROLE_ID = "Resident";
    
    
    
    /*************************** ATTRIBUTES ***************************/

    protected String address;
    @Column
    public String getAddress() {
        return this.address;
    }
    public void setAddress(String newAddress) {
        this.address = newAddress;
    }
    
    protected String city = "Alexandria";
    @Column
    public String getCity() {
        return this.city;
    }
    public void setCity(String newCity) {
        this.city = newCity;
    }
    
    protected String zipCode;
    @Column
    public String getZipCode() {
        return this.zipCode;
    }
    public void setZipCode(String newZipCode) {
        this.zipCode = newZipCode;
    }
    
    protected String homePhone;
    @Column
    public String getHomePhone() {
        return this.homePhone;
    }
    public void setHomePhone(String newHomePhone) {
        this.homePhone = newHomePhone;
    }
    
    protected String workPhone;
    @Column
    public String getWorkPhone() {
        return this.workPhone;
    }
    public void setWorkPhone(String newWorkPhone) {
        this.workPhone = newWorkPhone;
    }
    
    protected String emailAddress;
    @Column
    public String getEmailAddress() {
        return this.emailAddress;
    }
    public void setEmailAddress(String newEmailAddress) {
        this.emailAddress = newEmailAddress;
    }
    /*************************** RELATIONSHIPS ***************************/
    
    protected Profile userProfile;
    @OneToOne
    public Profile getUserProfile() {
    	return this.userProfile;
    }
    public void setUserProfile(Profile newUserProfile) {
        this.userProfile = newUserProfile;
    }
    
    protected Set<DogLicenseApplication> dogLicenseApplications = new LinkedHashSet<>();
    @OneToMany(mappedBy="dogOwner")
    public Set<DogLicenseApplication> getDogLicenseApplications() {
    	return this.dogLicenseApplications;
    }
    public void setDogLicenseApplications(Set<DogLicenseApplication> newDogLicenseApplications) {
        this.dogLicenseApplications = newDogLicenseApplications;
    }
    public void addToDogLicenseApplications(DogLicenseApplication newDogLicenseApplications) {
        this.dogLicenseApplications.add(newDogLicenseApplications);
    }
    public void removeFromDogLicenseApplications(DogLicenseApplication existingDogLicenseApplications) {
        this.dogLicenseApplications.remove(existingDogLicenseApplications);
    }
    /*************************** PERMISSIONS ********************/
    
    public static class Permissions {
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
        public static boolean canRead(Resident subject, Resident target) {
            return false;
        }
        public static boolean canUpdate(Resident subject, Resident target) {
            return subject.getId().equals(target.getId());
        }
        public static boolean canDelete(Resident subject, Resident target) {
            return false;
        }
    }
    
}
