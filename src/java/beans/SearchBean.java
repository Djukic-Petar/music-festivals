package beans;

import db.Festival;
import db.helpers.FestivalHelper;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

@Named("searchBean")
@ViewScoped
public class SearchBean implements Serializable {
    
    private String naziv;
    private String mesto;
    private Date datumOd;
    private Date datumDo;
    private String izvodjac;
    
    @Inject
    private LoginBean loginBean;
    
    private boolean showOcena = false;
    
    private static final FestivalHelper HELPER = new FestivalHelper();
    
    private Collection<Festival> festivali;

    @PostConstruct
    public void init() {
        if (loginBean.isRegularUser()) {
            this.festivali = HELPER.getPetNajskorijih();
        } else {
            this.festivali = HELPER.dohvatiSveFestivale();
        }
    }
    
    public String getIzvodjac() {
        return izvodjac;
    }

    public void setIzvodjac(String izvodjac) {
        this.izvodjac = izvodjac;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public String getMesto() {
        return mesto;
    }

    public void setMesto(String mesto) {
        this.mesto = mesto;
    }

    public Date getDatumOd() {
        return datumOd;
    }

    public void setDatumOd(Date datumOd) {
        this.datumOd = datumOd;
    }

    public Date getDatumDo() {
        return datumDo;
    }

    public void setDatumDo(Date datumDo) {
        this.datumDo = datumDo;
    }

    public Collection<Festival> getFestivali() {
        return festivali;
    }

    public void setFestivali(Collection<Festival> festivali) {
        this.festivali = festivali;
    }
    
    public void search() {
        showOcena = false;
        this.festivali = HELPER.getFestivals(naziv, mesto, izvodjac, datumOd, datumDo);
    }
    
    public void petNajboljeOcenjenih() {
        showOcena = true;
        this.festivali = HELPER.getNajboljeOcenjeniNonTransaction();
    }

    public boolean isShowOcena() {
        return showOcena;
    }

    public void setShowOcena(boolean showOcena) {
        this.showOcena = showOcena;
    }
    
    public double dohvatiOcenu(Festival festival) {
        return FestivalHelper.getProsecnaOcenaZaFestival(festival);
    }
}
