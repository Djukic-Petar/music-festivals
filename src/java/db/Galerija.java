package db;
// Generated Feb 10, 2017 9:46:45 PM by Hibernate Tools 4.3.1



/**
 * Galerija generated by hbm2java
 */
public class Galerija  implements java.io.Serializable {


     private Long idGal;
     private Festival festival;
     private String putanja;
     private int tip;

    public Galerija() {
    }

    public Galerija(Festival festival, String putanja, int tip) {
       this.festival = festival;
       this.putanja = putanja;
       this.tip = tip;
    }
   
    public Long getIdGal() {
        return this.idGal;
    }
    
    public void setIdGal(Long idGal) {
        this.idGal = idGal;
    }
    public Festival getFestival() {
        return this.festival;
    }
    
    public void setFestival(Festival festival) {
        this.festival = festival;
    }
    public String getPutanja() {
        return this.putanja;
    }
    
    public void setPutanja(String putanja) {
        this.putanja = putanja;
    }
    public int getTip() {
        return this.tip;
    }
    
    public void setTip(int tip) {
        this.tip = tip;
    }




}

