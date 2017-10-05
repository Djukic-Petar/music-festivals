package beans;

import db.Festival;
import db.Korisnik;
import db.Ulaznica;
import db.helpers.FestivalHelper;
import db.helpers.UlaznicaHelper;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

@Named(value = "reservationBean")
@SessionScoped
public class ReservationBean implements Serializable {

    public static final FestivalHelper FESTIVAL_HELPER = new FestivalHelper();
    public static final UlaznicaHelper ULAZNICA_HELPER = new UlaznicaHelper();
    
    private Festival festival;
    private Date dan;
    private int kolicina;
    private boolean paket;
    
    private List<Ulaznica> ulaznice;
    
    @Inject 
    LoginBean loginBean;
    
    @Inject
    DetailsBean detailsBean;

    public boolean canCancel(Ulaznica ulaznica) {
        return !ulaznica.isIsteklo() && !ulaznica.isProdato() && !prosaoNastup(ulaznica);
    }
    
    public List<Ulaznica> getUlaznicas() {
        return ULAZNICA_HELPER.getUlazniceForUser(loginBean.getKorisnik());
    }
    
    public boolean prosaoNastup(Ulaznica ulaznica) {
        if (ulaznica.getDatumNastupa() != null) {
            return new Date().after(ulaznica.getDatumNastupa());
        } else {
            return new Date().after(ulaznica.getFestival().getDatumDo());
        }
    }
    
    public String getColor(Ulaznica ulaznica) {
        return prosaoNastup(ulaznica) ? "red" : "green";
    }
    
    public void otkazi(Ulaznica ulaznica) {
        ULAZNICA_HELPER.otkazi(ulaznica);
    }
    
    public List<Ulaznica> getUlaznice() {
        return ulaznice;
    }

    public void setUlaznice(List<Ulaznica> ulaznice) {
        this.ulaznice = ulaznice;
    }

    public Festival getFestival() {
        return festival;
    }

    public void setFestival(Festival festival) {
        this.festival = festival;
    }

    public Date getDan() {
        return dan;
    }

    public void setDan(Date dan) {
        this.dan = dan;
    }

    public int getKolicina() {
        return kolicina;
    }

    public void setKolicina(int kolicina) {
        this.kolicina = kolicina;
    }

    public boolean isPaket() {
        return paket;
    }

    public void setPaket(boolean paket) {
        this.paket = paket;
    }
    
    public String prikaziSveRezervacije() {
        return "/user/rezervacije";
    }
    
    public boolean mozeRezervacija(Festival festival) {
        int ukupanBroj = 0;
        int brDana = (int)((festival.getDatumDo().getTime() - festival.getDatumOd().getTime()) / (1000*60*60*24));
        brDana++;
        for (Ulaznica ulaznica : festival.getUlaznicas()) {
            if (!ulaznica.isIsteklo()) {
                if (ulaznica.getDatumNastupa() == null) {
                    ukupanBroj += brDana * ulaznica.getKolicina();
                } else {
                    ukupanBroj += ulaznica.getKolicina();
                }
            }
        }
        return ukupanBroj < festival.getKapacitetPoDanu() * brDana && festival.getDatumOd().after(new Date());
    }
    
    public String prikaziRezervaciju(Festival festival) {
        this.festival = festival;
        this.dan = null;
        this.kolicina = 0;
        this.paket = false;
        return "/user/rezervacija";
    }
    
    public String odustani() {
        this.dan = null;
        this.kolicina = 0;
        this.paket = false;
        return "/user/homeKorisnik";
    }
    
    public String rezervisi()
    {
        Korisnik kor = loginBean.getKorisnik();
        if (kor != null)
        {
            boolean success;
            if (paket)
            {
                success = FESTIVAL_HELPER.rezervisiPaket(festival, kor, kolicina);
            }
            else
            {
                if (dan != null) {
                    success = FESTIVAL_HELPER.rezervisiZaDan(festival, dan, kor, kolicina);
                } else {
                    success = false;
                }
            }
            if (success)
            {
                FacesContext.getCurrentInstance().addMessage("message", new FacesMessage(FacesMessage.SEVERITY_INFO, "Rezervacija uspela!", "Uspesno ste rezervisali karte za zeljeni festival."));
                detailsBean.setFestival(festival);
                return "/user/detalji";
            }
            else
            {
                FacesContext.getCurrentInstance().addMessage("message", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Rezervacija nije uspela!", "Dogodila se greska prilikom rezervacije."));
                return "";
            }
        }
        FacesContext.getCurrentInstance().addMessage("message", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Rezervacija nije uspela!", "Morate da budete ulogovani da biste rezervisali karte."));     
        return "";
    }
}
