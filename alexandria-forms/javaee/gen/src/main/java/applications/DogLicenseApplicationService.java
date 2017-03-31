package applications;

import java.util.*;
import java.util.stream.*;
import java.util.function.*;
import java.io.Serializable;
import javax.persistence.*;
import javax.persistence.criteria.*;
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

@Stateless
public class DogLicenseApplicationService {
    
    public DogLicenseApplicationService() {
    }
    
    private EntityManager getEntityManager() {
        return util.PersistenceHelper.getEntityManager();
    }
    
    
    public DogLicenseApplication create(DogLicenseApplication toCreate) {
    	toCreate.setDogOwner(asResident(getCurrentProfile()));
        getEntityManager().persist(toCreate);
        return toCreate;
    }
    public DogLicenseApplication find(Object id) {
        return getEntityManager().find(DogLicenseApplication.class, id);
    }
    
    // id-based finder
    public DogLicenseApplication findByInternalNumber(long internalNumber) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<DogLicenseApplication> cq = cb.createQuery(DogLicenseApplication.class);
        Root<DogLicenseApplication> dogLicenseApplication = cq.from(DogLicenseApplication.class);
        return getEntityManager().createQuery(cq.select(dogLicenseApplication).where(cb.equal(dogLicenseApplication.get("internalNumber"), internalNumber))).getResultList().stream().findAny().orElse(null);
    }
    // id-based finder
    public DogLicenseApplication findByNumber(String number) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<DogLicenseApplication> cq = cb.createQuery(DogLicenseApplication.class);
        Root<DogLicenseApplication> dogLicenseApplication = cq.from(DogLicenseApplication.class);
        return getEntityManager().createQuery(cq.select(dogLicenseApplication).where(cb.equal(dogLicenseApplication.get("number"), number))).getResultList().stream().findAny().orElse(null);
    }
    public DogLicenseApplication refresh(DogLicenseApplication toRefresh) {
        getEntityManager().refresh(toRefresh);
        return toRefresh; 
    }
    public DogLicenseApplication merge(DogLicenseApplication toMerge) {
        return getEntityManager().merge(toMerge);
    }
    public List<DogLicenseApplication> findAll() {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<DogLicenseApplication> cq = cb.createQuery(DogLicenseApplication.class);
        Root<DogLicenseApplication> dogLicenseApplication = cq.from(DogLicenseApplication.class);
        return getEntityManager().createQuery(cq.select(dogLicenseApplication).orderBy(cb.asc(dogLicenseApplication.get("id"))).distinct(true)).getResultList();
    }
    public DogLicenseApplication update(DogLicenseApplication toUpdate) {
        assert toUpdate.getId() != null;
        getEntityManager().persist(toUpdate);
        return toUpdate;
    }
    public void delete(Object id) {
        DogLicenseApplication found = getEntityManager().find(DogLicenseApplication.class, id);
        if (found != null) {
            getEntityManager().remove(found);
        }
    }
    public List<Resident> getDomainForDogOwner(DogLicenseApplication context) {
    	return new ResidentService().findAll();
    }
    
    public List<Breed> getDomainForBreed(DogLicenseApplication context) {
    	return new BreedService().findAll();
    }
    
    /**
     * Returns related instances of DogLicenseApplication for the given 'dogOwner' Resident.
     */ 
    public Collection<DogLicenseApplication> findDogLicenseApplicationsByDogOwner(Resident dogOwner) {
    	if (dogOwner == null) {
    		return Collections.emptyList();
    	}
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<DogLicenseApplication> cq = cb.createQuery(DogLicenseApplication.class);
        Root<DogLicenseApplication> root = cq.from(DogLicenseApplication.class);
        return getEntityManager().createQuery(cq.select(root).where(cb.equal(root.get("dogOwner").get("id"), dogOwner.getId())).distinct(true)).getResultList();
    }
    
    /**
     * Returns related instances of DogLicenseApplication for the given 'breed' Breed.
     */ 
    public DogLicenseApplication findByBreed(Breed breed) {
    	if (breed == null) {
    		return null;
    	}
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<DogLicenseApplication> cq = cb.createQuery(DogLicenseApplication.class);
        Root<DogLicenseApplication> root = cq.from(DogLicenseApplication.class);
        return getEntityManager().createQuery(cq.select(root).where(cb.equal(root.get("breed").get("id"), breed.getId())).distinct(true)).getResultList().stream().findAny().orElse(null);
    }
    public Collection<DogLicenseApplication> myRequests() {
        return getEntityManager().createQuery(
            "SELECT DISTINCT dogLicenseApplication_ FROM DogLicenseApplication dogLicenseApplication_ WHERE dogLicenseApplication_.dogOwner = :systemUser", DogLicenseApplication.class
        ).getResultList();
    }
    
    public Collection<DogLicenseApplication> pendingPayment() {
        return getEntityManager().createQuery(
            "SELECT DISTINCT dogLicenseApplication_ FROM DogLicenseApplication dogLicenseApplication_ WHERE dogLicenseApplication_.status = 'Submitted'", DogLicenseApplication.class
        ).getResultList();
    }
    
    public Collection<DogLicenseApplication> pendingApproval() {
        return getEntityManager().createQuery(
            "SELECT DISTINCT dogLicenseApplication_ FROM DogLicenseApplication dogLicenseApplication_ WHERE dogLicenseApplication_.status = 'Paid'", DogLicenseApplication.class
        ).getResultList();
    }
    
    public Collection<DogLicenseApplication> paidWithinPeriod(Date start, Date end_) {
        return getEntityManager().createQuery(
            "SELECT DISTINCT dogLicenseApplication_ FROM DogLicenseApplication dogLicenseApplication_ WHERE dogLicenseApplication_.status >= 'Paid' AND (:start IS NULL OR dogLicenseApplication_.datePaid >= :start) AND (:end_ IS NULL OR dogLicenseApplication_.datePaid <= :end_)", DogLicenseApplication.class
        ).setParameter("start", start).setParameter("end_", end_).getResultList();
    }
    
    public Collection<Resident> dogOwners() {
        return getEntityManager().createQuery(
            "SELECT dogLicenseApplication_.dogOwner FROM DogLicenseApplication dogLicenseApplication_", Resident.class
        ).getResultList();
    }
}
