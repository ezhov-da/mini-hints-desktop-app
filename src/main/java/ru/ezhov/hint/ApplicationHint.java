package ru.ezhov.hint;

import javax.swing.*;

public class ApplicationHint {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
        {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Throwable ex) {
                //
            }
            HintFrame frame = new HintFrame();
            frame.setVisible(true);
        });
    }
}
