package beans;

import db.Festival;
import db.Galerija;
import db.Nastup;
import db.helpers.FestivalHelper;
import services.DateHelper;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

@Named(value = "editBean")
@SessionScoped
public class EditBean implements Serializable {

    private static final FestivalHelper HELPER = new FestivalHelper();
    
    private boolean basic = true;
    private boolean edited = false;
    
    private Festival festival;
    private Set<Nastup> nastupi;
    private List<Galerija> slike = new LinkedList<>();
    private List<Galerija> videi = new LinkedList<>();
    private Nastup nastup;
    private DateHelper dateHelper;
    
    private Map<Long, DateHelper> dates = new HashMap<>();
    
    private String videoURL;

    public Map<Long, DateHelper> getDates() {
        return dates;
    }

    public void setDates(Map<Long, DateHelper> dates) {
        this.dates = dates;
    }

    public DateHelper getDateHelper() {
        return dateHelper;
    }

    public void setDateHelper(DateHelper dateHelper) {
        this.dateHelper = dateHelper;
    }

    public String getVideoURL() {
        return videoURL;
    }

    public void setVideoURL(String videoURL) {
        this.videoURL = videoURL;
    }

    public Nastup getNastup() {
        return nastup;
    }

    public void setNastup(Nastup nastup) {
        this.nastup = nastup;
    }

    public boolean isBasic() {
        return basic;
    }

    public void setBasic(boolean basic) {
        this.basic = basic;
    }

    public Festival getFestival() {
        return festival;
    }

    public void setFestival(Festival festival) {
        this.festival = festival;
    }

    public Set<Nastup> getNastupi() {
        return nastupi;
    }

    public void setNastupi(Set<Nastup> nastupi) {
        this.nastupi = nastupi;
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
    
    public String prikaziEdit(Festival festival) {
        slike = new LinkedList<>();
        videi = new LinkedList<>();
        videoURL = "";
        basic = true;
        edited = false;
        nastup = null;
        this.festival = festival;
        nastupi = festival.getNastups();
        for (Nastup nastup : nastupi) {
            dates.put(nastup.getIdNas(), new DateHelper(nastup));
        }
        return "/admin/izmeniFestival";
    }
    
    public void izmeniNastup(Nastup nastup) {
        this.nastup = new Nastup(nastup.getFestival(), nastup.getIzvodjac(), nastup.getDatumOd(), nastup.getDatumDo(), nastup.getVremeOd(), nastup.getVremeDo(), false);
        this.nastup.setIdNas(nastup.getIdNas());
        this.dateHelper = dates.get(nastup.getIdNas());
        this.basic = false;
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
    
    public String sacuvaj() {
        festival.getGalerijas().addAll(slike);
        festival.getGalerijas().addAll(videi);
        HELPER.edit(festival);
        if (edited) {
            HELPER.posaljiNotifikacije(festival);
        }
        return "/admin/homeAdmin";
    }
    
    public String odustani() {
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("editBean");
        return "/admin/homeAdmin";
    }
    
    public void sacuvajNastup() {
        this.basic = true;
        this.edited = true;
        Nastup oldNastup = null;
        for (Nastup nas : nastupi) {
            if (nas.getIdNas().equals(nastup.getIdNas())) {
                oldNastup = nas; break;
            }
        }
        oldNastup.setIzvodjac(nastup.getIzvodjac());
        oldNastup.setDatumOd(nastup.getDatumOd());
        oldNastup.setDatumDo(nastup.getDatumDo());
        oldNastup.setVremeOd(nastup.getVremeOd());
        oldNastup.setVremeDo(nastup.getVremeDo());
        dates.remove(nastup.getIdNas());
        dates.put(nastup.getIdNas(), new DateHelper(nastup));
    }
    
    public void odustaniNastup() {
        this.basic = true;
    }
}
