package beans;

import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import javax.inject.Inject;
import javax.inject.Named;

@Named("navBean")
@SessionScoped
public class NavBean implements Serializable {
    
    private String previousPage = "";
    private String activePage = "content";
    
    @Inject
    private LoginBean loginbean;
    
    public boolean listaFestivalaNeregistrovani() {
        return loginbean.isUnregistered() && !"content".equals(activePage);
    }
    
    public boolean listaFestivalaKorisnik() {
        return loginbean.isRegularUser() && !"homeKorisnik".equals(activePage);
    }
    
    public boolean listaFestivalaAdmin() {
        return loginbean.isAdmin() && !"homeAdmin".equals(activePage);
    }
    
    public String registracija() {
        previousPage = activePage;
        activePage = "register";
        return "/register";
    }
    
    public String promenaLozinke() {
        if (loginbean.isAdmin()) return "/admin/promenaLozinke";
        return "/user/promenaLozinke";
    }

    public String getActivePage() {
        return activePage;
    }

    public void setActivePage(String activePage) {
        previousPage = activePage;
        this.activePage = activePage;
    }
    
    public void back() {
        activePage = previousPage;
        previousPage = "";
    }
    
    public String home() {
        String ret = "";
        if(loginbean.getKorisnik() != null)
            if(loginbean.getKorisnik().isAdmin()) {
                //setActivePage("homeadmin");
                ret = "/admin/homeAdmin";
            } else {
                //setActivePage("homekorisnik");
                ret = "/user/homeKorisnik";
            }
        else {
            //setActivePage("homekorisnik");
            //setActivePage("content");
            ret = "/index";
        }
        return ret;
    }
    
    public String showAddFestival(){
        setActivePage("dodajfestival");
        return "/admin/dodajFestival";
    }
    
    public String showUserHome(){
        setActivePage("homekorisnik");
        return "/admin/homeKorisnik";
    }
    
    public void showIzmeniFestival(){
        setActivePage("izmenifestival");
        //return "/admin/izmeniFestival";
    }
    
    public void showOdobriKorisnika(){
        setActivePage("odobrikorisnika");
        //return "/admin/odobriKorisnik";
    }
    
    public String showRecentLogins(){
        setActivePage("recentlogins");
        return "/admin/najskorijeUlogovani";
    }
    
    public void showImportFestival(){
        setActivePage("importfestival");
        //return "/admin/ucitajFestival";
    }
    
    public void showOdobriProdaju(){
        setActivePage("odobriprodaju");
        //return "/admin/odobriProdaju";
    }
    
    public void showProdajaNeregistrovanim(){
        setActivePage("prodajaneregistrovanim");
        //return "prodajaNeregistrovanim";
    }
}
