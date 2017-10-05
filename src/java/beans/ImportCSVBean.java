package beans;

import com.csvreader.CsvReader;
import db.Festival;
import db.Galerija;
import db.Nastup;
import db.helpers.FestivalHelper;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import services.DateHelper;

@Named(value = "importCSVBean")
@ViewScoped
public class ImportCSVBean implements Serializable {

    private static final FestivalHelper HELPER = new FestivalHelper();
    
    private List<Galerija> galerije = new LinkedList<>();
    private Festival festival = new Festival();
    private List<Galerija> slike = new LinkedList<>();
    private List<Galerija> videi = new LinkedList<>();
    private List<Nastup> nastupi = new ArrayList<>();
    
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
    
    private String videoURL;
    
    private int step = 0;

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
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        UploadedFile file = event.getFile();
        try {
            CsvReader csv = new CsvReader(file.getInputstream(), Charset.defaultCharset());
            festival.setDetalji("");
            
            csv.readHeaders();
            csv.readRecord();
            festival.setNaziv(csv.get("Festival"));
            festival.setMesto(csv.get("Place"));
            String[] date = csv.get("StartDate").split("/");
            festival.setDatumOd(new Date(Integer.parseInt(date[2]) - 1900, Integer.parseInt(date[1]), Integer.parseInt(date[0])));
            date = csv.get("EndDate").split("/");
            festival.setDatumDo(new Date(Integer.parseInt(date[2]) - 1900, Integer.parseInt(date[1]), Integer.parseInt(date[0])));
            
            csv.skipLine();
            
            csv.readHeaders();
            csv.readRecord();
            festival.setCenaDan(Integer.parseInt(csv.get("Price")));
            csv.readRecord();
            festival.setCenaPaket(Integer.parseInt(csv.get("Price")));
            
            csv.skipLine();
            
            csv.readHeaders();
            csv.readRecord();
            festival.setMaxKarataPoKorisniku(Integer.parseInt(csv.get("MaxPerUser")));
            festival.setKapacitetPoDanu(Integer.parseInt(csv.get("MaxPerDay")));
            
            csv.skipLine();
            
            csv.readHeaders();
            while (true) {
                csv.readRecord();
                if (csv.getColumnCount() < 5) {
                    break;
                }
                Nastup nastup = new Nastup();
                nastup.setIzvodjac(csv.get("Performer"));
                date = csv.get("StartDate").split("/");
                nastup.setDatumOd(new Date(Integer.parseInt(date[2]) - 1900, Integer.parseInt(date[1]), Integer.parseInt(date[0])));
                date = csv.get("EndDate").split("/");
                nastup.setDatumDo(new Date(Integer.parseInt(date[2]) - 1900, Integer.parseInt(date[1]), Integer.parseInt(date[0])));
                
                String[] timeFormat = csv.get("StartTime").split(" ");
                String[] time = timeFormat[0].split(":");
                int hour = Integer.parseInt(time[0]);
                int minute = Integer.parseInt(time[1]);
                int second = Integer.parseInt(time[2]);
                if ("PM".equals(timeFormat[1]) && !"12".equals(time[0])) {
                    hour += 12;
                }
                Date dateT = new Date();
                dateT.setHours(hour); dateT.setMinutes(minute); dateT.setSeconds(second);
                nastup.setVremeOd(dateT);
                timeFormat = csv.get("EndTime").split(" ");
                time = timeFormat[0].split(":");
                hour = Integer.parseInt(time[0]);
                minute = Integer.parseInt(time[1]);
                second = Integer.parseInt(time[2]);
                if ("PM".equals(timeFormat[1]) && !"12".equals(time[0])) {
                    hour += 12;
                }
                dateT = new Date();
                dateT.setHours(hour); dateT.setMinutes(minute); dateT.setSeconds(second);
                nastup.setVremeDo(dateT);
                
                nastupi.add(nastup);
            }
            
            festival.setNastups(new HashSet<>(nastupi));
            
            while (csv.readRecord()) {
                switch (csv.get(0)) {
                    case "Facebook":
                        festival.setFacebook(csv.get(1));
                        break;
                    case "Twitter":
                        festival.setTwitter(csv.get(1));
                        break;
                    case "Instagram":
                        festival.setInstagram(csv.get(1));
                        break;
                    case "YouTube":
                        festival.setYoutube(csv.get(1));
                        break;
                }
            }
            
            csv.close();
            
            //HELPER.dodajFestival(festival, nastupi, new LinkedList<>());
            //TODO: dodati i galeriju :P
            
        } catch (IOException ex) {
            Logger.getLogger(ImportCSVBean.class.getName()).log(Level.SEVERE, null, ex);
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
