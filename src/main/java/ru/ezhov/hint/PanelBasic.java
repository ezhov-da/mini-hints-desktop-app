package ru.ezhov.hint;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Как то так
 * <p>
 *
 * @author ezhov_da
 */
public class PanelBasic extends JPanel {
    private static final Logger LOG = Logger.getLogger(PanelBasic.class.getName());
    private Icon iconStandart;
    private Icon iconGif;

    private JLabel label;

    private JToggleButton toggleButton;
    private JDialog dialog;
    private Dimension sizeDialogBegin;


    private JFXPanel fXPanel;

    private PanelBrowser panelBrowser;


    public PanelBasic(JDialog dialog) {
        this.dialog = dialog;
        iconStandart = new ImageIcon(PanelBasic.class.getResource("/finished-work.png"));
        iconGif = new ImageIcon(PanelBasic.class.getResource("/show.gif"));
        toggleButton = new JToggleButton();
        toggleButton.setFocusPainted(false);
        toggleButton.setIcon(iconStandart);
        toggleButton.setPreferredSize(new Dimension(30, 30));
        toggleButton.addActionListener(e ->
        {
            SwingUtilities.invokeLater(() ->
            {
                if (toggleButton.isSelected()) {
                    panelBrowser.setVisible(true);
                    dialog.setSize(800, 600);
                } else {
                    panelBrowser.setVisible(false);
                    dialog.setSize(sizeDialogBegin);
                }
                revalidate();
            });
        });
        toggleButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    System.exit(0);
                }
            }
        });
        label = new JLabel("<html><p style=\"text-align: center;\">&#0149;</p>");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setOpaque(false);
        label.setBackground(Color.GRAY);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createLineBorder(Color.RED));
        add(toggleButton, BorderLayout.WEST);
        add(label, BorderLayout.NORTH);
        createPanel();
        panelBrowser = new PanelBrowser();
        panelBrowser.setVisible(false);
        add(panelBrowser, BorderLayout.CENTER);
        MouseMoveWindowListener moveListener = new MouseMoveWindowListener();
        label.addMouseMotionListener(moveListener);
        label.addMouseListener(moveListener);
    }

    private void createPanel() {
        fXPanel = new JFXPanel();
        Platform.runLater(() ->
        {
            Scene scene = createScene();
            fXPanel.setScene(scene);
            //fXPanel.setSize(750, 500);
        });
    }


    public void startTask() {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                toggleButton.setIcon(iconGif);
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(PanelBasic.class.getName()).log(Level.SEVERE, null, ex);
                }
                toggleButton.setIcon(iconStandart);
            }
        };
        Timer timer = new Timer();
        timer.schedule(timerTask, 5000, 60000);
    }


    private Scene createScene() {
        Group root = new Group();
        Scene scene;
        scene = new Scene(root, javafx.scene.paint.Color.web("#666970"));
        WebView browser = new WebView();
        WebEngine webEngine = browser.getEngine();
        webEngine.load("file:///C:/Users/ezhov_da/Desktop/test.html");
        //webEngine.load("https://www.google.ru");
        root.getChildren().add(browser);
        return (scene);
    }


    private class MouseMoveWindowListener extends MouseAdapter {

        private Point diffOnScreen;


        @Override
        public void mousePressed(MouseEvent e) {
            Point pressedPointLocationOnScreen;
            pressedPointLocationOnScreen = e.getLocationOnScreen();
            int x = pressedPointLocationOnScreen.x - dialog.getLocationOnScreen().x;
            int y = pressedPointLocationOnScreen.y - dialog.getLocationOnScreen().y;
            diffOnScreen = new Point(x, y);
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            Point nowMouseLocation = e.getLocationOnScreen();
            dialog.setLocation(
                    nowMouseLocation.x - diffOnScreen.x,
                    nowMouseLocation.y - diffOnScreen.y);
        }
    }

    public void setSizeDialogBegin(Dimension sizeDialogBegin) {
        this.sizeDialogBegin = sizeDialogBegin;
    }


}
