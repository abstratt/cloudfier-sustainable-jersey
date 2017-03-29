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
public class ServiceRequestTypeService {
    
    public ServiceRequestTypeService() {
    }
    
    private EntityManager getEntityManager() {
        return util.PersistenceHelper.getEntityManager();
    }
    
    
    public ServiceRequestType create(ServiceRequestType toCreate) {
        getEntityManager().persist(toCreate);
        return toCreate;
    }
    public ServiceRequestType find(Object id) {
        return getEntityManager().find(ServiceRequestType.class, id);
    }
    
    public ServiceRequestType refresh(ServiceRequestType toRefresh) {
        getEntityManager().refresh(toRefresh);
        return toRefresh; 
    }
    public ServiceRequestType merge(ServiceRequestType toMerge) {
        return getEntityManager().merge(toMerge);
    }
    public List<ServiceRequestType> findAll() {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<ServiceRequestType> cq = cb.createQuery(ServiceRequestType.class);
        Root<ServiceRequestType> serviceRequestType = cq.from(ServiceRequestType.class);
        return getEntityManager().createQuery(cq.select(serviceRequestType).orderBy(cb.asc(serviceRequestType.get("id"))).distinct(true)).getResultList();
    }
    public ServiceRequestType update(ServiceRequestType toUpdate) {
        assert toUpdate.getId() != null;
        getEntityManager().persist(toUpdate);
        return toUpdate;
    }
    public void delete(Object id) {
        ServiceRequestType found = getEntityManager().find(ServiceRequestType.class, id);
        if (found != null) {
            getEntityManager().remove(found);
        }
    }
    
}
