package edu.touro.mco152.bm.persist;
import edu.touro.mco152.bm.bmObserver;
import jakarta.persistence.EntityManager;

/**
 * An EM observer class that implements our bmObserver class
 */
public class dbObserver implements bmObserver {

    DiskRun run;

    /**
     * @param run DiskRun object
     */
    public dbObserver(DiskRun run){
        this.run = run;
    }

    /**
     * Overrides the update() method and updates the db with DiskRun info
     */
    @Override
    public void update() {
        EntityManager em = EM.getEntityManager();
        em.getTransaction().begin();
        em.persist(run);
        em.getTransaction().commit();
    }
}
