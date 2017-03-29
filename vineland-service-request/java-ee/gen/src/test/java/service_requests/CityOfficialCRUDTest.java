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

public class CityOfficialCRUDTest {
    private CityOfficialService cityOfficialService;

    private EntityManager em;
    private EntityTransaction tx;

    @Before
    public void initEM() {
        this.em = util.PersistenceHelper.createTestSchema();
        util.PersistenceHelper.setEntityManager(em);
        this.tx = this.em.getTransaction();
        this.tx.begin();
        this.cityOfficialService = new CityOfficialService();
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
        CityOfficial toCreate = new CityOfficial();
        toCreate.setFirstName("");
        toCreate.setLastName("");
        CityOfficial created = cityOfficialService.create(toCreate);
        Object id = created.getId();
        assertNotNull(id);
        PersistenceHelper.flush(true);
        CityOfficial retrieved = cityOfficialService.find(id);
        assertNotNull(retrieved);
        assertEquals(id, retrieved.getId());
        assertEquals(created.getFirstName(), retrieved.getFirstName());
        assertEquals(created.getLastName(), retrieved.getLastName());
    }
    @Test
    public void retrieve() {
        CityOfficial toCreate1 = new CityOfficial();
        toCreate1.setFirstName("");
        toCreate1.setLastName("");
        cityOfficialService.create(toCreate1);
        CityOfficial toCreate2 = new CityOfficial();
        toCreate2.setFirstName("");
        toCreate2.setLastName("");
        cityOfficialService.create(toCreate2);
        PersistenceHelper.flush(true);
        CityOfficial retrieved1 = cityOfficialService.find(toCreate1.getId());
        assertNotNull(retrieved1);
        assertEquals(toCreate1.getId(), retrieved1.getId());
        
        CityOfficial retrieved2 = cityOfficialService.find(toCreate2.getId());
        assertNotNull(retrieved2);
        assertEquals(toCreate2.getId(), retrieved2.getId());
    }
    @Test
    public void update() {
        CityOfficial toCreate = new CityOfficial();
        toCreate.setFirstName("");
        toCreate.setLastName("");
        Object id = cityOfficialService.create(toCreate).getId();
        PersistenceHelper.flush(true);
        CityOfficial retrieved = cityOfficialService.find(id);
        String originalValue = retrieved.getFirstName();
        retrieved.setFirstName("A string value");
        cityOfficialService.update(retrieved);
        PersistenceHelper.flush(true);
        CityOfficial updated = cityOfficialService.find(id); 
        assertNotEquals(originalValue, updated.getFirstName());
    }
    @Test
    public void delete() {
        CityOfficial toDelete = new CityOfficial();
        toDelete.setFirstName("");
        toDelete.setLastName("");
        Object id = cityOfficialService.create(toDelete).getId();
        assertNotNull(cityOfficialService.find(id));
        cityOfficialService.delete(id);
        assertNull(cityOfficialService.find(id));
    }
} 
