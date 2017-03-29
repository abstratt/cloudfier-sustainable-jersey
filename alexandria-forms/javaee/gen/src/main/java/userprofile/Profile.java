package userprofile;

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
import applications.CityOfficial;
import applications.SystemAdministrator;
import applications.Resident;

@Entity
@Table(schema="alexandria_forms")
public class Profile {
	
    
    
    private Long id;
    @Id
    @SequenceGenerator(name="ProfileSequence", sequenceName="profile_id_seq") 
    @GeneratedValue(strategy=GenerationType.AUTO, generator="ProfileSequence")
    public Long getId() {
        return id;
    }
    public void setId(Long newId) {
        this.id = newId;
    }
    
    /*************************** ATTRIBUTES ***************************/

    protected String username;
    @Column(nullable=false, unique=true)
    public String getUsername() {
        return this.username;
    }
    public void setUsername(String newUsername) {
        this.username = newUsername;
    }
    
    protected String password = "";
    @Column(nullable=false)
    public String getPassword() {
        return this.password;
    }
    public void setPassword(String newPassword) {
        this.password = newPassword;
    }
    /*************************** RELATIONSHIPS ***************************/
    
    protected CityOfficial roleAsCityOfficial;
    @OneToOne(mappedBy="userProfile")
    public CityOfficial getRoleAsCityOfficial() {
    	return this.roleAsCityOfficial;
    }
    public void setRoleAsCityOfficial(CityOfficial newRoleAsCityOfficial) {
        this.roleAsCityOfficial = newRoleAsCityOfficial;
    }
    
    protected SystemAdministrator roleAsSystemAdministrator;
    @OneToOne(mappedBy="userProfile")
    public SystemAdministrator getRoleAsSystemAdministrator() {
    	return this.roleAsSystemAdministrator;
    }
    public void setRoleAsSystemAdministrator(SystemAdministrator newRoleAsSystemAdministrator) {
        this.roleAsSystemAdministrator = newRoleAsSystemAdministrator;
    }
    
    protected Resident roleAsResident;
    @OneToOne(mappedBy="userProfile")
    public Resident getRoleAsResident() {
    	return this.roleAsResident;
    }
    public void setRoleAsResident(Resident newRoleAsResident) {
        this.roleAsResident = newRoleAsResident;
    }
    /*************************** PERMISSIONS ********************/
    
    public static class Permissions {
        public static boolean canRead(CityOfficial subject, Profile target) {
            return true;
        }
        public static boolean canUpdate(CityOfficial subject, Profile target) {
            return true;
        }
        public static boolean canDelete(CityOfficial subject, Profile target) {
            return true;
        }
        public static boolean canRead(SystemAdministrator subject, Profile target) {
            return true;
        }
        public static boolean canUpdate(SystemAdministrator subject, Profile target) {
            return true;
        }
        public static boolean canDelete(SystemAdministrator subject, Profile target) {
            return true;
        }
        public static boolean canRead(Resident subject, Profile target) {
            return true;
        }
        public static boolean canUpdate(Resident subject, Profile target) {
            return true;
        }
        public static boolean canDelete(Resident subject, Profile target) {
            return true;
        }
    }
    
}
