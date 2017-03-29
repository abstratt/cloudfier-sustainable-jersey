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

public class ResidentCRUDTest {
    private ResidentService residentService;

    private EntityManager em;
    private EntityTransaction tx;

    @Before
    public void initEM() {
        this.em = util.PersistenceHelper.createTestSchema();
        util.PersistenceHelper.setEntityManager(em);
        this.tx = this.em.getTransaction();
        this.tx.begin();
        this.residentService = new ResidentService();
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
        Resident toCreate = new Resident();
        toCreate.setFirstName("");
        toCreate.setLastName("");
        Resident created = residentService.create(toCreate);
        Object id = created.getId();
        assertNotNull(id);
        PersistenceHelper.flush(true);
        Resident retrieved = residentService.find(id);
        assertNotNull(retrieved);
        assertEquals(id, retrieved.getId());
        assertEquals(created.getFirstName(), retrieved.getFirstName());
        assertEquals(created.getLastName(), retrieved.getLastName());
    }
    @Test
    public void retrieve() {
        Resident toCreate1 = new Resident();
        toCreate1.setFirstName("");
        toCreate1.setLastName("");
        residentService.create(toCreate1);
        Resident toCreate2 = new Resident();
        toCreate2.setFirstName("");
        toCreate2.setLastName("");
        residentService.create(toCreate2);
        PersistenceHelper.flush(true);
        Resident retrieved1 = residentService.find(toCreate1.getId());
        assertNotNull(retrieved1);
        assertEquals(toCreate1.getId(), retrieved1.getId());
        
        Resident retrieved2 = residentService.find(toCreate2.getId());
        assertNotNull(retrieved2);
        assertEquals(toCreate2.getId(), retrieved2.getId());
    }
    @Test
    public void update() {
        Resident toCreate = new Resident();
        toCreate.setFirstName("");
        toCreate.setLastName("");
        Object id = residentService.create(toCreate).getId();
        PersistenceHelper.flush(true);
        Resident retrieved = residentService.find(id);
        String originalValue = retrieved.getFirstName();
        retrieved.setFirstName("A string value");
        residentService.update(retrieved);
        PersistenceHelper.flush(true);
        Resident updated = residentService.find(id); 
        assertNotEquals(originalValue, updated.getFirstName());
    }
    @Test
    public void delete() {
        Resident toDelete = new Resident();
        toDelete.setFirstName("");
        toDelete.setLastName("");
        Object id = residentService.create(toDelete).getId();
        assertNotNull(residentService.find(id));
        residentService.delete(id);
        assertNull(residentService.find(id));
    }
} 
