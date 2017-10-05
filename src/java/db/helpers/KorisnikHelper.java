package db.helpers;

import com.corejsf.hibernate.HibernateUtil;
import db.Korisnik;
import db.Notifikacija;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import services.SemaphoreUtil;

public class KorisnikHelper {
    private Session session = null;
    
    public KorisnikHelper(){
    }
    
    public Korisnik getKorisnik(String username) {
        try {
            SemaphoreUtil.HIBERNATE_SEM.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(FestivalHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        session = HibernateUtil.getSessionFactory().getCurrentSession();
        Korisnik k = null;
        org.hibernate.Transaction tx = session.beginTransaction();
        try{
            Query q = session.createQuery("from Korisnik as korisnik where korisnik.username = '" + username + "'");
            k = (Korisnik) q.uniqueResult();
        } catch (Exception e) {
            tx.rollback();
            String message = e.getMessage();
            System.err.println(message);
        }
        session.close();
        SemaphoreUtil.HIBERNATE_SEM.release();
        return k;
    }
    
    public Korisnik getKorisnik(String uname, String lozinka) {
        try {
            SemaphoreUtil.HIBERNATE_SEM.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(FestivalHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        session = HibernateUtil.getSessionFactory().getCurrentSession();
        Korisnik k = null;
        org.hibernate.Transaction tx = session.beginTransaction();
        try {
            
            Query q = session.createQuery("from Korisnik as korisnik where korisnik.username = '"+uname+"' AND korisnik.password = '"+lozinka+"'");
            k = (Korisnik) q.uniqueResult();
            tx.commit();
        } catch (Exception e){
            tx.rollback();
        }
        SemaphoreUtil.HIBERNATE_SEM.release();
        return k;
    }
    
    public boolean addKorisnik(Korisnik k) {
        try {
            SemaphoreUtil.HIBERNATE_SEM.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(FestivalHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        session = HibernateUtil.getSessionFactory().getCurrentSession();
        boolean ret = false;
        org.hibernate.Transaction tx = session.beginTransaction();
        try {
            Query q = session.createQuery("from Korisnik as korisnik where korisnik.username = '"+k.getUsername()+"'");
            if (q.uniqueResult() == null) {
                session.saveOrUpdate(k);
                ret = true;
            }
            tx.commit();
        } catch (Exception e) {
            ret = false;
            tx.rollback();
        }
        SemaphoreUtil.HIBERNATE_SEM.release();
        return ret;
    }
    
    public void promeniLozinku(String password, Korisnik k) {
        try {
            SemaphoreUtil.HIBERNATE_SEM.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(FestivalHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        session = HibernateUtil.getSessionFactory().getCurrentSession();
        org.hibernate.Transaction tx = session.beginTransaction();
        try {
            k.setPassword(password);
            session.saveOrUpdate(k);
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        }
        SemaphoreUtil.HIBERNATE_SEM.release();
    }
    
    public List<Korisnik> getKorisniciWithType(boolean admin) {
        try {
            SemaphoreUtil.HIBERNATE_SEM.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(FestivalHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        session = HibernateUtil.getSessionFactory().getCurrentSession();
        List<Korisnik> korisnici = Collections.EMPTY_LIST;
        org.hibernate.Transaction tx = session.beginTransaction();
        try {
            Criteria c = session.createCriteria(Korisnik.class);
            c.add(Restrictions.eq("admin", admin));
            
            korisnici = c.list();
            tx.commit();
        } catch (HibernateException e){
            tx.rollback();
        }
        SemaphoreUtil.HIBERNATE_SEM.release();
        return korisnici;
    }
    
    public List<Korisnik> getNeodobreniKorisnici() {
        try {
            SemaphoreUtil.HIBERNATE_SEM.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(FestivalHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        session = HibernateUtil.getSessionFactory().getCurrentSession();
        List<Korisnik> korisnici = Collections.EMPTY_LIST;
        org.hibernate.Transaction tx = session.beginTransaction();
        try {
            Criteria c = session.createCriteria(Korisnik.class);
            c.add(Restrictions.eq("odobren", false));
            
            korisnici = c.list();
            tx.commit();
        } catch (HibernateException e){
            tx.rollback();
        }
        SemaphoreUtil.HIBERNATE_SEM.release();
        return korisnici;
    }

    public void deleteKorisnik(Korisnik k) {
        try {
            SemaphoreUtil.HIBERNATE_SEM.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(FestivalHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        session = HibernateUtil.getSessionFactory().getCurrentSession();
        org.hibernate.Transaction tx = session.beginTransaction();
        try{
            session.delete(k);
            tx.commit();
        } catch (Exception e){
            tx.rollback();
        }
        SemaphoreUtil.HIBERNATE_SEM.release();
    }

    public void updateKorisnikTimeStamp(Korisnik korisnik) {
        try {
            SemaphoreUtil.HIBERNATE_SEM.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(FestivalHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        korisnik.setPoslednjaPrijava(new Date());
        session = HibernateUtil.getSessionFactory().getCurrentSession();
        org.hibernate.Transaction tx = session.beginTransaction();
        try{
            session.update(korisnik);
            tx.commit();
        } catch (Exception e){
            tx.rollback();
        }
        SemaphoreUtil.HIBERNATE_SEM.release();
    }

    public List<Korisnik> getRecent(){
        try {
            SemaphoreUtil.HIBERNATE_SEM.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(FestivalHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        List<Korisnik> korisnici = Collections.EMPTY_LIST;
        session = HibernateUtil.getSessionFactory().getCurrentSession();
        org.hibernate.Transaction tx = session.beginTransaction();
        try{
            Criteria c = session.createCriteria(Korisnik.class);
            c.add(Restrictions.ne("username", "neregistrovani"));
            c.add(Restrictions.eq("admin", false));
            c.addOrder(Order.desc("poslednjaPrijava"));
            c.setMaxResults(10);
            korisnici = c.list();
            tx.commit();
        } catch (HibernateException e){
            tx.rollback();
        }
        SemaphoreUtil.HIBERNATE_SEM.release();
        return korisnici;
    }

    public void seenujNotifikacije(Korisnik korisnik) {
        try {
            SemaphoreUtil.HIBERNATE_SEM.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(FestivalHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        session = HibernateUtil.getSessionFactory().getCurrentSession();
        org.hibernate.Transaction tx = session.beginTransaction();
        try{
            for (Notifikacija notifikacija : korisnik.getNotifikacijas()) {
                notifikacija.setPregledano(true);
                session.saveOrUpdate(notifikacija);
            }
            tx.commit();
        } catch (HibernateException e){
            tx.rollback();
        }
        SemaphoreUtil.HIBERNATE_SEM.release();
    }
}