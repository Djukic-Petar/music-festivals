package services;

import db.Ulaznica;
import db.helpers.FestivalHelper;
import db.helpers.UlaznicaHelper;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HourEvent extends TimerTask {
    
    private static final UlaznicaHelper HELPER = new UlaznicaHelper();
    
    @Override
    public void run() {
        try {
            SemaphoreUtil.HIBERNATE_SEM.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(FestivalHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("DOHVATAM ULAZNICEEEEEEEEEEEE");
        List<Ulaznica> ulaznice = HELPER.getSveUlazniceNonBlocking();
        Date now = new Date();
        for (Ulaznica ulaznica : ulaznice) {
            if (!ulaznica.isIsteklo() && !ulaznica.isProdato()) {
                int brDana = (int)((now.getTime() - ulaznica.getVremeKreiranja().getTime()) / (1000*60*60*24));
                if (brDana >= 2) {
                    HELPER.istekniNonBlocking(ulaznica);
                }
            }
        }
        SemaphoreUtil.HIBERNATE_SEM.release();
    }
}
