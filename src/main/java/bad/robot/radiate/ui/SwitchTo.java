package bad.robot.radiate.ui;

import java.awt.*;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.function.Supplier;

import static bad.robot.radiate.Main.Radiate;
import static bad.robot.radiate.monitor.MonitoringTasksFactory.singleAggregate;

class SwitchTo extends KeyAdapter implements AWTEventListener {

    private final Supplier<FrameFactory> mode;
    private final int keyCode;

    public SwitchTo(Supplier<FrameFactory> mode, int keyCode) {
        this.mode = mode;
        this.keyCode = keyCode;
    }

    @Override
    public void keyPressed(KeyEvent event) {
        if (event.getKeyCode() == keyCode) {
            Radiate.stop();
            Radiate.start(singleAggregate(), mode.get()); // TODO make singleAggregate() the current one, don't hardcode it
        }
    }

    @Override
    public void eventDispatched(AWTEvent event) {
        if (event.getID() == KeyEvent.KEY_PRESSED)
            keyPressed((KeyEvent) event);
    }

}