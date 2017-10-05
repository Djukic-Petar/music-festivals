package db.helpers;

import com.corejsf.hibernate.HibernateUtil;
import db.Festival;
import db.Galerija;
import db.Korisnik;
import db.Nastup;
import db.Notifikacija;
import db.Ocena;
import db.Ulaznica;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import services.OcenaComparator;
import services.SemaphoreUtil;

public class FestivalHelper {
    private Session session = null;
    
    private static final UlaznicaHelper ULAZNICA_HELPER = new UlaznicaHelper();
    
    public List<Festival> getFestivals(String name, String location, String artist, Date dateFrom, Date dateTo) {
        List<Festival> festivals = new ArrayList<>();
        session = HibernateUtil.getSessionFactory().getCurrentSession();
        org.hibernate.Transaction tx = session.beginTransaction();
        try {
            Criteria c = session.createCriteria(Festival.class);
            c.add(Restrictions.eq("otkazano", false));
            if (name != null && !("".equals(name))) {
                c.add(Restrictions.ilike("naziv", name, MatchMode.ANYWHERE));
            }
            if (location != null && !("".equals(location))) {
                c.add(Restrictions.ilike("mesto", location, MatchMode.ANYWHERE));
            }
            if (dateFrom != null) {
                c.add(Restrictions.ge("datumOd", dateFrom));
            }
            if (dateTo != null) {
                c.add(Restrictions.le("datumDo", dateTo));
            }
            if (dateTo == null && dateFrom == null) {
                c.add(Restrictions.gt("datumDo", new Date()));
            }
            if (artist != null && !("".equals(artist))) {
                c.createAlias("nastups", "nastup");
                c.add(Restrictions.ilike("nastup.izvodjac", artist, MatchMode.ANYWHERE));
            }
            festivals = c.list();
            tx.commit();
        } catch (HibernateException ex) {
            tx.rollback();
        }
        return festivals;
    }
    
    public List<Festival> getPetNajvisePregleda() {
        try {
            SemaphoreUtil.HIBERNATE_SEM.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(FestivalHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        session = HibernateUtil.getSessionFactory().getCurrentSession();
        List<Festival> festivali = null;
        Transaction tx = session.beginTransaction();
        try {
            Criteria c = session.createCriteria(Festival.class);
            c.add(Restrictions.eq("otkazano", false));
            c.addOrder(Order.desc("pregledi"));
            c.setMaxResults(5);
            festivali = c.list();
            tx.commit();
        } catch (HibernateException e) {
            tx.rollback();
        }
        SemaphoreUtil.HIBERNATE_SEM.release();
        return festivali;
    }
    
    public List<Festival> getPetNajskorijih() {
        try {
            SemaphoreUtil.HIBERNATE_SEM.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(FestivalHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        session = HibernateUtil.getSessionFactory().getCurrentSession();
        List<Festival> festivali = null;
        
        Transaction tx = session.beginTransaction();
        try {
            Criteria c = session.createCriteria(Festival.class);
            c.add(Restrictions.eq("otkazano", false));
            c.add(Restrictions.gt("datumDo", new Date()));
            c.addOrder(Order.asc("datumOd"));
            c.setMaxResults(5);
            festivali = c.list();
            tx.commit();
        } catch (HibernateException e) {
            tx.rollback();
        }
        SemaphoreUtil.HIBERNATE_SEM.release();
        return festivali;
    }
    
    public List<Festival> getPetNajviseUlaznica() {
        try {
            SemaphoreUtil.HIBERNATE_SEM.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(FestivalHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        session = HibernateUtil.getSessionFactory().getCurrentSession();
        List<Festival> festivali = null;
        
        Transaction tx = session.beginTransaction();
        try {
            Criteria c = session.createCriteria(Festival.class);
            c.add(Restrictions.eq("otkazano", false));
            c.addOrder(Order.desc("brojKupljenih"));
            c.setMaxResults(5);
            festivali = c.list();
            tx.commit();
        } catch (HibernateException e) {
            tx.rollback();
        }
        SemaphoreUtil.HIBERNATE_SEM.release();
        return festivali;
    }
    
    public List<Festival> getDaysEvents(Date date) {
        List<Festival> festivals = new ArrayList<>();
        session = HibernateUtil.getSessionFactory().getCurrentSession();
        org.hibernate.Transaction tx = session.beginTransaction();
        try {
            Criteria c = session.createCriteria(Festival.class);
            c.add(Restrictions.eq("otkazano", false));
            c.add(Restrictions.le("datumOd", date));
            c.add(Restrictions.ge("datumDo", date));
            festivals = c.list();
            tx.commit();
        } catch (HibernateException ex) {
            tx.rollback();
        }
        return festivals;
    }
    
    public void otkazi(Festival festival) {
        try {
            SemaphoreUtil.HIBERNATE_SEM.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(FestivalHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction tx = session.beginTransaction();
        try {
            festival.setOtkazano(true);
            session.saveOrUpdate(festival);
            Set<Korisnik> korisnici = new HashSet<>();
            List<Notifikacija> notifikacije = new LinkedList<>();
            List<Ulaznica> ulaznice = null;
            Criteria c = session.createCriteria(Ulaznica.class);
            c.add(Restrictions.eq("festival", festival));
            ulaznice = c.list();
            for (Ulaznica ulaznica : ulaznice) {
                korisnici.add(ulaznica.getKorisnik());
            }
            for (Korisnik korisnik : korisnici) {
                Notifikacija notifikacija = new Notifikacija();
                notifikacija.setKorisnik(korisnik);
                notifikacija.setTekst("Otkazan je festival " + festival.getNaziv() + "! :( Dodjite na blagajnu za povracaj novca ili otkazite rezervaciju!");
                notifikacije.add(notifikacija);
            }
            for (Notifikacija notifikacija : notifikacije) {
                session.save(notifikacija);
            }
            tx.commit();
        } catch (HibernateException e) {
            tx.rollback();
        }
        SemaphoreUtil.HIBERNATE_SEM.release();
    }
    
    public List<Festival> dohvatiSveFestivale() {
        try {
            SemaphoreUtil.HIBERNATE_SEM.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(FestivalHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        session = HibernateUtil.getSessionFactory().getCurrentSession();
        List<Festival> festivali = null;
        
        Transaction tx = session.beginTransaction();
        try {
            Criteria c = session.createCriteria(Festival.class);
            c.add(Restrictions.eq("otkazano", false));
            festivali = c.list();
            tx.commit();
        } catch (HibernateException e) {
            tx.rollback();
        }
        SemaphoreUtil.HIBERNATE_SEM.release();
        return festivali;
    }
    
    public void edit(Festival festival) {
        try {
            SemaphoreUtil.HIBERNATE_SEM.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(FestivalHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction tx = session.beginTransaction();
        try {
            session.saveOrUpdate(festival);
            for (Nastup nastup : festival.getNastups()) {
                session.saveOrUpdate(nastup);
            }
            for (Galerija galerija : festival.getGalerijas()) {
                session.saveOrUpdate(galerija);
            }
            tx.commit();
        } catch (HibernateException e) {
            tx.rollback();
        }
        SemaphoreUtil.HIBERNATE_SEM.release();
    }
    
    public int getPreostaloZaDan(Festival festival, Date date) {
        try {
            SemaphoreUtil.HIBERNATE_SEM.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(FestivalHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        int preostalo = festival.getKapacitetPoDanu();
        session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction tx = session.beginTransaction();
        try {
            Criteria c = session.createCriteria(Ulaznica.class);
            c.add(Restrictions.eq("festival", festival));
            c.add(Restrictions.or(Restrictions.eq("datumNastupa", date), Restrictions.isNull("datumNastupa")));
            c.add(Restrictions.eq("isteklo", false));
            List<Ulaznica> ulaznice = c.list();
            for (Ulaznica ulaznica : ulaznice) {
                preostalo -= ulaznica.getKolicina();
            }
            tx.commit();
        } catch (HibernateException e) {
            tx.rollback();
        }
        SemaphoreUtil.HIBERNATE_SEM.release();
        return preostalo;
    }
    
    private int getPreostaloZaDanNonTransaction(Festival festival, Date date, Session s) throws HibernateException
    {
        int preostalo = festival.getKapacitetPoDanu();
        Criteria c = session.createCriteria(Ulaznica.class);
        
        c.add(Restrictions.eq("festival", festival));
        c.add(Restrictions.or(Restrictions.eq("datumNastupa", date), Restrictions.isNull("datumNastupa")));
        c.add(Restrictions.eq("isteklo", false));
        List<Ulaznica> ulaznice = c.list();
        for (Ulaznica ulaznica : ulaznice) {
            preostalo -= ulaznica.getKolicina();
        }
        return preostalo;
    }
    
    //Neblokirajuca, jer poziva blokirajucu metodu getPreostaloZaDan
    public int getMinPreostalo(Festival festival) {
        int min = festival.getKapacitetPoDanu();
        Date currentDate = new Date(festival.getDatumOd().getTime());
        for (; currentDate.before(festival.getDatumDo()); currentDate.setTime(currentDate.getTime() + 86400000)) {
            int brojKarata = getPreostaloZaDan(festival, currentDate);
            if (min > brojKarata) {
                min = brojKarata;
            }
        }
        return min;
    }
    
    private int getMinPreostaloNonTransaction(Festival festival, Session session)
    {
        int min = festival.getKapacitetPoDanu();
        Date currentDate = new Date(festival.getDatumOd().getTime());
        for (; currentDate.before(festival.getDatumDo()); currentDate.setTime(currentDate.getTime() + 86400000)) {
            int brojKarata = getPreostaloZaDanNonTransaction(festival, currentDate, session);
            if (min > brojKarata) {
                min = brojKarata;
            }
        }
        return min;
    }
    
    public void dodajUlaznicu(Ulaznica ulaznica) {
        try {
            SemaphoreUtil.HIBERNATE_SEM.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(FestivalHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction tx = session.beginTransaction();
        try {
            if (ulaznica.getDatumNastupa() != null) {
                ulaznica.getFestival().setBrojKupljenih(ulaznica.getFestival().getBrojKupljenih() + ulaznica.getKolicina());
            } else {
                int brDana = (int)((ulaznica.getFestival().getDatumDo().getTime() - ulaznica.getFestival().getDatumOd().getTime()) / (1000*60*60*24));
                brDana++;
                ulaznica.getFestival().setBrojKupljenih(ulaznica.getFestival().getBrojKupljenih() + brDana*ulaznica.getKolicina());
            }
            session.saveOrUpdate(ulaznica.getFestival());
            session.save(ulaznica);
            tx.commit();
        } catch (HibernateException e) {
            tx.rollback();
        }
        SemaphoreUtil.HIBERNATE_SEM.release();
    }
    
    public void dodajFestival(Festival festival, List<Nastup> nastupi, List<Galerija> galerije) {
        try {
            SemaphoreUtil.HIBERNATE_SEM.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(FestivalHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction tx = session.beginTransaction();
        try {
            session.save(festival);
            for (Nastup nastup : nastupi) {
                nastup.setFestival(festival);
                session.save(nastup);
            }
            for (Galerija galerija : galerije) {
                galerija.setFestival(festival);
                session.save(galerija);
            }
            tx.commit();
        } catch (HibernateException e) {
            tx.rollback();
        }
        SemaphoreUtil.HIBERNATE_SEM.release();
    }

    public void posaljiNotifikacije(Festival festival) {
        try {
            SemaphoreUtil.HIBERNATE_SEM.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(FestivalHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction tx = session.beginTransaction();
        try {
            Set<Korisnik> korisnici = new HashSet<>();
            List<Notifikacija> notifikacije = new LinkedList<>();
            List<Ulaznica> ulaznice = null;
            Criteria c = session.createCriteria(Ulaznica.class);
            c.add(Restrictions.eq("festival", festival));
            ulaznice = c.list();
            for (Ulaznica ulaznica : ulaznice) {
                korisnici.add(ulaznica.getKorisnik());
            }
            for (Korisnik korisnik : korisnici) {
                Notifikacija notifikacija = new Notifikacija();
                notifikacija.setKorisnik(korisnik);
                notifikacija.setTekst("Izmenjen je festival " + festival.getNaziv() + "! Za vise informacija, posetite stranicu festivala!");
                notifikacije.add(notifikacija);
            }
            for (Notifikacija notifikacija : notifikacije) {
                session.save(notifikacija);
            }
            tx.commit();
        } catch (HibernateException e) {
            tx.rollback();
        }
        SemaphoreUtil.HIBERNATE_SEM.release();
    }

    public boolean rezervisiZaDan(Festival festival, Date dan, Korisnik kor, int kolicina) {
        boolean success = true;
        try {
            SemaphoreUtil.HIBERNATE_SEM.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(FestivalHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction tx = session.beginTransaction();
        try {
            //Proveri dal je korisnik banovan
            Criteria c = session.createCriteria(Korisnik.class);
            c.add(Restrictions.eq("idKor", kor.getIdKor()));
            kor = (Korisnik)c.uniqueResult();
            if(kor.getBrojUpozorenja() < 3)
            {
                //Proveri dal vec ima za ovaj fest istekle rezervacije
                c = session.createCriteria(Ulaznica.class);
                c.add(Restrictions.eq("korisnik", kor));
                c.add(Restrictions.eq("festival", festival));
                List<Ulaznica> ulaznice = c.list();
                int ulaznicaZaOvajDan = kolicina;
                for(Ulaznica ulaznica : ulaznice)
                {
                    if(ulaznica.getDatumNastupa() == null || ulaznica.getDatumNastupa().equals(dan))
                        ulaznicaZaOvajDan += ulaznica.getKolicina();
                    if(ulaznica.isIsteklo())
                    {
                        success = false;
                        break;
                    }
                }
                //Ako ima vazece rez, proveri dal ovom rezervacijom narusava pravilo max karata po danu
                if(ulaznicaZaOvajDan > festival.getMaxKarataPoKorisniku())
                    success = false;
                
                //Proveri da li ima karata za taj dan
                c = session.createCriteria(Ulaznica.class);
                c.add(Restrictions.eq("korisnik", kor));
                c.add(Restrictions.eq("festival", festival));
                ulaznicaZaOvajDan = kolicina;
                ulaznice = c.list();
                for(Ulaznica ulaznica : ulaznice)
                {
                    if(ulaznica.getDatumNastupa() == null || ulaznica.getDatumNastupa().equals(dan))
                        ulaznicaZaOvajDan += ulaznica.getKolicina();
                }
                if(ulaznicaZaOvajDan > festival.getKapacitetPoDanu())
                    success = false;
                
                if(success){
                    Ulaznica mojaRezervacija = new Ulaznica();
                    mojaRezervacija.setDatumNastupa(dan);
                    mojaRezervacija.setFestival(festival);
                    mojaRezervacija.setKolicina(kolicina);
                    mojaRezervacija.setKorisnik(kor);
                    mojaRezervacija.setVremeKreiranja(new Date());
                    session.save(mojaRezervacija);
                    
                }
            }
            else
                success = false;
            tx.commit();
        } catch (HibernateException e) {
            tx.rollback();
            success = false;
        }
        SemaphoreUtil.HIBERNATE_SEM.release();
        return success;
    }

    public boolean rezervisiPaket(Festival festival, Korisnik kor, int kolicina) {
        boolean success = true;
        try {
            SemaphoreUtil.HIBERNATE_SEM.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(FestivalHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction tx = session.beginTransaction();
        try {
            //Proveri dal je korisnik banovan
            Criteria c = session.createCriteria(Korisnik.class);
            c.add(Restrictions.eq("idKor", kor.getIdKor()));
            kor = (Korisnik)c.uniqueResult();
            if(kor.getBrojUpozorenja() < 3)
            {
                //Proveri dal vec ima za ovaj fest istekle rezervacije
                c = session.createCriteria(Ulaznica.class);
                c.add(Restrictions.eq("korisnik", kor));
                c.add(Restrictions.eq("festival", festival));
                List<Ulaznica> ulaznice = c.list();                
                for(Ulaznica ulaznica : ulaznice)
                {                    
                    if(ulaznica.isIsteklo())
                    {
                        success = false;
                        break;
                    }
                }
                //Ako ima vazece rez, proveri dal ovom rezervacijom narusava pravilo max karata po danu
                int maxUlaznicaZaDan = getMaxRezervisanihNonTransaction(festival, kor, session) + kolicina;
                if (maxUlaznicaZaDan > festival.getMaxKarataPoKorisniku())
                    success = false;
                
                //Proveri da li ima karata za dan za koji ima najmanje karata                                
                int preostaloUlaznica = getMinPreostaloNonTransaction(festival, session) - kolicina;
                if(preostaloUlaznica < 0)
                    success = false;
                
                if(success){
                    Ulaznica mojaRezervacija = new Ulaznica();
                    mojaRezervacija.setDatumNastupa(null);
                    mojaRezervacija.setFestival(festival);
                    mojaRezervacija.setKolicina(kolicina);
                    mojaRezervacija.setKorisnik(kor);
                    mojaRezervacija.setVremeKreiranja(new Date());
                    session.save(mojaRezervacija);
                }
            }
            else
                success = false;
            tx.commit();
        } catch (HibernateException e) {
            tx.rollback();
            success = false;
        }
        SemaphoreUtil.HIBERNATE_SEM.release();
        return success;
    }

    private int getMaxRezervisanihNonTransaction(Festival festival, Korisnik kor, Session session) {
        int max = 0;
        Date currentDate = new Date(festival.getDatumOd().getTime());
        for (; currentDate.before(festival.getDatumDo()); currentDate.setTime(currentDate.getTime() + 86400000)) {
            int brojKarata = getRezervisanihZaDanNonTransaction(festival, kor, currentDate, session);
            if (max < brojKarata) {
                max = brojKarata;
            }
        }
        return max;
    }
    
    private int getRezervisanihZaDanNonTransaction(Festival festival, Korisnik kor, Date dan, Session session) throws HibernateException
    {
        Criteria c = session.createCriteria(Ulaznica.class);
        c.add(Restrictions.eq("korisnik", kor));
        c.add(Restrictions.eq("festival", festival));
        int ulaznicaZaOvajDan = 0;
        List<Ulaznica> ulaznice = c.list();
        for(Ulaznica ulaznica : ulaznice)
        {
            if(ulaznica.getDatumNastupa() == null || ulaznica.getDatumNastupa().equals(dan))
                ulaznicaZaOvajDan += ulaznica.getKolicina();
        }
        return ulaznicaZaOvajDan;
    }
    
    public static double getProsecnaOcenaZaFestival(Festival festival) {
        try {
            SemaphoreUtil.HIBERNATE_SEM.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(FestivalHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        double ret = 0;
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction tx = session.beginTransaction();
        List<Ocena> ocene = new LinkedList<>();
        try {
            Criteria c = session.createCriteria(Ocena.class);
            c.add(Restrictions.eq("imeFestivala", festival.getNaziv()));
            ocene = c.list();
            double sum = 0.0;
            for (Ocena ocena : ocene) {
                sum += ocena.getOcena();
            }
            ret = ocene.size() > 0 ? sum/ocene.size() : 0;
            tx.commit();
        } catch (HibernateException e) {
            tx.rollback();
        }
        SemaphoreUtil.HIBERNATE_SEM.release();
        return ret;
    }
    
    private static boolean containsName(List<Festival> festivali, Festival festival) {
        boolean contains = false;
        for (Festival f : festivali) {
            if (f.getNaziv().equals(festival.getNaziv())) {
                contains = true;
            }
        }
        return contains;
    }

    public Collection<Festival> getNajboljeOcenjeniNonTransaction() {
        List<Festival> svi = dohvatiSveFestivale();
        Collections.sort(svi, new OcenaComparator());
        List<Festival> najbolji = new LinkedList<>();
        boolean gotov = false;
        while (!gotov && najbolji.size() < 5) {
            if (svi.isEmpty()) {
                gotov = true;
            } else {
                Festival festival = svi.remove(0);
                if (!containsName(svi, festival)) {
                    najbolji.add(festival);
                }
            }
        }
        return najbolji;
    }

    public void dodajSliku(Galerija galerija) {
        try {
            SemaphoreUtil.HIBERNATE_SEM.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(FestivalHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction tx = session.beginTransaction();
        try {
            session.save(galerija);
            tx.commit();
        } catch (HibernateException e) {
            tx.rollback();
        }
        SemaphoreUtil.HIBERNATE_SEM.release();
    }

    public List<Ocena> getOceneZaFestival(Festival festival) {
        try {
            SemaphoreUtil.HIBERNATE_SEM.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(FestivalHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        List<Ocena> ocene = new LinkedList<>();
        session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction tx = session.beginTransaction();
        try {
            Criteria c = session.createCriteria(Ocena.class);
            c.add(Restrictions.eq("imeFestivala", festival.getNaziv()));
            ocene = c.list();
            tx.commit();
        } catch (HibernateException e) {
            tx.rollback();
        }
        SemaphoreUtil.HIBERNATE_SEM.release();
        return ocene;
    }

    public void dodajOcenu(Festival festival, int ocena, String komentar, Korisnik korisnik) {
        try {
            SemaphoreUtil.HIBERNATE_SEM.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(FestivalHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction tx = session.beginTransaction();
        try {
            Ocena o = new Ocena();
            o.setFestival(festival);
            o.setImeFestivala(festival.getNaziv());
            o.setKomentar(komentar);
            o.setOcena(ocena);
            o.setKorisnik(korisnik);
            session.save(o);
            tx.commit();
        } catch (HibernateException e) {
            tx.rollback();
        }
        SemaphoreUtil.HIBERNATE_SEM.release();
    }

    public void inkrementirajPreglede(Festival festival) {
        try {
            SemaphoreUtil.HIBERNATE_SEM.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(FestivalHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction tx = session.beginTransaction();
        try {
            festival.setPregledi(festival.getPregledi() + 1);
            session.update(festival);
            tx.commit();
        } catch (HibernateException e) {
            tx.rollback();
        }
        SemaphoreUtil.HIBERNATE_SEM.release();
    }

    public void dodajVideo(Galerija galerija) {
        try {
            SemaphoreUtil.HIBERNATE_SEM.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(FestivalHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction tx = session.beginTransaction();
        try {
            session.save(galerija);
            tx.commit();
        } catch (HibernateException e) {
            tx.rollback();
        }
        SemaphoreUtil.HIBERNATE_SEM.release();
    }
}
