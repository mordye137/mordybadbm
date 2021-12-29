package edu.touro.mco152.bm.persist;
import edu.touro.mco152.bm.bmObserver;
import jakarta.persistence.EntityManager;

public class dbObserver implements bmObserver {

    DiskRun run;

    public dbObserver(DiskRun run){
        this.run = run;
    }

    @Override
    public void update() {
        EntityManager em = EM.getEntityManager();
        em.getTransaction().begin();
        em.persist(run);
        em.getTransaction().commit();
    }
}
