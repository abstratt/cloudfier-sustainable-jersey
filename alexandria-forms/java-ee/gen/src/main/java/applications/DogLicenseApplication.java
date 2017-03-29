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
@Table(schema="alexandria_forms")
public class DogLicenseApplication {
	
    
    
    private Long id;
    @Id
    @SequenceGenerator(name="DogLicenseApplicationSequence", sequenceName="doglicenseapplication_id_seq") 
    @GeneratedValue(strategy=GenerationType.AUTO, generator="DogLicenseApplicationSequence")
    public Long getId() {
        return id;
    }
    public void setId(Long newId) {
        this.id = newId;
    }
    
    /*************************** ATTRIBUTES ***************************/

    protected DogLicenseApplication.Status status = DogLicenseApplication.Status.Draft;
    @Column(nullable=false)
    @Enumerated(EnumType.STRING)
    public DogLicenseApplication.Status getStatus() {
        return this.status;
    }
    public void setStatus(DogLicenseApplication.Status newStatus) {
        this.status = newStatus;
    }
    
    protected String petName = "";
    @Column(nullable=false)
    public String getPetName() {
        return this.petName;
    }
    public void setPetName(String newPetName) {
        this.petName = newPetName;
    }
    
    protected Date datePaid;
    @Column
    public Date getDatePaid() {
        return this.datePaid;
    }
    public void setDatePaid(Date newDatePaid) {
        this.datePaid = newDatePaid;
    }
    
    protected HairType hair = HairType.Short;
    @Column(nullable=false)
    @Enumerated(EnumType.STRING)
    public HairType getHair() {
        return this.hair;
    }
    public void setHair(HairType newHair) {
        this.hair = newHair;
    }
    
    protected String coloring = "";
    @Column(nullable=false)
    public String getColoring() {
        return this.coloring;
    }
    public void setColoring(String newColoring) {
        this.coloring = newColoring;
    }
    
    protected Gender sex = Gender.Male;
    @Column(nullable=false)
    @Enumerated(EnumType.STRING)
    public Gender getSex() {
        return this.sex;
    }
    public void setSex(Gender newSex) {
        this.sex = newSex;
    }
    
    protected boolean neutered = false;
    @Column(nullable=false)
    public boolean isNeutered() {
        return this.neutered;
    }
    public void setNeutered(boolean newNeutered) {
        this.neutered = newNeutered;
    }
    
    protected Date birthDate = new Date();
    @Column(nullable=false)
    public Date getBirthDate() {
        return this.birthDate;
    }
    public void setBirthDate(Date newBirthDate) {
        this.birthDate = newBirthDate;
    }
    
    protected String rabiesCertificate;
    @Column
    public String getRabiesCertificate() {
        return this.rabiesCertificate;
    }
    public void setRabiesCertificate(String newRabiesCertificate) {
        this.rabiesCertificate = newRabiesCertificate;
    }
    
    protected double paymentReceived = 0.0;
    @Column(nullable=false)
    public double getPaymentReceived() {
        return this.paymentReceived;
    }
    public void setPaymentReceived(double newPaymentReceived) {
        this.paymentReceived = newPaymentReceived;
    }
    /*************************** SEQUENCE ***************************/

    @Transient
    public long getInternalNumber() {
    	return id;
    }
    /*************************** RELATIONSHIPS ***************************/
    
    protected Resident dogOwner;
    @ManyToOne
    public Resident getDogOwner() {
    	return this.dogOwner;
    }
    public void setDogOwner(Resident newDogOwner) {
        this.dogOwner = newDogOwner;
    }
    
    protected Breed breed;
    @OneToOne
    public Breed getBreed() {
    	return this.breed;
    }
    public void setBreed(Breed newBreed) {
        this.breed = newBreed;
    }
    /*************************** ACTIONS ***************************/
    
    public void submit() {
        if ( (this.getRabiesCertificate() == null)) {
            // predicate violated
            throw new RabiesCertificateRecordMustBeAttachedException();
        }
        this.handleEvent(StatusEvent.Submit);
    }
    public void receivePayment() {
        this.setPaymentReceived(this.getRequiredFee());
        this.setDatePaid(java.sql.Date.valueOf(java.time.LocalDate.now()));
        this.handleEvent(StatusEvent.ReceivePayment);
    }
    public void reject() {
        this.handleEvent(StatusEvent.Reject);
    }
    public void approve() {
        this.handleEvent(StatusEvent.Approve);
    }
    public void cancel() {
        this.handleEvent(StatusEvent.Cancel);
    }
    /**
     * Is the Submit action enabled at this time?
     */
     @Transient
     public boolean isSubmitActionEnabled() {
     	if (getStatus() != DogLicenseApplication.Status.Draft) {
     		return false;
     	}
         if ( (this.getRabiesCertificate() == null)) {
             return false;
         }
         return true;
     }
    
    /**
     * Is the ReceivePayment action enabled at this time?
     */
     @Transient
     public boolean isReceivePaymentActionEnabled() {
     	if (getStatus() != DogLicenseApplication.Status.Submitted) {
     		return false;
     	}
         return true;
     }
    
    /**
     * Is the Reject action enabled at this time?
     */
     @Transient
     public boolean isRejectActionEnabled() {
     	if (getStatus() != DogLicenseApplication.Status.Paid) {
     		return false;
     	}
         return true;
     }
    
    /**
     * Is the Approve action enabled at this time?
     */
     @Transient
     public boolean isApproveActionEnabled() {
     	if (getStatus() != DogLicenseApplication.Status.Paid) {
     		return false;
     	}
         return true;
     }
    
    /**
     * Is the Cancel action enabled at this time?
     */
     @Transient
     public boolean isCancelActionEnabled() {
     	if (!EnumSet.of(DogLicenseApplication.Status.Draft, DogLicenseApplication.Status.Submitted).contains(getStatus())) {
     		return false;
     	}
         return true;
     }
    /*************************** DERIVED PROPERTIES ****************/
    
    @Transient
    public String getNumber() {
        return "DGL" + this.getInternalNumber();
    }
    
    @Transient
    public Date getReferenceDate() {
        if ((this.getDatePaid() == null)) {
            return java.sql.Date.valueOf(java.time.LocalDate.now());
        } else  {
            return this.getDatePaid();
        }
    }
    
    @Transient
    public long getAgeInYears() {
        return Unsupported: Duration operation: toYears;
    }
    
    @Transient
    public long getPartialAgeInMonths() {
        return Unsupported: Number operation modulo;
    }
    
    @Transient
    public String getAge() {
        long partialAgeInMonths = this.getPartialAgeInMonths();
        long ageInYears = this.getAgeInYears();
        if (ageInYears == 0L) {
            return Objects.toString(partialAgeInMonths) + " months";
        } else  {
            if (partialAgeInMonths == 0L) {
                return "" + ageInYears + " years";
            } else  {
                return "" + ageInYears + " years and " + partialAgeInMonths + " months ";
            }
        }
    }
    
    @Transient
    public boolean isLateFeeApplies() {
        return (this.getReferenceDate() != null && this.getReferenceDate().compareTo(new Date((int) this.getReferenceDate().getYear() + 1900L - 1900, (int) 4L - 1, (int) 1L)) > 0);
    }
    
    @Transient
    public double getLateFee() {
        if (! this.isLateFeeApplies()) {
            return 0.0;
        } else  {
            java.lang.NullPointerException;
        }
    }
    
    @Transient
    public double getBaseFee() {
        java.lang.NullPointerException;
    }
    
    @Transient
    public double getRequiredFee() {
        return this.getBaseFee() + this.getLateFee();
    }
    /*************************** STATE MACHINE ********************/
    
    public enum Status {
        Draft {
            @Override void handleEvent(DogLicenseApplication instance, StatusEvent event) {
                switch (event) {
                    case Submit :
                        doTransitionTo(instance, Submitted);
                        break;
                    
                    case Cancel :
                        doTransitionTo(instance, Canceled);
                        break;
                    default : break; /* unexpected events are silently ignored */ 
                }
            }                       
        },
        Submitted {
            @Override void handleEvent(DogLicenseApplication instance, StatusEvent event) {
                switch (event) {
                    case ReceivePayment :
                        doTransitionTo(instance, Paid);
                        break;
                    
                    case Cancel :
                        doTransitionTo(instance, Canceled);
                        break;
                    default : break; /* unexpected events are silently ignored */ 
                }
            }                       
        },
        Paid {
            @Override void handleEvent(DogLicenseApplication instance, StatusEvent event) {
                switch (event) {
                    case Reject :
                        doTransitionTo(instance, Rejected);
                        break;
                    
                    case Approve :
                        doTransitionTo(instance, Approved);
                        break;
                    default : break; /* unexpected events are silently ignored */ 
                }
            }                       
        },
        Approved {
            @Override void handleEvent(DogLicenseApplication instance, StatusEvent event) {
                /* this is a final state */
            }                       
        },
        Rejected {
            @Override void handleEvent(DogLicenseApplication instance, StatusEvent event) {
                /* this is a final state */
            }                       
        },
        Canceled {
            @Override void handleEvent(DogLicenseApplication instance, StatusEvent event) {
                /* this is a final state */
            }                       
        };
        void onEntry(DogLicenseApplication instance) {
            /* no entry behavior by default */
        }
        void onExit(DogLicenseApplication instance) {
            /* no exit behavior by default */
        }
        /** Each state implements handling of events. */
        abstract void handleEvent(DogLicenseApplication instance, StatusEvent event);
        /** 
         *  Performs a transition.
         *  @param instance the instance to update
         *  @param newState the new state to transition to 
         */
        final void doTransitionTo(DogLicenseApplication instance, Status newState) {
            instance.status.onExit(instance);
            instance.status = newState;
            instance.status.onEntry(instance);
        }
    }
    
    public enum StatusEvent {
        Submit,
        Cancel,
        ReceivePayment,
        Reject,
        Approve
    }
    
    public void handleEvent(StatusEvent event) {
        status.handleEvent(this, event);
    }
    /*************************** PERMISSIONS ********************/
    
    public static class Permissions {
        public static boolean canRead(CityOfficial subject, DogLicenseApplication target) {
            return true;
        }
        public static boolean canUpdate(CityOfficial subject, DogLicenseApplication target) {
            return true;
        }
        public static boolean canDelete(CityOfficial subject, DogLicenseApplication target) {
            return false;
        }
        /**
         * Is the 'submit' action allowed for the given CityOfficial?
         */
        public static boolean isSubmitAllowedFor(CityOfficial subject, DogLicenseApplication target) {
            return true;
        }
        /**
         * Is the 'receivePayment' action allowed for the given CityOfficial?
         */
        public static boolean isReceivePaymentAllowedFor(CityOfficial subject, DogLicenseApplication target) {
            return true;
        }
        /**
         * Is the 'reject' action allowed for the given CityOfficial?
         */
        public static boolean isRejectAllowedFor(CityOfficial subject, DogLicenseApplication target) {
            return true;
        }
        /**
         * Is the 'approve' action allowed for the given CityOfficial?
         */
        public static boolean isApproveAllowedFor(CityOfficial subject, DogLicenseApplication target) {
            return true;
        }
        /**
         * Is the 'cancel' action allowed for the given CityOfficial?
         */
        public static boolean isCancelAllowedFor(CityOfficial subject, DogLicenseApplication target) {
            return true;
        }
        public static boolean canRead(SystemAdministrator subject, DogLicenseApplication target) {
            return true;
        }
        public static boolean canUpdate(SystemAdministrator subject, DogLicenseApplication target) {
            return true;
        }
        public static boolean canDelete(SystemAdministrator subject, DogLicenseApplication target) {
            return true;
        }
        /**
         * Is the 'submit' action allowed for the given SystemAdministrator?
         */
        public static boolean isSubmitAllowedFor(SystemAdministrator subject, DogLicenseApplication target) {
            return true;
        }
        /**
         * Is the 'receivePayment' action allowed for the given SystemAdministrator?
         */
        public static boolean isReceivePaymentAllowedFor(SystemAdministrator subject, DogLicenseApplication target) {
            return true;
        }
        /**
         * Is the 'reject' action allowed for the given SystemAdministrator?
         */
        public static boolean isRejectAllowedFor(SystemAdministrator subject, DogLicenseApplication target) {
            return true;
        }
        /**
         * Is the 'approve' action allowed for the given SystemAdministrator?
         */
        public static boolean isApproveAllowedFor(SystemAdministrator subject, DogLicenseApplication target) {
            return true;
        }
        /**
         * Is the 'cancel' action allowed for the given SystemAdministrator?
         */
        public static boolean isCancelAllowedFor(SystemAdministrator subject, DogLicenseApplication target) {
            return true;
        }
        public static boolean canRead(Resident subject, DogLicenseApplication target) {
            return subject.getId().equals(target.getDogOwner().getId());
        }
        public static boolean canUpdate(Resident subject, DogLicenseApplication target) {
            return subject.getId().equals(target.getDogOwner().getId()) && target.getStatus().compareTo(DogLicenseApplication.Status.Submitted) < 0;
        }
        public static boolean canDelete(Resident subject, DogLicenseApplication target) {
            return false;
        }
        /**
         * Is the 'submit' action allowed for the given Resident?
         */
        public static boolean isSubmitAllowedFor(Resident subject, DogLicenseApplication target) {
            return true;
        }
    }
    
}
