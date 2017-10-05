package beans;

import db.Festival;
import db.helpers.FestivalHelper;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

@Named(value = "cancelBean")
@RequestScoped
public class CancelBean {
    private static final FestivalHelper HELPER = new FestivalHelper();
    
    @Inject
    private SearchBean searchBean;
    
    public void cancel(Festival festival) {
        HELPER.otkazi(festival);
        searchBean.setFestivali(HELPER.dohvatiSveFestivale());
    }
}
