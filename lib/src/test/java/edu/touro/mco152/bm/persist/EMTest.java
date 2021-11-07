package edu.touro.mco152.bm.persist;

import edu.touro.mco152.bm.persist.EM;
import jakarta.persistence.EntityManager;
import org.eclipse.persistence.jpa.jpql.Assert;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EMTest {

    /**
     *Conformance- make sure the getEntityManager() method returns an Entity Manager
     */
    @Test
    void getEntityManager() {
        EntityManager entityManager = EM.getEntityManager();

        assertNotEquals(null, entityManager);
        assertTrue(entityManager instanceof EntityManager);
    }
}