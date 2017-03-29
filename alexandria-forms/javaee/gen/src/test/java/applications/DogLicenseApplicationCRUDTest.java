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

public class DogLicenseApplicationCRUDTest {
    private DogLicenseApplicationService dogLicenseApplicationService;

    private EntityManager em;
    private EntityTransaction tx;

    @Before
    public void initEM() {
        this.em = util.PersistenceHelper.createTestSchema();
        util.PersistenceHelper.setEntityManager(em);
        this.tx = this.em.getTransaction();
        this.tx.begin();
        this.dogLicenseApplicationService = new DogLicenseApplicationService();
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
        DogLicenseApplication toCreate = new DogLicenseApplication();
        toCreate.setStatus(DogLicenseApplication.Status.Draft);
        toCreate.setPetName("");
        toCreate.setHair(HairType.Short);
        toCreate.setColoring("");
        toCreate.setSex(Gender.Male);
        toCreate.setNeutered(false);
        toCreate.setBirthDate(new Timestamp(System.currentTimeMillis()));
        DogLicenseApplication created = dogLicenseApplicationService.create(toCreate);
        Object id = created.getId();
        assertNotNull(id);
        PersistenceHelper.flush(true);
        DogLicenseApplication retrieved = dogLicenseApplicationService.find(id);
        assertNotNull(retrieved);
        assertEquals(id, retrieved.getId());
        assertEquals(created.getStatus(), retrieved.getStatus());
        assertEquals(created.getPetName(), retrieved.getPetName());
        assertEquals(created.getHair(), retrieved.getHair());
        assertEquals(created.getColoring(), retrieved.getColoring());
        assertEquals(created.getSex(), retrieved.getSex());
        assertEquals(created.isNeutered(), retrieved.isNeutered());
        assertTrue((created.getBirthDate().getTime() - retrieved.getBirthDate().getTime()) < 10000);
    }
    @Test
    public void retrieve() {
        DogLicenseApplication toCreate1 = new DogLicenseApplication();
        toCreate1.setStatus(DogLicenseApplication.Status.Draft);
        toCreate1.setPetName("");
        toCreate1.setHair(HairType.Short);
        toCreate1.setColoring("");
        toCreate1.setSex(Gender.Male);
        toCreate1.setNeutered(false);
        toCreate1.setBirthDate(new Timestamp(System.currentTimeMillis()));
        dogLicenseApplicationService.create(toCreate1);
        DogLicenseApplication toCreate2 = new DogLicenseApplication();
        toCreate2.setStatus(DogLicenseApplication.Status.Draft);
        toCreate2.setPetName("");
        toCreate2.setHair(HairType.Short);
        toCreate2.setColoring("");
        toCreate2.setSex(Gender.Male);
        toCreate2.setNeutered(false);
        toCreate2.setBirthDate(new Timestamp(System.currentTimeMillis()));
        dogLicenseApplicationService.create(toCreate2);
        PersistenceHelper.flush(true);
        DogLicenseApplication retrieved1 = dogLicenseApplicationService.find(toCreate1.getId());
        assertNotNull(retrieved1);
        assertEquals(toCreate1.getId(), retrieved1.getId());
        
        DogLicenseApplication retrieved2 = dogLicenseApplicationService.find(toCreate2.getId());
        assertNotNull(retrieved2);
        assertEquals(toCreate2.getId(), retrieved2.getId());
    }
    @Test
    public void update() {
        DogLicenseApplication toCreate = new DogLicenseApplication();
        toCreate.setStatus(DogLicenseApplication.Status.Draft);
        toCreate.setPetName("");
        toCreate.setHair(HairType.Short);
        toCreate.setColoring("");
        toCreate.setSex(Gender.Male);
        toCreate.setNeutered(false);
        toCreate.setBirthDate(new Timestamp(System.currentTimeMillis()));
        Object id = dogLicenseApplicationService.create(toCreate).getId();
        PersistenceHelper.flush(true);
        DogLicenseApplication retrieved = dogLicenseApplicationService.find(id);
        DogLicenseApplication.Status originalValue = retrieved.getStatus();
        retrieved.setStatus(DogLicenseApplication.Status.Submitted);
        dogLicenseApplicationService.update(retrieved);
        PersistenceHelper.flush(true);
        DogLicenseApplication updated = dogLicenseApplicationService.find(id); 
        assertNotEquals(originalValue, updated.getStatus());
    }
    @Test
    public void delete() {
        DogLicenseApplication toDelete = new DogLicenseApplication();
        toDelete.setStatus(DogLicenseApplication.Status.Draft);
        toDelete.setPetName("");
        toDelete.setHair(HairType.Short);
        toDelete.setColoring("");
        toDelete.setSex(Gender.Male);
        toDelete.setNeutered(false);
        toDelete.setBirthDate(new Timestamp(System.currentTimeMillis()));
        Object id = dogLicenseApplicationService.create(toDelete).getId();
        assertNotNull(dogLicenseApplicationService.find(id));
        dogLicenseApplicationService.delete(id);
        assertNull(dogLicenseApplicationService.find(id));
    }
} 
