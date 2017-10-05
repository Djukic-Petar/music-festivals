package beans;

import db.Festival;
import db.Korisnik;
import db.Notifikacija;
import db.helpers.FestivalHelper;
import db.helpers.KorisnikHelper;
import java.io.IOException;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

@Named(value = "loginBean")
@SessionScoped
public class LoginBean implements Serializable {
    private static final KorisnikHelper HELPER = new KorisnikHelper();
    private static final FestivalHelper FESTIVAL_HELPER = new FestivalHelper();
    private String username;
    private String password;
    private String msg;
    private Korisnik korisnik;
    
    @Inject
    private SearchBean searchBean;
    
    private List<Festival> najvisePregleda = null;
    private List<Festival> najviseKarata = null;
    
    private List<Notifikacija> noveNotifikacije = new LinkedList<>();
    
    private boolean unregistered = true;
    
    @Inject
    private NavBean navbean;

    public LoginBean(){
    }
    
    public String showLogin()
    {
        return "/login";
    }

    public List<Notifikacija> getNoveNotifikacije() {
        return noveNotifikacije;
    }

    public void setNoveNotifikacije(List<Notifikacija> noveNotifikacije) {
        this.noveNotifikacije = noveNotifikacije;
    }
    
    public boolean isAdmin() {
        return this.korisnik != null && this.korisnik.isAdmin();
    }
    
    public String login() {
        FacesContext context = FacesContext.getCurrentInstance();
        korisnik = HELPER.getKorisnik(username, password);
        if (korisnik == null) {
            msg = "Uneli ste neispravno korisnicko ime i/ili lozinku. Pokusajte ponovo.";
            context.addMessage("message", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Neuspesna prijava!", msg));
        } else {
            if (korisnik.getBrojUpozorenja() >= 3) {
                msg = "Vas nalog je suspendovan.";
                context.addMessage("message", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Neuspesna prijava!", msg));
                return "";
            }
            if (!korisnik.isAdmin()){
                if (korisnik.isOdobren()) {
                    msg = "";
                    HELPER.updateKorisnikTimeStamp(korisnik);
                    navbean.setActivePage("homeKorisnik");
                    unregistered = false;
                    for (Notifikacija notifikacija : korisnik.getNotifikacijas()) {
                        if (!notifikacija.isPregledano()) {
                            noveNotifikacije.add(notifikacija);
                        }
                    }
                    searchBean.setFestivali(FESTIVAL_HELPER.getPetNajskorijih());
                    HELPER.seenujNotifikacije(korisnik);
                } else {
                    msg = "Vas nalog nije odobren od strane administratora.";
                    context.addMessage("message", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Neuspesna prijava", msg));
                }
            } else {
                msg = "";
                HELPER.updateKorisnikTimeStamp(korisnik);
                navbean.setActivePage("homeAdmin");
                najvisePregleda = FESTIVAL_HELPER.getPetNajvisePregleda();
                najviseKarata = FESTIVAL_HELPER.getPetNajviseUlaznica();
                unregistered = false;
            }
        }
        return "/index";
    }

    public List<Festival> getNajvisePregleda() {
        return najvisePregleda;
    }

    public void setNajvisePregleda(List<Festival> najvisePregleda) {
        this.najvisePregleda = najvisePregleda;
    }

    public List<Festival> getNajviseKarata() {
        return najviseKarata;
    }

    public void setNajviseKarata(List<Festival> najviseKarata) {
        this.najviseKarata = najviseKarata;
    }
    
    public void logout() throws IOException {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        navbean.setActivePage("content");
        ec.redirect("/ProjekatPIA/index.xhtml");
    }
    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMsg() {
        return msg;
    }

    public Korisnik getKorisnik() {
        return korisnik;
    }

    public boolean isUnregistered() {
        return unregistered;
    }
    
    public boolean isRegularUser()
    {
        return this.korisnik != null && !this.korisnik.isAdmin();
    }
}
