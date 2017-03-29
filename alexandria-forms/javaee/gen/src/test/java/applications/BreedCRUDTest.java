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

public class BreedCRUDTest {
    private BreedService breedService;

    private EntityManager em;
    private EntityTransaction tx;

    @Before
    public void initEM() {
        this.em = util.PersistenceHelper.createTestSchema();
        util.PersistenceHelper.setEntityManager(em);
        this.tx = this.em.getTransaction();
        this.tx.begin();
        this.breedService = new BreedService();
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
        Breed toCreate = new Breed();
        toCreate.setBreed("");
        Breed created = breedService.create(toCreate);
        Object id = created.getId();
        assertNotNull(id);
        PersistenceHelper.flush(true);
        Breed retrieved = breedService.find(id);
        assertNotNull(retrieved);
        assertEquals(id, retrieved.getId());
        assertEquals(created.getBreed(), retrieved.getBreed());
    }
    @Test
    public void retrieve() {
        Breed toCreate1 = new Breed();
        toCreate1.setBreed("");
        breedService.create(toCreate1);
        Breed toCreate2 = new Breed();
        toCreate2.setBreed("");
        breedService.create(toCreate2);
        PersistenceHelper.flush(true);
        Breed retrieved1 = breedService.find(toCreate1.getId());
        assertNotNull(retrieved1);
        assertEquals(toCreate1.getId(), retrieved1.getId());
        
        Breed retrieved2 = breedService.find(toCreate2.getId());
        assertNotNull(retrieved2);
        assertEquals(toCreate2.getId(), retrieved2.getId());
    }
    @Test
    public void update() {
        Breed toCreate = new Breed();
        toCreate.setBreed("");
        Object id = breedService.create(toCreate).getId();
        PersistenceHelper.flush(true);
        Breed retrieved = breedService.find(id);
        String originalValue = retrieved.getBreed();
        retrieved.setBreed("A string value");
        breedService.update(retrieved);
        PersistenceHelper.flush(true);
        Breed updated = breedService.find(id); 
        assertNotEquals(originalValue, updated.getBreed());
    }
    @Test
    public void delete() {
        Breed toDelete = new Breed();
        toDelete.setBreed("");
        Object id = breedService.create(toDelete).getId();
        assertNotNull(breedService.find(id));
        breedService.delete(id);
        assertNull(breedService.find(id));
    }
} 
