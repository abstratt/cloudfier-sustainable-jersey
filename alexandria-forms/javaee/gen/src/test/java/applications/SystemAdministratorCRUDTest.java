package applications;

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


import applications.*;

public class SystemAdministratorCRUDTest {
    private SystemAdministratorService systemAdministratorService;

    private EntityManager em;
    private EntityTransaction tx;

    @Before
    public void initEM() {
        this.em = util.PersistenceHelper.createTestSchema();
        util.PersistenceHelper.setEntityManager(em);
        this.tx = this.em.getTransaction();
        this.tx.begin();
        this.systemAdministratorService = new SystemAdministratorService();
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
        SystemAdministrator toCreate = new SystemAdministrator();
        toCreate.setFirstName("");
        toCreate.setLastName("");
        SystemAdministrator created = systemAdministratorService.create(toCreate);
        Object id = created.getId();
        assertNotNull(id);
        PersistenceHelper.flush(true);
        SystemAdministrator retrieved = systemAdministratorService.find(id);
        assertNotNull(retrieved);
        assertEquals(id, retrieved.getId());
        assertEquals(created.getFirstName(), retrieved.getFirstName());
        assertEquals(created.getLastName(), retrieved.getLastName());
    }
    @Test
    public void retrieve() {
        SystemAdministrator toCreate1 = new SystemAdministrator();
        toCreate1.setFirstName("");
        toCreate1.setLastName("");
        systemAdministratorService.create(toCreate1);
        SystemAdministrator toCreate2 = new SystemAdministrator();
        toCreate2.setFirstName("");
        toCreate2.setLastName("");
        systemAdministratorService.create(toCreate2);
        PersistenceHelper.flush(true);
        SystemAdministrator retrieved1 = systemAdministratorService.find(toCreate1.getId());
        assertNotNull(retrieved1);
        assertEquals(toCreate1.getId(), retrieved1.getId());
        
        SystemAdministrator retrieved2 = systemAdministratorService.find(toCreate2.getId());
        assertNotNull(retrieved2);
        assertEquals(toCreate2.getId(), retrieved2.getId());
    }
    @Test
    public void update() {
        SystemAdministrator toCreate = new SystemAdministrator();
        toCreate.setFirstName("");
        toCreate.setLastName("");
        Object id = systemAdministratorService.create(toCreate).getId();
        PersistenceHelper.flush(true);
        SystemAdministrator retrieved = systemAdministratorService.find(id);
        String originalValue = retrieved.getFirstName();
        retrieved.setFirstName("A string value");
        systemAdministratorService.update(retrieved);
        PersistenceHelper.flush(true);
        SystemAdministrator updated = systemAdministratorService.find(id); 
        assertNotEquals(originalValue, updated.getFirstName());
    }
    @Test
    public void delete() {
        SystemAdministrator toDelete = new SystemAdministrator();
        toDelete.setFirstName("");
        toDelete.setLastName("");
        Object id = systemAdministratorService.create(toDelete).getId();
        assertNotNull(systemAdministratorService.find(id));
        systemAdministratorService.delete(id);
        assertNull(systemAdministratorService.find(id));
    }
} 
