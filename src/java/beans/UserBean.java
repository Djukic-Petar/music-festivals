package beans;

import db.helpers.KorisnikHelper;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import validation.Password;

@Named(value = "userBean")
@RequestScoped
public class UserBean {
    
    private static final KorisnikHelper HELPER = new KorisnikHelper();

    private String oldPassword;
    @Password
    private String newPassword;
    
    @Inject
    private LoginBean loginbean;

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
    
    public String promeniPassword() {
        if (oldPassword.equals(loginbean.getKorisnik().getPassword())) {
            HELPER.promeniLozinku(newPassword, loginbean.getKorisnik());
            return "/index";
        } else {
            FacesContext.getCurrentInstance().addMessage("message", new FacesMessage("Neuspeh!", "Pogresno ste uneli staru lozinku!"));
            return null;
        }
    }
    
}
