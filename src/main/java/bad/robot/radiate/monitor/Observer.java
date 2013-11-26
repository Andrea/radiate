package bad.robot.radiate.monitor;

import bad.robot.radiate.Activity;
import bad.robot.radiate.Status;
import bad.robot.radiate.teamcity.Progress;

public interface Observer {
    void update(Observable source, Status status);
    void update(Observable source, Activity activity, Progress progress);
    void update(Observable source, Information information);
    void update(Observable source, Exception exception);
}
