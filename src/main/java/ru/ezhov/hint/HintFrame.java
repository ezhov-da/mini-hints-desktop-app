package ru.ezhov.hint;

import javax.swing.*;
import java.util.logging.Logger;

/**
 * @author ezhov_da
 */
public class HintFrame extends JDialog {
    private static final Logger LOG = Logger.getLogger(HintFrame.class.getName());
    private PanelBasic panelBasic;

    public HintFrame() {
        panelBasic = new PanelBasic(this);
        add(panelBasic);
        setUndecorated(true);
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);
        pack();
        panelBasic.setSizeDialogBegin(getSize());
    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        panelBasic.startTask();
    }


}
