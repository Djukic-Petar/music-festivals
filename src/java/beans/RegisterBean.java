package beans;

import db.Korisnik;
import db.helpers.KorisnikHelper;
import java.util.Date;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import validation.Password;
import validation.Username;

@RequestScoped
@Named("registerBean")
public class RegisterBean {
    
    private static final KorisnikHelper HELPER = new KorisnikHelper();
    
    private String ime;
    private String prezime;
    @Username
    private String username;
    @Password
    private String password;
    private String email;
    private String telefon;
    
    private String msg;
    
    @Inject
    private NavBean navbean;
    
    public String register() {
        FacesContext context = FacesContext.getCurrentInstance();
        Korisnik k = new Korisnik(ime, prezime, username, password, telefon, email, false, false, new Date(), 0);
        if (HELPER.addKorisnik(k)) {
            msg = "Uspesna registracija!";
            context.addMessage("message", new FacesMessage("Nalog uspesno napravljen!", msg));
            navbean.setActivePage("content");
            return "/index";
        } else {
            msg = "Nalog sa korisnickim imenom ili emailom vec postoji!";
            context.addMessage("msg", new FacesMessage("Greska u registraciji!", msg));
            return "";
        }
    }

    public String getIme() {
        return ime;
    }

    public void setIme(String ime) {
        this.ime = ime;
    }

    public String getPrezime() {
        return prezime;
    }

    public void setPrezime(String prezime) {
        this.prezime = prezime;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefon() {
        return telefon;
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }
    
}
