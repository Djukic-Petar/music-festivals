package beans;

import db.Festival;
import db.helpers.FestivalHelper;
import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

@Named("sidebarBean")
@SessionScoped
public class SidebarBean implements Serializable {
    private Date datumSidebar;
    private String datumSidebarString;
    private List<Festival> festivali;
    
    private Date danas = new Date();
    
    @Inject
    private NavBean navBean;
    
    private static final FestivalHelper HELPER = new FestivalHelper();

    public Date getDanas() {
        return danas;
    }

    public void setDanas(Date danas) {
        this.danas = danas;
    }

    public Date getDatumSidebar() {
        return datumSidebar;
    }

    public void setDatumSidebar(Date datumSidebar) {
        this.datumSidebar = datumSidebar;
    }

    public List<Festival> getFestivali() {
        return festivali;
    }

    public void setFestivali(List<Festival> festivali) {
        this.festivali = festivali;
    }

    public String getDatumSidebarString() {
        return datumSidebarString;
    }

    public void setDatumSidebarString(String datumSidebarString) {
        this.datumSidebarString = datumSidebarString;
    }
    
    public void odabranDatum() {
        navBean.setActivePage("datum");
        festivali = HELPER.getDaysEvents(datumSidebar);
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        datumSidebarString = df.format(datumSidebar);
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect("/ProjekatPIA/listaFestivalaPoDanu.xhtml");
        } catch (IOException ex) {
            Logger.getLogger(SidebarBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
