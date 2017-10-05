package db;
// Generated Feb 10, 2017 9:46:45 PM by Hibernate Tools 4.3.1



/**
 * Ocena generated by hbm2java
 */
public class Ocena  implements java.io.Serializable {


     private Long idOce;
     private Festival festival;
     private Korisnik korisnik;
     private int ocena;
     private String komentar;
     private String imeFestivala;

    public Ocena() {
    }

    public Ocena(Festival festival, Korisnik korisnik, int ocena, String komentar, String imeFestivala) {
       this.festival = festival;
       this.korisnik = korisnik;
       this.ocena = ocena;
       this.komentar = komentar;
       this.imeFestivala = imeFestivala;
    }
   
    public Long getIdOce() {
        return this.idOce;
    }
    
    public void setIdOce(Long idOce) {
        this.idOce = idOce;
    }
    public Festival getFestival() {
        return this.festival;
    }
    
    public void setFestival(Festival festival) {
        this.festival = festival;
    }
    public Korisnik getKorisnik() {
        return this.korisnik;
    }
    
    public void setKorisnik(Korisnik korisnik) {
        this.korisnik = korisnik;
    }
    public int getOcena() {
        return this.ocena;
    }
    
    public void setOcena(int ocena) {
        this.ocena = ocena;
    }
    public String getKomentar() {
        return this.komentar;
    }
    
    public void setKomentar(String komentar) {
        this.komentar = komentar;
    }
    public String getImeFestivala() {
        return this.imeFestivala;
    }
    
    public void setImeFestivala(String imeFestivala) {
        this.imeFestivala = imeFestivala;
    }




}

