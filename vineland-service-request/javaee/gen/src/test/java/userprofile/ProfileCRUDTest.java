package userprofile;

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


import userprofile.*;

public class ProfileCRUDTest {
    private ProfileService profileService;

    private EntityManager em;
    private EntityTransaction tx;

    @Before
    public void initEM() {
        this.em = util.PersistenceHelper.createTestSchema();
        util.PersistenceHelper.setEntityManager(em);
        this.tx = this.em.getTransaction();
        this.tx.begin();
        this.profileService = new ProfileService();
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
        Profile toCreate = new Profile();
        toCreate.setUsername("ccb156a0-392a-4952-ae4a-152ff48ace32");
        toCreate.setPassword("");
        Profile created = profileService.create(toCreate);
        Object id = created.getId();
        assertNotNull(id);
        PersistenceHelper.flush(true);
        Profile retrieved = profileService.find(id);
        assertNotNull(retrieved);
        assertEquals(id, retrieved.getId());
        assertEquals(created.getUsername(), retrieved.getUsername());
        assertEquals(created.getPassword(), retrieved.getPassword());
    }
    @Test
    public void retrieve() {
        Profile toCreate1 = new Profile();
        toCreate1.setUsername("6bbb9fbd-a328-46bb-9563-2cd8b8f25496");
        toCreate1.setPassword("");
        profileService.create(toCreate1);
        Profile toCreate2 = new Profile();
        toCreate2.setUsername("0f7963aa-64e4-4f66-8c8c-b9feaeabf964");
        toCreate2.setPassword("");
        profileService.create(toCreate2);
        PersistenceHelper.flush(true);
        Profile retrieved1 = profileService.find(toCreate1.getId());
        assertNotNull(retrieved1);
        assertEquals(toCreate1.getId(), retrieved1.getId());
        
        Profile retrieved2 = profileService.find(toCreate2.getId());
        assertNotNull(retrieved2);
        assertEquals(toCreate2.getId(), retrieved2.getId());
    }
    @Test
    public void update() {
        Profile toCreate = new Profile();
        toCreate.setUsername("36b659f2-1bad-4f4f-9a20-98f4d7360660");
        toCreate.setPassword("");
        Object id = profileService.create(toCreate).getId();
        PersistenceHelper.flush(true);
        Profile retrieved = profileService.find(id);
        String originalValue = retrieved.getPassword();
        retrieved.setPassword("A string value");
        profileService.update(retrieved);
        PersistenceHelper.flush(true);
        Profile updated = profileService.find(id); 
        assertNotEquals(originalValue, updated.getPassword());
    }
    @Test
    public void delete() {
        Profile toDelete = new Profile();
        toDelete.setUsername("7f91c645-574b-40b1-9503-cdcf4e02b9b5");
        toDelete.setPassword("");
        Object id = profileService.create(toDelete).getId();
        assertNotNull(profileService.find(id));
        profileService.delete(id);
        assertNull(profileService.find(id));
    }
} 
