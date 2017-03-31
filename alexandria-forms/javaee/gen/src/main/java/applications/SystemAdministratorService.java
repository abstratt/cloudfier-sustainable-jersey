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
public class SystemAdministratorService {
    
    public SystemAdministratorService() {
    }
    
    private EntityManager getEntityManager() {
        return util.PersistenceHelper.getEntityManager();
    }
    
    
    public SystemAdministrator create(SystemAdministrator toCreate) {
        getEntityManager().persist(toCreate);
        return toCreate;
    }
    public SystemAdministrator find(Object id) {
        return getEntityManager().find(SystemAdministrator.class, id);
    }
    
    public SystemAdministrator refresh(SystemAdministrator toRefresh) {
        getEntityManager().refresh(toRefresh);
        return toRefresh; 
    }
    public SystemAdministrator merge(SystemAdministrator toMerge) {
        return getEntityManager().merge(toMerge);
    }
    public List<SystemAdministrator> findAll() {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<SystemAdministrator> cq = cb.createQuery(SystemAdministrator.class);
        Root<SystemAdministrator> systemAdministrator = cq.from(SystemAdministrator.class);
        return getEntityManager().createQuery(cq.select(systemAdministrator).orderBy(cb.asc(systemAdministrator.get("id"))).distinct(true)).getResultList();
    }
    public SystemAdministrator update(SystemAdministrator toUpdate) {
        assert toUpdate.getId() != null;
        getEntityManager().persist(toUpdate);
        return toUpdate;
    }
    public void delete(Object id) {
        SystemAdministrator found = getEntityManager().find(SystemAdministrator.class, id);
        if (found != null) {
            getEntityManager().remove(found);
        }
    }
    
    /**
     * Returns related instances of SystemAdministrator for the given 'userProfile' Profile.
     */ 
    public SystemAdministrator findRoleAsSystemAdministratorByUserProfile(Profile userProfile) {
    	if (userProfile == null) {
    		return null;
    	}
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<SystemAdministrator> cq = cb.createQuery(SystemAdministrator.class);
        Root<SystemAdministrator> root = cq.from(SystemAdministrator.class);
        return getEntityManager().createQuery(cq.select(root).where(cb.equal(root.get("userProfile").get("id"), userProfile.getId())).distinct(true)).getResultList().stream().findAny().orElse(null);
    }
}
