package beans;

import db.Festival;
import db.Ulaznica;
import db.helpers.FestivalHelper;
import db.helpers.KorisnikHelper;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.Date;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

@Named(value = "saleBean")
@SessionScoped
public class SaleBean implements Serializable {
    
    private static final FestivalHelper FESTIVAL_HELPER = new FestivalHelper();
    private static final KorisnikHelper KORISNIK_HELPER = new KorisnikHelper();
    
    private Festival festival;
    private boolean paket;
    private Date datum;
    private int kolicina;

    public Date getDatum() {
        return datum;
    }

    public void setDatum(Date datum) {
        this.datum = datum;
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

    public Festival getFestival() {
        return festival;
    }

    public void setFestival(Festival festival) {
        this.festival = festival;
    }
    
    public String prikaziProdaju(Festival festival) {
        this.festival = festival;
        paket = false;
        datum = null;
        kolicina = 0;
        return "/admin/prodaja";
    }
    
    public String prodaj() {
        if (kolicina <= 0 || kolicina > festival.getMaxKarataPoKorisniku()) {
            FacesContext.getCurrentInstance().addMessage("messages", new FacesMessage("Nevalidan broj karata!"));
            return "";
        }
        if (paket) {
            if (kolicina > FESTIVAL_HELPER.getMinPreostalo(festival)) {
                FacesContext.getCurrentInstance().addMessage("messages", new FacesMessage("Nevalidan broj karata!"));
                return "";
            }
            Ulaznica ulaznica = new Ulaznica();
            ulaznica.setFestival(festival);
            ulaznica.setKolicina(kolicina);
            ulaznica.setKorisnik(KORISNIK_HELPER.getKorisnik("neregistrovani"));
            ulaznica.setProdato(true);
            FESTIVAL_HELPER.dodajUlaznicu(ulaznica);
            FacesContext.getCurrentInstance().addMessage("messages", new FacesMessage("Uspesno prodate karte!"));
            paket = false;
            datum = null;
            kolicina = 0;
            return "";
        } else {
            if (kolicina > FESTIVAL_HELPER.getPreostaloZaDan(festival, datum)) {
                FacesContext.getCurrentInstance().addMessage("messages", new FacesMessage("Nevalidan broj karata!"));
                return "";
            }
            Ulaznica ulaznica = new Ulaznica();
            ulaznica.setFestival(festival);
            ulaznica.setKolicina(kolicina);
            ulaznica.setDatumNastupa(datum);
            ulaznica.setKorisnik(KORISNIK_HELPER.getKorisnik("neregistrovani"));
            ulaznica.setProdato(true);
            paket = false;
            datum = null;
            kolicina = 0;
            FESTIVAL_HELPER.dodajUlaznicu(ulaznica);
            FacesContext.getCurrentInstance().addMessage("messages", new FacesMessage("Uspesno prodate karte!"));
            paket = false;
            datum = null;
            kolicina = 0;
            return "";
        }
    }
}
