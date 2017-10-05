package services;

import java.util.Timer;
import javax.inject.Named;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Destroyed;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;

@Named(value = "timerPasulj")
@ApplicationScoped
public class TimerPasulj {
    private Timer timer;
    private HourEvent event;
    
    public void init(@Observes @Initialized(ApplicationScoped.class) Object init) {
        event = new HourEvent();
        timer = new Timer("PerMinuteRefresh", true);
        timer.scheduleAtFixedRate(event, 60*5*1000, 30*1000);
    }
    
    public void destroy(@Observes @Destroyed(ApplicationScoped.class) Object init) {
        timer.cancel();
    }
}
