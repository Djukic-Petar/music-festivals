package services;

import db.Festival;
import db.helpers.FestivalHelper;
import java.util.Comparator;

public class OcenaComparator implements Comparator<Festival> {

    @Override
    public int compare(Festival o1, Festival o2) {
        double diff;
        diff = FestivalHelper.getProsecnaOcenaZaFestival(o2) - FestivalHelper.getProsecnaOcenaZaFestival(o1);
        int ret = 0;
        if (diff > 0) {
            ret = 1;
        }
        if (diff < 0) {
            ret = -1;
        }
        return ret;
    }
    
}
