package beans;

import db.Festival;
import db.Galerija;
import db.Korisnik;
import db.Ocena;
import db.Ulaznica;
import db.helpers.FestivalHelper;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

@Named("detailsBean")
@SessionScoped
public class DetailsBean implements Serializable {

    private static final FestivalHelper HELPER = new FestivalHelper();
    
    private Festival festival;
    private List<Galerija> slike;
    private List<Galerija> videi;
    
    private String komentar;
    private int ocena;
    private String videoURL;
    
    @Inject
    LoginBean loginBean;

    public String getVideoURL() {
        return videoURL;
    }

    public void setVideoURL(String videoURL) {
        this.videoURL = videoURL;
    }

    public String getKomentar() {
        return komentar;
    }

    public void setKomentar(String komentar) {
        this.komentar = komentar;
    }

    public int getOcena() {
        return ocena;
    }

    public void setOcena(int ocena) {
        this.ocena = ocena;
    }
    
    public String prikaziDetalje(Festival festival) {
        this.festival = festival;
        slike = new LinkedList<>();
        videi = new LinkedList<>();
        for (Galerija galerija : festival.getGalerijas()) {
            if (galerija.getTip() == 1) {
                slike.add(galerija);
            } else {
                videi.add(galerija);
            }
        }
        HELPER.inkrementirajPreglede(festival);
        return "/user/detalji";
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
    
    public void setFestival(Festival festival) {
        this.festival = festival;
    }
    
    public Festival getFestival() {
        return this.festival;
    }
    
    public List<Ocena> oceneZaFestival() {
        return HELPER.getOceneZaFestival(festival);
    }
    
    private boolean prosao(Festival festival) {
        return new Date().after(festival.getDatumDo());
    }
    
    private boolean kupioUlaznicu(Korisnik korisnik, Festival festival) {
        boolean kupio = false;
        for (Ulaznica ulaznica : festival.getUlaznicas()) {
            if (ulaznica.getKorisnik().getIdKor().equals(korisnik.getIdKor()) && ulaznica.isProdato()) {
                kupio = true;
                break;
            }
        }
        return kupio;
    }
    
    public boolean canRate() {
        return kupioUlaznicu(loginBean.getKorisnik(), festival) && prosao(festival);
    }
    
    public void dodajOcenu() {
        HELPER.dodajOcenu(festival, ocena, komentar, loginBean.getKorisnik());
        ocena = 0;
        komentar = "";
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
            HELPER.dodajSliku(galerija);
            context.addMessage("messages", new FacesMessage(FacesMessage.SEVERITY_INFO, "Uspesan upload!", file.getFileName()));
            
        } catch (IOException ex) {
            context.addMessage("messages", new FacesMessage(FacesMessage.SEVERITY_FATAL, "Neuspesan upload!", file.getFileName()));
            Logger.getLogger(FestivalBean.class.getName()).log(Level.SEVERE, null, ex);
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
            HELPER.dodajVideo(galerija);
            context.addMessage("messages", new FacesMessage("Uspesno dodavanje snimka!"));
        } else {
            context.addMessage("messages", new FacesMessage("Neuspesno dodavanje snimka!"));
        }
    }
    
}
