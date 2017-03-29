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
public class ServiceRequestService {
    
    public ServiceRequestService() {
    }
    
    private EntityManager getEntityManager() {
        return util.PersistenceHelper.getEntityManager();
    }
    
    
    public ServiceRequest create(ServiceRequest toCreate) {
    	toCreate.setResident(asResident(getCurrentProfile()));
        getEntityManager().persist(toCreate);
        return toCreate;
    }
    public ServiceRequest find(Object id) {
        return getEntityManager().find(ServiceRequest.class, id);
    }
    
    public ServiceRequest refresh(ServiceRequest toRefresh) {
        getEntityManager().refresh(toRefresh);
        return toRefresh; 
    }
    public ServiceRequest merge(ServiceRequest toMerge) {
        return getEntityManager().merge(toMerge);
    }
    public List<ServiceRequest> findAll() {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<ServiceRequest> cq = cb.createQuery(ServiceRequest.class);
        Root<ServiceRequest> serviceRequest = cq.from(ServiceRequest.class);
        return getEntityManager().createQuery(cq.select(serviceRequest).orderBy(cb.asc(serviceRequest.get("id"))).distinct(true)).getResultList();
    }
    public ServiceRequest update(ServiceRequest toUpdate) {
        assert toUpdate.getId() != null;
        getEntityManager().persist(toUpdate);
        return toUpdate;
    }
    public void delete(Object id) {
        ServiceRequest found = getEntityManager().find(ServiceRequest.class, id);
        if (found != null) {
            getEntityManager().remove(found);
        }
    }
    public List<ServiceRequestType> getDomainForServiceRequestType(ServiceRequest context) {
    	return new ServiceRequestTypeService().findAll();
    }
    
    public List<Resident> getDomainForResident(ServiceRequest context) {
    	return new ResidentService().findAll();
    }
    
    /**
     * Returns related instances of ServiceRequest for the given 'serviceRequestType' ServiceRequestType.
     */ 
    public ServiceRequest findByServiceRequestType(ServiceRequestType serviceRequestType) {
    	if (serviceRequestType == null) {
    		return null;
    	}
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<ServiceRequest> cq = cb.createQuery(ServiceRequest.class);
        Root<ServiceRequest> root = cq.from(ServiceRequest.class);
        return getEntityManager().createQuery(cq.select(root).where(cb.equal(root.get("serviceRequestType").get("id"), serviceRequestType.getId())).distinct(true)).getResultList().stream().findAny().orElse(null);
    }
    
    /**
     * Returns related instances of ServiceRequest for the given 'resident' Resident.
     */ 
    public ServiceRequest findByResident(Resident resident) {
    	if (resident == null) {
    		return null;
    	}
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<ServiceRequest> cq = cb.createQuery(ServiceRequest.class);
        Root<ServiceRequest> root = cq.from(ServiceRequest.class);
        return getEntityManager().createQuery(cq.select(root).where(cb.equal(root.get("resident").get("id"), resident.getId())).distinct(true)).getResultList().stream().findAny().orElse(null);
    }
    
    /**
     * Returns related instances of ServiceRequest for the given 'staff' CityOfficial.
     */ 
    public ServiceRequest findByStaff(CityOfficial staff) {
    	if (staff == null) {
    		return null;
    	}
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<ServiceRequest> cq = cb.createQuery(ServiceRequest.class);
        Root<ServiceRequest> root = cq.from(ServiceRequest.class);
        return getEntityManager().createQuery(cq.select(root).where(cb.equal(root.get("staff").get("id"), staff.getId())).distinct(true)).getResultList().stream().findAny().orElse(null);
    }
    public Collection<ServiceRequest> myRequests() {
        return getEntityManager().createQuery(
            "SELECT DISTINCT serviceRequest_ FROM ServiceRequest serviceRequest_ WHERE serviceRequest_.resident = :systemUser", ServiceRequest.class
        ).getResultList();
    }
    
    public Collection<ServiceRequest> toValidate() {
        return getEntityManager().createQuery(
            "SELECT DISTINCT serviceRequest_ FROM ServiceRequest serviceRequest_ WHERE serviceRequest_.status = 'Submitted'", ServiceRequest.class
        ).getResultList();
    }
    
    public Collection<ServiceRequest> toComplete() {
        return getEntityManager().createQuery(
            "SELECT DISTINCT serviceRequest_ FROM ServiceRequest serviceRequest_ WHERE serviceRequest_.status = 'Accepted'", ServiceRequest.class
        ).getResultList();
    }
    
    public Collection<ServiceRequest> completed() {
        return getEntityManager().createQuery(
            "SELECT DISTINCT serviceRequest_ FROM ServiceRequest serviceRequest_ WHERE serviceRequest_.status = 'Completed'", ServiceRequest.class
        ).getResultList();
    }
    
    public Collection<ServiceRequest> byResident(Resident resident) {
        return getEntityManager().createQuery(
            "SELECT DISTINCT serviceRequest_ FROM ServiceRequest serviceRequest_ WHERE serviceRequest_.resident = :resident", ServiceRequest.class
        ).setParameter("resident", resident).getResultList();
    }
}
