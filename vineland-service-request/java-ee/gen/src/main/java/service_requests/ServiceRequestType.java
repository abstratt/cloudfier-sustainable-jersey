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
@Table(schema="service_requests")
public class ServiceRequestType {
	
    
    
    private Long id;
    @Id
    @SequenceGenerator(name="ServiceRequestTypeSequence", sequenceName="servicerequesttype_id_seq") 
    @GeneratedValue(strategy=GenerationType.AUTO, generator="ServiceRequestTypeSequence")
    public Long getId() {
        return id;
    }
    public void setId(Long newId) {
        this.id = newId;
    }
    
    /*************************** ATTRIBUTES ***************************/

    protected String name = "";
    @Column(nullable=false)
    public String getName() {
        return this.name;
    }
    public void setName(String newName) {
        this.name = newName;
    }
    /*************************** PERMISSIONS ********************/
    
    public static class Permissions {
        public static boolean canRead(Resident subject, ServiceRequestType target) {
            return true;
        }
        public static boolean canUpdate(Resident subject, ServiceRequestType target) {
            return false;
        }
        public static boolean canDelete(Resident subject, ServiceRequestType target) {
            return false;
        }
        public static boolean canRead(CityOfficial subject, ServiceRequestType target) {
            return true;
        }
        public static boolean canUpdate(CityOfficial subject, ServiceRequestType target) {
            return true;
        }
        public static boolean canDelete(CityOfficial subject, ServiceRequestType target) {
            return true;
        }
        public static boolean canRead(SystemAdministrator subject, ServiceRequestType target) {
            return true;
        }
        public static boolean canUpdate(SystemAdministrator subject, ServiceRequestType target) {
            return true;
        }
        public static boolean canDelete(SystemAdministrator subject, ServiceRequestType target) {
            return true;
        }
    }
    
}
