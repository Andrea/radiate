package bad.robot.radiate.monitor;

import bad.robot.radiate.Activity;
import bad.robot.radiate.Progress;
import bad.robot.radiate.Status;
import org.apache.log4j.Logger;

public class LoggingObserver implements Observer {

    @Override
    public void update(Observable source, Exception exception) {
        Logger.getLogger(source.getClass()).error(exception.getMessage(), exception);
    }

    @Override
    public void update(Observable source, Information information) {
        Logger.getLogger(source.getClass()).info(information);
    }
}
