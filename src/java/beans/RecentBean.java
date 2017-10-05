package beans;

import db.Korisnik;
import db.helpers.KorisnikHelper;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

@Named("recentBean")
@RequestScoped
public class RecentBean {
    private static final KorisnikHelper HELPER = new KorisnikHelper();
    private List<Korisnik> korisnici = new LinkedList<>();
    
    @PostConstruct
    public void init() {
        korisnici = HELPER.getRecent();
        
    }
    
    public List<Korisnik> getKorisnici() {
        return korisnici;
    }
}
