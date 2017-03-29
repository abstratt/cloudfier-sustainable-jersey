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

public class ServiceRequestTypeCRUDTest {
    private ServiceRequestTypeService serviceRequestTypeService;

    private EntityManager em;
    private EntityTransaction tx;

    @Before
    public void initEM() {
        this.em = util.PersistenceHelper.createTestSchema();
        util.PersistenceHelper.setEntityManager(em);
        this.tx = this.em.getTransaction();
        this.tx.begin();
        this.serviceRequestTypeService = new ServiceRequestTypeService();
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
        ServiceRequestType toCreate = new ServiceRequestType();
        toCreate.setName("");
        ServiceRequestType created = serviceRequestTypeService.create(toCreate);
        Object id = created.getId();
        assertNotNull(id);
        PersistenceHelper.flush(true);
        ServiceRequestType retrieved = serviceRequestTypeService.find(id);
        assertNotNull(retrieved);
        assertEquals(id, retrieved.getId());
        assertEquals(created.getName(), retrieved.getName());
    }
    @Test
    public void retrieve() {
        ServiceRequestType toCreate1 = new ServiceRequestType();
        toCreate1.setName("");
        serviceRequestTypeService.create(toCreate1);
        ServiceRequestType toCreate2 = new ServiceRequestType();
        toCreate2.setName("");
        serviceRequestTypeService.create(toCreate2);
        PersistenceHelper.flush(true);
        ServiceRequestType retrieved1 = serviceRequestTypeService.find(toCreate1.getId());
        assertNotNull(retrieved1);
        assertEquals(toCreate1.getId(), retrieved1.getId());
        
        ServiceRequestType retrieved2 = serviceRequestTypeService.find(toCreate2.getId());
        assertNotNull(retrieved2);
        assertEquals(toCreate2.getId(), retrieved2.getId());
    }
    @Test
    public void update() {
        ServiceRequestType toCreate = new ServiceRequestType();
        toCreate.setName("");
        Object id = serviceRequestTypeService.create(toCreate).getId();
        PersistenceHelper.flush(true);
        ServiceRequestType retrieved = serviceRequestTypeService.find(id);
        String originalValue = retrieved.getName();
        retrieved.setName("A string value");
        serviceRequestTypeService.update(retrieved);
        PersistenceHelper.flush(true);
        ServiceRequestType updated = serviceRequestTypeService.find(id); 
        assertNotEquals(originalValue, updated.getName());
    }
    @Test
    public void delete() {
        ServiceRequestType toDelete = new ServiceRequestType();
        toDelete.setName("");
        Object id = serviceRequestTypeService.create(toDelete).getId();
        assertNotNull(serviceRequestTypeService.find(id));
        serviceRequestTypeService.delete(id);
        assertNull(serviceRequestTypeService.find(id));
    }
} 
