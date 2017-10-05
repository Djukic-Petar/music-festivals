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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.json.JSONArray;
import org.primefaces.json.JSONObject;
import org.primefaces.model.UploadedFile;
import services.DateHelper;

@Named(value = "importJSONBean")
@ViewScoped
public class ImportJSONBean implements Serializable {

    private static final FestivalHelper HELPER = new FestivalHelper();
    
    private Festival festival = new Festival();
    private List<Nastup> nastupi = new LinkedList<>();
    private List<Galerija> slike = new LinkedList<>();
    private List<Galerija> videi = new LinkedList<>();
    private List<Galerija> galerije = new LinkedList<>();
    
    private String videoURL;
    
    private int step = 0;

    public String getDatumOdString(Nastup nastup) {
        DateHelper helper = new DateHelper(nastup);
        return helper.getDatumOdString();
    }
    
    public String getDatumDoString(Nastup nastup) {
        DateHelper helper = new DateHelper(nastup);
        return helper.getDatumDoString();
    }
    
    public String getVremeOdString(Nastup nastup) {
        DateHelper helper = new DateHelper(nastup);
        return helper.getVremeOdString();
    }
    
    public String getVremeDoString(Nastup nastup) {
        DateHelper helper = new DateHelper(nastup);
        return helper.getVremeDoString();
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

    public String getVideoURL() {
        return videoURL;
    }

    public void setVideoURL(String videoURL) {
        this.videoURL = videoURL;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }
    
    public void ucitaj(FileUploadEvent event) {
        
        try {
            byte[] filecontent = event.getFile().getContents();
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            String contentAsString = new String(filecontent);
            JSONObject json = new JSONObject(contentAsString.trim());
            JSONObject festivalJSON = json.getJSONObject("Festival");
            if (festivalJSON != null) {
                festival.setNaziv(festivalJSON.getString("Name"));
                festival.setMesto(festivalJSON.getString("Place"));
                String dateString = festivalJSON.getString("StartDate");
                festival.setDatumOd(formatter.parse(dateString));
                dateString = festivalJSON.getString("EndDate");
                festival.setDatumDo(formatter.parse(dateString));
                if (festival.getDatumOd().after(festival.getDatumDo())) {
                    FacesContext.getCurrentInstance().addMessage("messages", new FacesMessage("Datum kraja mora biti posle datuma pocetka!"));
                    return;
                }
                
                JSONArray karteArray = festivalJSON.getJSONArray("Tickets");
                
                festival.setCenaDan(karteArray.getInt(0));
                festival.setCenaPaket(karteArray.getInt(1));
                
                festival.setDetalji("");
                
                festival.setMaxKarataPoKorisniku(festivalJSON.getInt("MaxPerUser"));
                festival.setKapacitetPoDanu(festivalJSON.getInt("MaxPerDay"));
                
                JSONArray nastupiJSON = festivalJSON.getJSONArray("PerformersList");
                for (int i = 0; i < nastupiJSON.length(); i++) {
                    Nastup nastup = new Nastup();
                    JSONObject nastupObject = nastupiJSON.getJSONObject(i);
                    nastup.setFestival(festival);
                    nastup.setIzvodjac(nastupObject.getString("Performer"));
                    nastup.setDatumOd(formatter.parse(nastupObject.getString("StartDate")));
                    nastup.setDatumDo(formatter.parse(nastupObject.getString("EndDate")));
                    
                    SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss aaa");
                    nastup.setVremeOd(timeFormat.parse(nastupObject.getString("StartTime")));
                    nastup.setVremeDo(timeFormat.parse(nastupObject.getString("EndTime")));
                    nastupi.add(nastup);
                }
                
                JSONArray socialArray = festivalJSON.getJSONArray("SocialNetworks");
                for (int i = 0; i < socialArray.length(); i++) {
                    JSONObject socialObject = socialArray.getJSONObject(i);
                    String socialName = socialObject.getString("Name");
                    String socialLink = socialObject.getString("Link");
                    
                    if (socialName.equalsIgnoreCase("Facebook")) {
                        festival.setFacebook(socialLink);
                    }
                    if (socialName.equalsIgnoreCase("Twitter")) {
                        festival.setTwitter(socialLink);
                    }
                    if (socialName.equalsIgnoreCase("Instagram")) {
                        festival.setInstagram(socialLink);
                    }
                    if (socialName.equalsIgnoreCase("YouTube")) {
                        festival.setYoutube(socialLink);
                    }
                }
                
                //izbaciti posle
                //HELPER.dodajFestival(festival, nastupi, new LinkedList<>());
                
                FacesContext.getCurrentInstance().addMessage("messages", new FacesMessage("Uspesno ucitan festival!"));
            }
        } catch (ParseException ex) {
            FacesContext.getCurrentInstance().addMessage("messages", new FacesMessage("Neuspesno ucitan festival!"));
            Logger.getLogger(ImportJSONBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void drugiKorak() {
        step++;
    }
    
    public void treciKorak() {
        step++;
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
    
    public String potvrdi() {
        FacesContext context = FacesContext.getCurrentInstance();
        galerije.addAll(slike);
        galerije.addAll(videi);
        HELPER.dodajFestival(festival, nastupi, galerije);
        context.addMessage("messages", new FacesMessage("Uspesno dodat festival!"));
        return "/admin/homeAdmin";
    }
    
    public String otkazi() {
        return "/admin/homeAdmin";
    }

    public Festival getFestival() {
        return festival;
    }

    public void setFestival(Festival festival) {
        this.festival = festival;
    }

    public List<Nastup> getNastupi() {
        return nastupi;
    }

    public void setNastupi(List<Nastup> nastupi) {
        this.nastupi = nastupi;
    }

    public List<Galerija> getGalerije() {
        return galerije;
    }

    public void setGalerije(List<Galerija> galerije) {
        this.galerije = galerije;
    }    
}
