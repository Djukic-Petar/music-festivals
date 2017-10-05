package db.helpers;

import com.corejsf.hibernate.HibernateUtil;
import db.Festival;
import db.Korisnik;
import db.Notifikacija;
import db.Ulaznica;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import services.SemaphoreUtil;

public class UlaznicaHelper {
    
    public Ulaznica getUlaznica(long ulaznicaId) {
        try {
            SemaphoreUtil.HIBERNATE_SEM.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(FestivalHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        org.hibernate.Transaction tx = session.beginTransaction();
        Ulaznica u = null;
        try {
            Criteria c = session.createCriteria(Ulaznica.class);
            c.add(Restrictions.eq("idUla", ulaznicaId));
            u = (Ulaznica) c.uniqueResult();
            tx.commit();
        } catch (HibernateException e) {
            tx.rollback();
        }
        SemaphoreUtil.HIBERNATE_SEM.release();
        return u;
    }
    
    public List<Ulaznica> getUlaznice(Festival festival) {
        try {
            SemaphoreUtil.HIBERNATE_SEM.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(FestivalHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        org.hibernate.Transaction tx = session.beginTransaction();
        List<Ulaznica> ulaznice = null;
        try {
            Criteria c = session.createCriteria(Ulaznica.class);
            c.add(Restrictions.eq("idFes", festival.getIdFes()));
            ulaznice = c.list();
            tx.commit();
        } catch (HibernateException e) {
            tx.rollback();
        }
        SemaphoreUtil.HIBERNATE_SEM.release();
        return ulaznice;
    }
    
    public List<Ulaznica> getSveUlazniceNonBlocking() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        org.hibernate.Transaction tx = session.beginTransaction();
        List<Ulaznica> ulaznice = null;
        try {
            Criteria c = session.createCriteria(Ulaznica.class);
            ulaznice = c.list();
            tx.commit();
        } catch (HibernateException e) {
            tx.rollback();
        }
        return ulaznice;
    }
    
    public void prodaj(Ulaznica u) {
        try {
            SemaphoreUtil.HIBERNATE_SEM.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(FestivalHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        org.hibernate.Transaction tx = session.beginTransaction();
        try {
            u.setProdato(true);
            session.saveOrUpdate(u);
            Festival festival = u.getFestival();
            int brDana = 1;
            if (u.getDatumNastupa() == null) {
                brDana = (int)((festival.getDatumDo().getTime() - festival.getDatumOd().getTime()) / (1000*60*60*24));
                brDana++;
            }
            festival.setBrojKupljenih(festival.getBrojKupljenih() + brDana*u.getKolicina());
            session.saveOrUpdate(festival);
            tx.commit();
        } catch (HibernateException e) {
            tx.rollback();
        }
        SemaphoreUtil.HIBERNATE_SEM.release();
    }
    
    private void vratiKarteFestivaluNonTransaction(Festival festival, Ulaznica ulaznica) {
        int brKarata;
        if (ulaznica.getDatumNastupa() != null) {
            brKarata = ulaznica.getKolicina();
        } else {
            int brDana = (int)((ulaznica.getFestival().getDatumDo().getTime() - ulaznica.getFestival().getDatumOd().getTime()) / (1000*60*60*24));
            brDana++;
            brKarata = brDana * ulaznica.getKolicina();
        }
        festival.setBrojKupljenih(festival.getBrojKupljenih() - brKarata);
    }

    public void istekniNonBlocking(Ulaznica ulaznica) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        org.hibernate.Transaction tx = session.beginTransaction();
        try {
            ulaznica.setIsteklo(true);
            session.saveOrUpdate(ulaznica);
            Korisnik korisnik = ulaznica.getKorisnik();
            korisnik.setBrojUpozorenja(korisnik.getBrojUpozorenja() + 1);
            session.saveOrUpdate(korisnik);
            vratiKarteFestivaluNonTransaction(ulaznica.getFestival(), ulaznica);
            session.saveOrUpdate(ulaznica.getFestival());
            Notifikacija notifikacija = new Notifikacija();
            notifikacija.setKorisnik(korisnik);
            notifikacija.setTekst("Istekla vam je rezervacija! Dobili ste " + korisnik.getBrojUpozorenja() + ". upozorenje! Sa 3 upozorenja gubite pravo pristupa sajtu!");
            session.save(notifikacija);
            tx.commit();
        } catch (HibernateException e) {
            tx.rollback();
        }
    }

    public void otkazi(Ulaznica ulaznica) {
        try {
            SemaphoreUtil.HIBERNATE_SEM.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(FestivalHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        org.hibernate.Transaction tx = session.beginTransaction();
        try {
            //vratiKarteFestivaluNonTransaction(ulaznica.getFestival(), ulaznica);
            Festival festival = ulaznica.getFestival();
            session.delete(ulaznica);
            session.update(festival);
            tx.commit();
        } catch (HibernateException e) {
            tx.rollback();
        }
        SemaphoreUtil.HIBERNATE_SEM.release();
    }
    
    public List<Ulaznica> getUlazniceForUser(Korisnik korisnik) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        org.hibernate.Transaction tx = session.beginTransaction();
        List<Ulaznica> ulaznice = new LinkedList<>();
        try {
            Criteria c = session.createCriteria(Ulaznica.class);
            c.add(Restrictions.eq("korisnik", korisnik));
            ulaznice = c.list();
            tx.commit();
        } catch (HibernateException e) {
            tx.rollback();
        }
        return ulaznice;
    }
    
}
