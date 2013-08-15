package bad.robot.radiate.monitor;

import bad.robot.radiate.Status;

public interface Observable {

    boolean addObservers(Observer... observer);

    boolean removeObservers(Observer... observer);

    void notifyObservers(Status status);

    void notifyObservers(Exception exception);
}
