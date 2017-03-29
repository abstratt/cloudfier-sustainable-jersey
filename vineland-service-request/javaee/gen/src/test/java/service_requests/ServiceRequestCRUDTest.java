package service_requests;

import org.junit.*;  
import static org.junit.Assert.*;  
import java.util.*;
import java.util.Date;
import java.util.stream.*;
import java.text.*;
import java.util.function.*;
import javax.persistence.*;
import javax.enterprise.context.*;
import javax.inject.*;
import javax.ejb.*;
import java.sql.*;
import util.PersistenceHelper;


import service_requests.*;

public class ServiceRequestCRUDTest {
    private ServiceRequestService serviceRequestService;

    private EntityManager em;
    private EntityTransaction tx;

    @Before
    public void initEM() {
        this.em = util.PersistenceHelper.createTestSchema();
        util.PersistenceHelper.setEntityManager(em);
        this.tx = this.em.getTransaction();
        this.tx.begin();
        this.serviceRequestService = new ServiceRequestService();
    }
    
    @After
    public void tearDown() {
        if (tx != null)
            tx.rollback();
        if (em != null)
            em.close();    
    }
    
    @Test
    public void create() {
        ServiceRequest toCreate = new ServiceRequest();
        toCreate.setDescription("");
        toCreate.setStatus(ServiceRequest.Status.Submitted);
        ServiceRequest created = serviceRequestService.create(toCreate);
        Object id = created.getId();
        assertNotNull(id);
        PersistenceHelper.flush(true);
        ServiceRequest retrieved = serviceRequestService.find(id);
        assertNotNull(retrieved);
        assertEquals(id, retrieved.getId());
        assertEquals(created.getDescription(), retrieved.getDescription());
        assertEquals(created.getStatus(), retrieved.getStatus());
    }
    @Test
    public void retrieve() {
        ServiceRequest toCreate1 = new ServiceRequest();
        toCreate1.setDescription("");
        toCreate1.setStatus(ServiceRequest.Status.Submitted);
        serviceRequestService.create(toCreate1);
        ServiceRequest toCreate2 = new ServiceRequest();
        toCreate2.setDescription("");
        toCreate2.setStatus(ServiceRequest.Status.Submitted);
        serviceRequestService.create(toCreate2);
        PersistenceHelper.flush(true);
        ServiceRequest retrieved1 = serviceRequestService.find(toCreate1.getId());
        assertNotNull(retrieved1);
        assertEquals(toCreate1.getId(), retrieved1.getId());
        
        ServiceRequest retrieved2 = serviceRequestService.find(toCreate2.getId());
        assertNotNull(retrieved2);
        assertEquals(toCreate2.getId(), retrieved2.getId());
    }
    @Test
    public void update() {
        ServiceRequest toCreate = new ServiceRequest();
        toCreate.setDescription("");
        toCreate.setStatus(ServiceRequest.Status.Submitted);
        Object id = serviceRequestService.create(toCreate).getId();
        PersistenceHelper.flush(true);
        ServiceRequest retrieved = serviceRequestService.find(id);
        String originalValue = retrieved.getDescription();
        retrieved.setDescription("A memo value");
        serviceRequestService.update(retrieved);
        PersistenceHelper.flush(true);
        ServiceRequest updated = serviceRequestService.find(id); 
        assertNotEquals(originalValue, updated.getDescription());
    }
    @Test
    public void delete() {
        ServiceRequest toDelete = new ServiceRequest();
        toDelete.setDescription("");
        toDelete.setStatus(ServiceRequest.Status.Submitted);
        Object id = serviceRequestService.create(toDelete).getId();
        assertNotNull(serviceRequestService.find(id));
        serviceRequestService.delete(id);
        assertNull(serviceRequestService.find(id));
    }
} 
