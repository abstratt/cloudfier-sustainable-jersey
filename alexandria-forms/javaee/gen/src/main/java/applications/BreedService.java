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

import userprofile.*;
import applications.CityOfficial;
import applications.SystemAdministrator;
import applications.Resident;

@Stateless
public class BreedService {
    
    public BreedService() {
    }
    
    private EntityManager getEntityManager() {
        return util.PersistenceHelper.getEntityManager();
    }
    
    
    public Breed create(Breed toCreate) {
        getEntityManager().persist(toCreate);
        return toCreate;
    }
    public Breed find(Object id) {
        return getEntityManager().find(Breed.class, id);
    }
    
    public Breed refresh(Breed toRefresh) {
        getEntityManager().refresh(toRefresh);
        return toRefresh; 
    }
    public Breed merge(Breed toMerge) {
        return getEntityManager().merge(toMerge);
    }
    public List<Breed> findAll() {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Breed> cq = cb.createQuery(Breed.class);
        Root<Breed> breed = cq.from(Breed.class);
        return getEntityManager().createQuery(cq.select(breed).orderBy(cb.asc(breed.get("id"))).distinct(true)).getResultList();
    }
    public Breed update(Breed toUpdate) {
        assert toUpdate.getId() != null;
        getEntityManager().persist(toUpdate);
        return toUpdate;
    }
    public void delete(Object id) {
        Breed found = getEntityManager().find(Breed.class, id);
        if (found != null) {
            getEntityManager().remove(found);
        }
    }
    
}
