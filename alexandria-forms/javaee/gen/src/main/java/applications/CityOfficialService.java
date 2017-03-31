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
public class CityOfficialService {
    
    public CityOfficialService() {
    }
    
    private EntityManager getEntityManager() {
        return util.PersistenceHelper.getEntityManager();
    }
    
    
    public CityOfficial create(CityOfficial toCreate) {
        getEntityManager().persist(toCreate);
        return toCreate;
    }
    public CityOfficial find(Object id) {
        return getEntityManager().find(CityOfficial.class, id);
    }
    
    public CityOfficial refresh(CityOfficial toRefresh) {
        getEntityManager().refresh(toRefresh);
        return toRefresh; 
    }
    public CityOfficial merge(CityOfficial toMerge) {
        return getEntityManager().merge(toMerge);
    }
    public List<CityOfficial> findAll() {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<CityOfficial> cq = cb.createQuery(CityOfficial.class);
        Root<CityOfficial> cityOfficial = cq.from(CityOfficial.class);
        return getEntityManager().createQuery(cq.select(cityOfficial).orderBy(cb.asc(cityOfficial.get("id"))).distinct(true)).getResultList();
    }
    public CityOfficial update(CityOfficial toUpdate) {
        assert toUpdate.getId() != null;
        getEntityManager().persist(toUpdate);
        return toUpdate;
    }
    public void delete(Object id) {
        CityOfficial found = getEntityManager().find(CityOfficial.class, id);
        if (found != null) {
            getEntityManager().remove(found);
        }
    }
    
    /**
     * Returns related instances of CityOfficial for the given 'userProfile' Profile.
     */ 
    public CityOfficial findRoleAsCityOfficialByUserProfile(Profile userProfile) {
    	if (userProfile == null) {
    		return null;
    	}
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<CityOfficial> cq = cb.createQuery(CityOfficial.class);
        Root<CityOfficial> root = cq.from(CityOfficial.class);
        return getEntityManager().createQuery(cq.select(root).where(cb.equal(root.get("userProfile").get("id"), userProfile.getId())).distinct(true)).getResultList().stream().findAny().orElse(null);
    }
}
