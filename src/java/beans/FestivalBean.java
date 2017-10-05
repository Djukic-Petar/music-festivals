package beans;

import db.Festival;
import db.Galerija;
import db.Nastup;
import db.helpers.FestivalHelper;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

@Named(value = "festivalBean")
@ViewScoped
public class FestivalBean implements Serializable {
    
    public static final FestivalHelper HELPER = new FestivalHelper();
    
    private Festival festival;
    private Nastup nastup;
    private List<Nastup> nastupi;
    private List<Galerija> galerije;
    private int step = 0;
    private Date vremePocetka;
    private Date vremeKraja;
    private Date datumPocetka;
    private Date datumKraja;
    
    private String videoURL = "";
    
    List<Galerija> slike = new ArrayList<>();
    List<Galerija> videi = new ArrayList<>();
    
    private static int i = 0;
    
    @Inject
    private NavBean navbean;
    
    @PostConstruct
    public void init() {
        System.out.println("POZVAN POST CONSTRUCT " + i++ + ". PUT!");
        festival = new Festival();
        nastup = new Nastup();
        nastupi = new LinkedList<>();
        galerije = new LinkedList<>();
    }

    public String getVideoURL() {
        return videoURL;
    }

    public void setVideoURL(String videoURL) {
        this.videoURL = videoURL;
    }

    public List<Galerija> getSlike() {
        return slike;
    }

    public void setSlike(List<Galerija> slike) {
        this.slike = slike;
    }

    public List<Galerija> getVidei() {
        return videi;
    }

    public void setVidei(List<Galerija> videi) {
        this.videi = videi;
    }

    public List<Galerija> getGalerije() {
        return galerije;
    }

    public void setGalerije(List<Galerija> galerije) {
        this.galerije = galerije;
    }

    public List<Nastup> getNastupi() {
        return nastupi;
    }

    public void setNastupi(List<Nastup> nastupi) {
        this.nastupi = nastupi;
    }
    
    public void rezervisi(Festival festival)
    {
        
    }
    
    public void uploadSlike(FileUploadEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
        UploadedFile file = event.getFile();
        String path = "C:/Temp/Img/" + festival.getNaziv();
        Path tempfile;
        
        try (InputStream istream = file.getInputstream()) {
            
            Path folder;
            
            folder = Paths.get(path);
            if (!folder.toFile().exists()) {
                folder.toFile().mkdir();
            }
            
            String filename = file.getFileName();
            String[] split = filename.split("\\.");
            String extension = "." + split[split.length-1];
            filename = filename.subSequence(0, filename.lastIndexOf('.')).toString();
            tempfile = Files.createTempFile(folder, filename, extension, new FileAttribute<?>[]{});
            
            Files.copy(istream, tempfile, StandardCopyOption.REPLACE_EXISTING);
            
            Galerija galerija = new Galerija();
            galerija.setFestival(festival);
            galerija.setPutanja(tempfile.toAbsolutePath().toString());
            galerija.setTip(1);
            
            slike.add(galerija);
            
            context.addMessage("messages", new FacesMessage(FacesMessage.SEVERITY_INFO, "Uspesan upload!", file.getFileName()));
            
        } catch (IOException ex) {
            context.addMessage("messages", new FacesMessage(FacesMessage.SEVERITY_FATAL, "Neuspesan upload!", file.getFileName()));
            Logger.getLogger(FestivalBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void dodajNastup() {
        boolean greska = false;
        String msg = "Uspesno dodat nastup!";
        FacesContext context = FacesContext.getCurrentInstance();
        if (nastup.getIzvodjac() == null || "".equals(nastup.getIzvodjac())) {
            msg = "Morate da unesete izvodjaca!";
            greska = true;
        } else {
            if (nastup.getDatumOd() == null) {
                msg = "Morate da unesete datum pocetka!";
                greska = true;
            } else {
                if (nastup.getDatumDo() == null) {
                    msg = "Morate da unesete datum zavrsetka!";
                    greska = true;
                } else {
                    if (nastup.getVremeOd() == null) {
                        msg = "Morate da unesete vreme pocetka!";
                        greska = true;
                    } else {
                        if (nastup.getVremeDo() == null) {
                            msg = "Morate da unesete vreme zavrsetka!";
                            greska = true;
                        }
                    }
                }
            }
        }
        if (greska) {
            nastup.setDatumDo(null);
            nastup.setDatumOd(null);
            nastup.setVremeOd(null);
            nastup.setVremeDo(null);
            context.addMessage("message", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Neuspesno dodavanje nastupa!", msg));
        } else {
            nastupi.add(nastup);
            nastup = new Nastup();
            context.addMessage("message", new FacesMessage(FacesMessage.SEVERITY_INFO, "Sve je u redu!", msg));
        }
    }
    
    public void uploadVidea() {
        FacesContext context = FacesContext.getCurrentInstance();
        String videoUrlFormatted = "https://www.youtube.com/v/";
        String videoUrlLowerCase = videoURL.toLowerCase();
        Pattern regVariant1 = Pattern.compile("(http(s)?://)?(www\\.)?youtube.com/watch\\?v\\=[A-Za-z0-9\\-\\_]+");
        Pattern regVariant2 = Pattern.compile("(http(s)?://)?(www\\.)?youtu.be/[.]+");
        
        boolean matchFound = false;
        
        if (regVariant1.matcher(videoUrlLowerCase).matches()) {
            videoUrlFormatted = videoUrlFormatted.concat(videoURL.substring(videoURL.lastIndexOf('=')+1, videoURL.length()));
            matchFound = true;
        }
        
        if (!matchFound && regVariant2.matcher(videoUrlLowerCase).matches()) {
            videoUrlFormatted = videoUrlFormatted.concat(videoURL.substring(videoURL.lastIndexOf('/')+1, videoURL.length()));
            matchFound = true;
        }
        
        if (matchFound) {
            Galerija galerija = new Galerija();
            galerija.setFestival(festival);
            galerija.setPutanja(videoUrlFormatted);
            galerija.setTip(2);
            videi.add(galerija);
            context.addMessage("messages", new FacesMessage("Uspesno dodavanje snimka!"));
        } else {
            context.addMessage("messages", new FacesMessage("Neuspesno dodavanje snimka!"));
        }
    }
    
    public void predjiNaMedije() {
        step++;
    }
    
    public void sacuvajOsnovneInformacije() {
        step++;
    }
    
    public String sacuvajFestival() {
        FacesContext context = FacesContext.getCurrentInstance();
        galerije.addAll(slike);
        galerije.addAll(videi);
        HELPER.dodajFestival(festival, nastupi, galerije);
        context.addMessage("messages", new FacesMessage("Uspesno dodat festival!"));
        return "/admin/homeAdmin";
    }

    public Nastup getNastup() {
        return nastup;
    }

    public void setNastup(Nastup nastup) {
        this.nastup = nastup;
    }

    public Festival getFestival() {
        return festival;
    }

    public void setFestival(Festival festival) {
        this.festival = festival;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public Date getVremePocetka() {
        return vremePocetka;
    }

    public void setVremePocetka(Date vremePocetka) {
        this.vremePocetka = vremePocetka;
    }

    public Date getVremeKraja() {
        return vremeKraja;
    }

    public void setVremeKraja(Date vremeKraja) {
        this.vremeKraja = vremeKraja;
    }

    public Date getDatumPocetka() {
        return datumPocetka;
    }

    public void setDatumPocetka(Date datumPocetka) {
        this.datumPocetka = datumPocetka;
    }

    public Date getDatumKraja() {
        return datumKraja;
    }

    public void setDatumKraja(Date datumKraja) {
        this.datumKraja = datumKraja;
    }
    
}
