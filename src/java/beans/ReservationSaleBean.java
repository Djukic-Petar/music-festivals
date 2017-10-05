package beans;

import db.Ulaznica;
import db.helpers.UlaznicaHelper;
import javax.inject.Named;
import java.io.Serializable;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;

@Named(value = "reservationSaleBean")
@ViewScoped
public class ReservationSaleBean implements Serializable {
    private static final UlaznicaHelper HELPER = new UlaznicaHelper();
    
    private long reservationId = 0;

    public long getReservationId() {
        return reservationId;
    }

    public void setReservationId(long reservationId) {
        this.reservationId = reservationId;
    }
    
    public String prikaziRezervaciju() {
        return "/admin/rezervacija";
    }
    
    public String prodaj() {
        Ulaznica u = HELPER.getUlaznica(reservationId);
        if (u == null) {
            FacesContext.getCurrentInstance().addMessage("messages", new FacesMessage("Nevalidan broj rezervacije!"));
            return "";
        }
        if (!u.isIsteklo() && !u.isProdato()) {
            HELPER.prodaj(u);
            FacesContext.getCurrentInstance().addMessage("messages", new FacesMessage("Uspesno prodata ulaznica!"));
            return "";
        } else {
            FacesContext.getCurrentInstance().addMessage("messages", new FacesMessage("Rezervacija je istekla ili je vec prodata!"));
            return "";
        }
    }
    
}
