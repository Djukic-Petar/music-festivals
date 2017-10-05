package services;

import java.util.concurrent.Semaphore;

public class SemaphoreUtil {
    public static final Semaphore HIBERNATE_SEM = new Semaphore(1);
}
