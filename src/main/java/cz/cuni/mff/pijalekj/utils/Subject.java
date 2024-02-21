package cz.cuni.mff.pijalekj.utils;

public interface Subject {
    void attach(Observer observer);
    void detach(Observer observer);
    void notifySubject();
}
