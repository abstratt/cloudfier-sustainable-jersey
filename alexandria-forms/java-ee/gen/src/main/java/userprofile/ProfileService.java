package userprofile;

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

import applications.CityOfficial;
import applications.SystemAdministrator;
import applications.Resident;

@Stateless
public class ProfileService {
    
    public ProfileService() {
    }
    
    private EntityManager getEntityManager() {
        return util.PersistenceHelper.getEntityManager();
    }
    
    
    public Profile create(Profile toCreate) {
        getEntityManager().persist(toCreate);
        return toCreate;
    }
    public Profile find(Object id) {
        return getEntityManager().find(Profile.class, id);
    }
    
    // id-based finder
    public Profile findByUsername(String username) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Profile> cq = cb.createQuery(Profile.class);
        Root<Profile> profile = cq.from(Profile.class);
        return getEntityManager().createQuery(cq.select(profile).where(cb.equal(profile.get("username"), username))).getResultList().stream().findAny().orElse(null);
    }
    public Profile refresh(Profile toRefresh) {
        getEntityManager().refresh(toRefresh);
        return toRefresh; 
    }
    public Profile merge(Profile toMerge) {
        return getEntityManager().merge(toMerge);
    }
    public List<Profile> findAll() {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Profile> cq = cb.createQuery(Profile.class);
        Root<Profile> profile = cq.from(Profile.class);
        return getEntityManager().createQuery(cq.select(profile).orderBy(cb.asc(profile.get("id"))).distinct(true)).getResultList();
    }
    public Profile update(Profile toUpdate) {
        assert toUpdate.getId() != null;
        getEntityManager().persist(toUpdate);
        return toUpdate;
    }
    public void delete(Object id) {
        Profile found = getEntityManager().find(Profile.class, id);
        if (found != null) {
            getEntityManager().remove(found);
        }
    }
    
    /**
     * Returns related instances of Profile for the given 'roleAsCityOfficial' CityOfficial.
     */ 
    public Profile findUserProfileByRoleAsCityOfficial(CityOfficial roleAsCityOfficial) {
    	if (roleAsCityOfficial == null) {
    		return null;
    	}
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Profile> cq = cb.createQuery(Profile.class);
        Root<Profile> root = cq.from(Profile.class);
        return getEntityManager().createQuery(cq.select(root).where(cb.equal(root.get("roleAsCityOfficial").get("id"), roleAsCityOfficial.getId())).distinct(true)).getResultList().stream().findAny().orElse(null);
    }
    
    /**
     * Returns related instances of Profile for the given 'roleAsSystemAdministrator' SystemAdministrator.
     */ 
    public Profile findUserProfileByRoleAsSystemAdministrator(SystemAdministrator roleAsSystemAdministrator) {
    	if (roleAsSystemAdministrator == null) {
    		return null;
    	}
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Profile> cq = cb.createQuery(Profile.class);
        Root<Profile> root = cq.from(Profile.class);
        return getEntityManager().createQuery(cq.select(root).where(cb.equal(root.get("roleAsSystemAdministrator").get("id"), roleAsSystemAdministrator.getId())).distinct(true)).getResultList().stream().findAny().orElse(null);
    }
    
    /**
     * Returns related instances of Profile for the given 'roleAsResident' Resident.
     */ 
    public Profile findUserProfileByRoleAsResident(Resident roleAsResident) {
    	if (roleAsResident == null) {
    		return null;
    	}
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Profile> cq = cb.createQuery(Profile.class);
        Root<Profile> root = cq.from(Profile.class);
        return getEntityManager().createQuery(cq.select(root).where(cb.equal(root.get("roleAsResident").get("id"), roleAsResident.getId())).distinct(true)).getResultList().stream().findAny().orElse(null);
    }
}
