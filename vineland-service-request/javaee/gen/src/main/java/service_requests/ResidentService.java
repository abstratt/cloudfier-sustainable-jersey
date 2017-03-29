package service_requests;

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

import userprofile.*;
import service_requests.Resident;
import service_requests.CityOfficial;
import service_requests.SystemAdministrator;

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
}
