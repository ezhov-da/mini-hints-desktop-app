package ru.ezhov.hint;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import static javafx.concurrent.Worker.State.FAILED;
import javafx.embed.swing.JFXPanel;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 *
 * @author ezhov_da
 */
public class PanelBrowser extends JPanel {

    private final JFXPanel jfxPanel = new JFXPanel();
    private WebEngine engine;

    private final JLabel lblStatus = new JLabel();

    private final JButton btnGo = new JButton("Go");
    private final JTextField txtURL = new JTextField();
    private final JProgressBar progressBar = new JProgressBar();

    private JCheckBox checkBoxUseProxy;
    private JLabel labelHost;
    private JTextField textFieldHost;
    private JLabel labelPort;
    private JTextField textFieldPort;

    private String sysProxy;
    private String sysProxyPort;

    public PanelBrowser() {
        super(new BorderLayout());
        sysProxy = System.getProperty("http.proxyHost");
        sysProxyPort = System.getProperty("http.proxyHost");
        initComponents();
    }

    private void initComponents() {
        createScene();
        ActionListener al = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (checkBoxUseProxy.isSelected()) {
                    System.setProperty("http.proxyHost", textFieldHost.getText());
                    System.setProperty("http.proxyPort", textFieldPort.getText());
                } else if (Objects.nonNull(sysProxy)) {
                    System.setProperty("http.proxyHost", sysProxy);
                    System.setProperty("http.proxyPort", sysProxyPort);
                }
                loadURL(txtURL.getText());
            }
        };
        btnGo.addActionListener(al);
        txtURL.addActionListener(al);
        progressBar.setPreferredSize(new Dimension(150, 18));
        progressBar.setStringPainted(true);
        JPanel topBar = new JPanel(new BorderLayout(5, 0));
        topBar.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));
        topBar.add(txtURL, BorderLayout.CENTER);
        topBar.add(btnGo, BorderLayout.EAST);
        JPanel topBarHostPort = new JPanel();
        checkBoxUseProxy = new JCheckBox("use proxy");
        labelHost = new JLabel("Host:");
        textFieldHost = new JTextField(15);
        labelPort = new JLabel("Port:");
        textFieldPort = new JTextField(5);
        topBarHostPort.add(checkBoxUseProxy);
        topBarHostPort.add(labelHost);
        topBarHostPort.add(textFieldHost);
        topBarHostPort.add(labelPort);
        topBarHostPort.add(textFieldPort);
        JPanel panelBar = new JPanel(new BorderLayout());
        panelBar.add(topBar, BorderLayout.CENTER);
        panelBar.add(topBarHostPort, BorderLayout.EAST);
        JPanel statusBar = new JPanel(new BorderLayout(5, 0));
        statusBar.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));
        statusBar.add(lblStatus, BorderLayout.CENTER);
        statusBar.add(progressBar, BorderLayout.EAST);
        add(panelBar, BorderLayout.NORTH);
        add(jfxPanel, BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);
    }

    private void createScene() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                WebView view = new WebView();
                engine = view.getEngine();
//                engine.titleProperty().addListener(new ChangeListener<String>() {
//                    @Override
//                    public void changed(ObservableValue<? extends String> observable, String oldValue, final String newValue) {
//                        SwingUtilities.invokeLater(new Runnable() {
//                            @Override
//                            public void run() {
//                                SimpleSwingBrowser.this.setTitle(newValue);
//                            }
//                        });
//                    }
//                });
                engine.setOnStatusChanged(new EventHandler<WebEvent<String>>() {
                    @Override
                    public void handle(final WebEvent<String> event) {
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                lblStatus.setText(event.getData());
                            }
                        });
                    }
                });
                engine.locationProperty().addListener(new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> ov, String oldValue, final String newValue) {
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                txtURL.setText(newValue);
                            }
                        });
                    }
                });
                engine.getLoadWorker().workDoneProperty().addListener(new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, final Number newValue) {
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setValue(newValue.intValue());
                            }
                        });
                    }
                });
                engine.getLoadWorker()
                        .exceptionProperty()
                        .addListener(new ChangeListener<Throwable>() {
                            public void changed(ObservableValue<? extends Throwable> o, Throwable old, final Throwable value) {
                                if (engine.getLoadWorker().getState() == FAILED) {
                                    SwingUtilities.invokeLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            StringWriter stringWriter = new StringWriter();
                                            value.printStackTrace(new PrintWriter(stringWriter));
                                            JOptionPane.showMessageDialog(
                                                    PanelBrowser.this,
                                                    (value != null)
                                                            ? engine.getLocation() + "\n" + value.getMessage() + "\n" + stringWriter.toString()
                                                            : engine.getLocation() + "\nUnexpected error.",
                                                    "Loading error...",
                                                    JOptionPane.ERROR_MESSAGE);
                                        }
                                    });
                                }
                            }
                        });
                jfxPanel.setScene(new Scene(view));
            }
        });
    }

    public void loadURL(final String url) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                String tmp = toURL(url);
                if (tmp == null) {
                    tmp = toURL("http://" + url);
                }
                engine.load(tmp);
            }
        });
    }

    private static String toURL(String str) {
        try {
            return new URL(str).toExternalForm();
        } catch (MalformedURLException exception) {
            return null;
        }
    }
}
