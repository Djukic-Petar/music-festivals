package beans;

import com.corejsf.hibernate.HibernateUtil;
import db.Korisnik;
import db.helpers.KorisnikHelper;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

@Named("unapprovedBean")
@RequestScoped
public class UnapprovedBean {
    private static final KorisnikHelper HELPER = new KorisnikHelper();
    private List<Korisnik> korisnici = new LinkedList<>();
    
    @PostConstruct
    public void init() {
        korisnici = HELPER.getNeodobreniKorisnici(); 
    }
    
    public List<Korisnik> getKorisnici() {
        return korisnici;
    }
    
    public void approve(String username) {
        Korisnik korisnik = HELPER.getKorisnik(username);
        korisnik.setOdobren(true);
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction tx = session.beginTransaction();
        try {
            session.update(korisnik);
            tx.commit();
        } catch (HibernateException e) {
            tx.rollback();
        }
        korisnici = HELPER.getNeodobreniKorisnici();
    }
}
