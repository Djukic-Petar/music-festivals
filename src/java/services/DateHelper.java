package services;

import db.Nastup;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class DateHelper {
    private String datumOdString = "";
    private String datumDoString = "";
    private String vremeOdString = "";
    private String vremeDoString = "";
    
    public DateHelper(Nastup nastup) {
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        DateFormat tf = new SimpleDateFormat("HH:mm:ss");
        this.datumOdString = df.format(nastup.getDatumOd());
        this.datumDoString = df.format(nastup.getDatumDo());
        this.vremeOdString = tf.format(nastup.getVremeOd());
        this.vremeDoString = tf.format(nastup.getVremeDo());
    }

    public String getDatumOdString() {
        return datumOdString;
    }

    public void setDatumOdString(String datumOdString) {
        this.datumOdString = datumOdString;
    }

    public String getDatumDoString() {
        return datumDoString;
    }

    public void setDatumDoString(String datumDoString) {
        this.datumDoString = datumDoString;
    }

    public String getVremeOdString() {
        return vremeOdString;
    }

    public void setVremeOdString(String vremeOdString) {
        this.vremeOdString = vremeOdString;
    }

    public String getVremeDoString() {
        return vremeDoString;
    }

    public void setVremeDoString(String vremeDoString) {
        this.vremeDoString = vremeDoString;
    }
    
}
