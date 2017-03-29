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
public class ServiceRequest {
	
    
    
    private Long id;
    @Id
    @SequenceGenerator(name="ServiceRequestSequence", sequenceName="servicerequest_id_seq") 
    @GeneratedValue(strategy=GenerationType.AUTO, generator="ServiceRequestSequence")
    public Long getId() {
        return id;
    }
    public void setId(Long newId) {
        this.id = newId;
    }
    
    /*************************** ATTRIBUTES ***************************/

    protected Date reportDate = java.sql.Date.valueOf(java.time.LocalDate.now());
    @Column(nullable=false)
    public Date getReportDate() {
        return this.reportDate;
    }
    public void setReportDate(Date newReportDate) {
        this.reportDate = newReportDate;
    }
    
    protected String description = "";
    @Column(length=16384, nullable=false)
    public String getDescription() {
        return this.description;
    }
    public void setDescription(String newDescription) {
        this.description = newDescription;
    }
    
    protected ServiceRequest.Status status = ServiceRequest.Status.Submitted;
    @Column(nullable=false)
    @Enumerated(EnumType.STRING)
    public ServiceRequest.Status getStatus() {
        return this.status;
    }
    public void setStatus(ServiceRequest.Status newStatus) {
        this.status = newStatus;
    }
    
    protected String picture;
    @Column
    public String getPicture() {
        return this.picture;
    }
    public void setPicture(String newPicture) {
        this.picture = newPicture;
    }
    
    protected String location;
    @Column
    public String getLocation() {
        return this.location;
    }
    public void setLocation(String newLocation) {
        this.location = newLocation;
    }
    
    protected Date acceptanceDate;
    @Column
    public Date getAcceptanceDate() {
        return this.acceptanceDate;
    }
    public void setAcceptanceDate(Date newAcceptanceDate) {
        this.acceptanceDate = newAcceptanceDate;
    }
    
    protected Date completionDate;
    @Column
    public Date getCompletionDate() {
        return this.completionDate;
    }
    public void setCompletionDate(Date newCompletionDate) {
        this.completionDate = newCompletionDate;
    }
    
    protected String staffComment = "";
    @Column
    public String getStaffComment() {
        return this.staffComment;
    }
    public void setStaffComment(String newStaffComment) {
        this.staffComment = newStaffComment;
    }
    /*************************** RELATIONSHIPS ***************************/
    
    protected ServiceRequestType serviceRequestType;
    @OneToOne
    public ServiceRequestType getServiceRequestType() {
    	return this.serviceRequestType;
    }
    public void setServiceRequestType(ServiceRequestType newServiceRequestType) {
        this.serviceRequestType = newServiceRequestType;
    }
    
    protected Resident resident;
    @OneToOne
    public Resident getResident() {
    	return this.resident;
    }
    public void setResident(Resident newResident) {
        this.resident = newResident;
    }
    
    protected CityOfficial staff;
    @OneToOne
    public CityOfficial getStaff() {
    	return this.staff;
    }
    public void setStaff(CityOfficial newStaff) {
        this.staff = newStaff;
    }
    /*************************** ACTIONS ***************************/
    
    public void accept() {
        this.setStaff(asCityOfficial(getCurrentProfile()));
        this.handleEvent(StatusEvent.Accept);
    }
    public void reject(String reason) {
        this.setStaffComment(reason);
        this.setStaff(asCityOfficial(getCurrentProfile()));
        this.handleEvent(StatusEvent.Reject);
    }
    public void complete() {
        this.handleEvent(StatusEvent.Complete);
    }
    /**
     * Is the Accept action enabled at this time?
     */
     @Transient
     public boolean isAcceptActionEnabled() {
     	if (getStatus() != ServiceRequest.Status.Submitted) {
     		return false;
     	}
         return true;
     }
    
    /**
     * Is the Reject action enabled at this time?
     */
     @Transient
     public boolean isRejectActionEnabled() {
     	if (getStatus() != ServiceRequest.Status.Submitted) {
     		return false;
     	}
         return true;
     }
    
    /**
     * Is the Complete action enabled at this time?
     */
     @Transient
     public boolean isCompleteActionEnabled() {
     	if (getStatus() != ServiceRequest.Status.Accepted) {
     		return false;
     	}
         return true;
     }
    /*************************** STATE MACHINE ********************/
    
    public enum Status {
        Submitted {
            @Override void handleEvent(ServiceRequest instance, StatusEvent event) {
                switch (event) {
                    case Accept :
                        doTransitionTo(instance, Accepted);
                        break;
                    
                    case Reject :
                        doTransitionTo(instance, Invalid);
                        break;
                    default : break; /* unexpected events are silently ignored */ 
                }
            }                       
        },
        Invalid {
            @Override void handleEvent(ServiceRequest instance, StatusEvent event) {
                /* this is a final state */
            }                       
        },
        Accepted {
            @Override void onEntry(ServiceRequest instance) {
                instance.setAcceptanceDate(java.sql.Date.valueOf(java.time.LocalDate.now()));
            }
            @Override void handleEvent(ServiceRequest instance, StatusEvent event) {
                switch (event) {
                    case Complete :
                        doTransitionTo(instance, Completed);
                        break;
                    default : break; /* unexpected events are silently ignored */ 
                }
            }                       
        },
        Completed {
            @Override void onEntry(ServiceRequest instance) {
                instance.setCompletionDate(java.sql.Date.valueOf(java.time.LocalDate.now()));
            }
            @Override void handleEvent(ServiceRequest instance, StatusEvent event) {
                /* this is a final state */
            }                       
        };
        void onEntry(ServiceRequest instance) {
            /* no entry behavior by default */
        }
        void onExit(ServiceRequest instance) {
            /* no exit behavior by default */
        }
        /** Each state implements handling of events. */
        abstract void handleEvent(ServiceRequest instance, StatusEvent event);
        /** 
         *  Performs a transition.
         *  @param instance the instance to update
         *  @param newState the new state to transition to 
         */
        final void doTransitionTo(ServiceRequest instance, Status newState) {
            instance.status.onExit(instance);
            instance.status = newState;
            instance.status.onEntry(instance);
        }
    }
    
    public enum StatusEvent {
        Accept,
        Reject,
        Complete
    }
    
    public void handleEvent(StatusEvent event) {
        status.handleEvent(this, event);
    }
    /*************************** PERMISSIONS ********************/
    
    public static class Permissions {
        public static boolean canRead(Resident subject, ServiceRequest target) {
            return true;
        }
        public static boolean canUpdate(Resident subject, ServiceRequest target) {
            return target.getResident().getId().equals(subject.getId()) && (target.getStatus() == ServiceRequest.Status.Submitted);
        }
        public static boolean canDelete(Resident subject, ServiceRequest target) {
            return false;
        }
        public static boolean canRead(CityOfficial subject, ServiceRequest target) {
            return true;
        }
        public static boolean canUpdate(CityOfficial subject, ServiceRequest target) {
            return true;
        }
        public static boolean canDelete(CityOfficial subject, ServiceRequest target) {
            return true;
        }
        /**
         * Is the 'accept' action allowed for the given CityOfficial?
         */
        public static boolean isAcceptAllowedFor(CityOfficial subject, ServiceRequest target) {
            return true;
        }
        /**
         * Is the 'reject' action allowed for the given CityOfficial?
         */
        public static boolean isRejectAllowedFor(CityOfficial subject, ServiceRequest target) {
            return true;
        }
        /**
         * Is the 'complete' action allowed for the given CityOfficial?
         */
        public static boolean isCompleteAllowedFor(CityOfficial subject, ServiceRequest target) {
            return true;
        }
        public static boolean canRead(SystemAdministrator subject, ServiceRequest target) {
            return true;
        }
        public static boolean canUpdate(SystemAdministrator subject, ServiceRequest target) {
            return true;
        }
        public static boolean canDelete(SystemAdministrator subject, ServiceRequest target) {
            return true;
        }
        /**
         * Is the 'accept' action allowed for the given SystemAdministrator?
         */
        public static boolean isAcceptAllowedFor(SystemAdministrator subject, ServiceRequest target) {
            return true;
        }
        /**
         * Is the 'reject' action allowed for the given SystemAdministrator?
         */
        public static boolean isRejectAllowedFor(SystemAdministrator subject, ServiceRequest target) {
            return true;
        }
        /**
         * Is the 'complete' action allowed for the given SystemAdministrator?
         */
        public static boolean isCompleteAllowedFor(SystemAdministrator subject, ServiceRequest target) {
            return true;
        }
    }
    
}
