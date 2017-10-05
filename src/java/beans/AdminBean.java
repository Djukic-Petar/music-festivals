package beans;

import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

@Named("adminBean")
@SessionScoped
public class AdminBean implements Serializable {
    
    @Inject
    private NavBean navbean;
    
    public String prikaziNajskorijeUlogovane() {
        navbean.setActivePage("najskorijeUlogovani");
        return "/admin/najskorijeUlogovani";
    }
    
    public String prikaziNeodobreneKorisnike() {
        navbean.setActivePage("neodobreniKorisnici");
        return "/admin/neodobreniKorisnici";
    }
    
    public String prikaziDodavanjeFestivala() {
        navbean.setActivePage("dodajFestival");
        return "/admin/dodajFestival";
    }
    
    public String prikaziImportCSV() {
        navbean.setActivePage("dodajFestival");
        return "/admin/dodajCSV";
    }
    
    public String prikaziImportJSON() {
        navbean.setActivePage("dodajFestival");
        return "/admin/dodajJSON";
    }
}
