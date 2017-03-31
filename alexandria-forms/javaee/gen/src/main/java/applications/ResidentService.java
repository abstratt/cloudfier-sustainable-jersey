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
public class ResidentService {
    
    public ResidentService() {
    }
    
    private EntityManager getEntityManager() {
        return util.PersistenceHelper.getEntityManager();
    }
    
    
    public Resident create(Resident toCreate) {
        getEntityManager().persist(toCreate);
        return toCreate;
    }
    public Resident find(Object id) {
        return getEntityManager().find(Resident.class, id);
    }
    
    public Resident refresh(Resident toRefresh) {
        getEntityManager().refresh(toRefresh);
        return toRefresh; 
    }
    public Resident merge(Resident toMerge) {
        return getEntityManager().merge(toMerge);
    }
    public List<Resident> findAll() {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Resident> cq = cb.createQuery(Resident.class);
        Root<Resident> resident = cq.from(Resident.class);
        return getEntityManager().createQuery(cq.select(resident).orderBy(cb.asc(resident.get("id"))).distinct(true)).getResultList();
    }
    public Resident update(Resident toUpdate) {
        assert toUpdate.getId() != null;
        getEntityManager().persist(toUpdate);
        return toUpdate;
    }
    public void delete(Object id) {
        Resident found = getEntityManager().find(Resident.class, id);
        if (found != null) {
            getEntityManager().remove(found);
        }
    }
    public List<DogLicenseApplication> getDomainForDogLicenseApplications(Resident context) {
    	return new DogLicenseApplicationService().findAll();
    }
    
    /**
     * Returns related instances of Resident for the given 'userProfile' Profile.
     */ 
    public Resident findRoleAsResidentByUserProfile(Profile userProfile) {
    	if (userProfile == null) {
    		return null;
    	}
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Resident> cq = cb.createQuery(Resident.class);
        Root<Resident> root = cq.from(Resident.class);
        return getEntityManager().createQuery(cq.select(root).where(cb.equal(root.get("userProfile").get("id"), userProfile.getId())).distinct(true)).getResultList().stream().findAny().orElse(null);
    }
    
    /**
     * Returns related instances of Resident for the given 'dogLicenseApplications' DogLicenseApplication.
     */ 
    public Resident findDogOwnerByDogLicenseApplications(DogLicenseApplication dogLicenseApplications) {
    	if (dogLicenseApplications == null) {
    		return null;
    	}
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Resident> cq = cb.createQuery(Resident.class);
        Root<Resident> root = cq.from(Resident.class);
        return getEntityManager().createQuery(cq.select(root).where(cb.equal(root.get("dogLicenseApplications").get("id"), dogLicenseApplications.getId())).distinct(true)).getResultList().stream().findAny().orElse(null);
    }
    public Collection<Resident> findByName(String firstName, String lastName) {
        return getEntityManager().createQuery(
            "SELECT DISTINCT resident_ FROM Resident resident_ WHERE (resident_.firstName = :firstName OR resident_.lastName = :lastName)", Resident.class
        ).setParameter("firstName", firstName).setParameter("lastName", lastName).getResultList();
    }
}
