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
public class PersonService {
    
    public PersonService() {
    }
    
    private EntityManager getEntityManager() {
        return util.PersistenceHelper.getEntityManager();
    }
    
    
    public Person create(Person toCreate) {
        getEntityManager().persist(toCreate);
        return toCreate;
    }
    public Person find(Object id) {
        return getEntityManager().find(Person.class, id);
    }
    
    public Person refresh(Person toRefresh) {
        getEntityManager().refresh(toRefresh);
        return toRefresh; 
    }
    public Person merge(Person toMerge) {
        return getEntityManager().merge(toMerge);
    }
    public List<Person> findAll() {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Person> cq = cb.createQuery(Person.class);
        Root<Person> person = cq.from(Person.class);
        return getEntityManager().createQuery(cq.select(person).orderBy(cb.asc(person.get("id"))).distinct(true)).getResultList();
    }
    public Person update(Person toUpdate) {
        assert toUpdate.getId() != null;
        getEntityManager().persist(toUpdate);
        return toUpdate;
    }
    public void delete(Object id) {
        Person found = getEntityManager().find(Person.class, id);
        if (found != null) {
            getEntityManager().remove(found);
        }
    }
    
}
