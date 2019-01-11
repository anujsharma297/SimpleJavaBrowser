package FinalMark_6;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.embed.swing.JFXPanel;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;

public class MyBrowser2 extends JFrame {

	// panel to display web page content
	private final JFXPanel jfxPanel = new JFXPanel();

	// non-visual object that manages web pages
	private WebEngine engine;

	// this panel will contain all other panels
	private final JPanel panel = new JPanel(new BorderLayout());

	// displays status of the web page.
	private final JLabel lblStatus = new JLabel();

	// button Go
	private final JButton btnGo = new JButton("Go");

	// Search here. in the text field
	private final JTextField searchField = new JTextField();

	// displays the progress of the page
	private final JProgressBar progressBar = new JProgressBar();

	public MyBrowser2() {
		super();

		initializeCompoments();

		setPreferredSize(new Dimension(1024, 600));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		pack();
	}

	private void initializeCompoments() {
		// set the gui for display through setScene() method.
		createSceneForDisplay();

		// adding functionality to the button Go
		btnGo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				loadURL(searchField.getText());
			}
		});

		// pressing enter will load the webPage
		searchField.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					loadURL(searchField.getText());
				}
			}
		});

		// sets the size of the progress bar
		progressBar.setPreferredSize(new Dimension(150, 18));

		// displays the percentage of task completed
		progressBar.setStringPainted(true);

		// the top bar which contains search field and go button
		JPanel topBar = new JPanel(new BorderLayout(5, 0));
		topBar.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));// sets
																		// margin
																		// in
																		// the
																		// panel
		topBar.add(searchField, BorderLayout.CENTER);
		topBar.add(btnGo, BorderLayout.EAST);

		// the status Bar which displays the status and progess bar
		JPanel statusBar = new JPanel(new BorderLayout(5, 0));
		statusBar.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));
		statusBar.add(lblStatus, BorderLayout.CENTER);
		statusBar.add(progressBar, BorderLayout.EAST);

		// adding all components in the main Component that is #panel.
		panel.add(topBar, BorderLayout.NORTH);
		panel.add(jfxPanel, BorderLayout.CENTER);
		panel.add(statusBar, BorderLayout.SOUTH);

		getContentPane().add(panel);

	}

	private void createSceneForDisplay() {

		Platform.runLater(new Runnable() {

			@Override
			public void run() {

				WebView view = new WebView();
				engine = view.getEngine();

				// change window title according to the current page
				engine.titleProperty().addListener(new ChangeListener<String>() {

					@Override
					public void changed(ObservableValue<? extends String> observable, String oldValue,
							String newValue) {
						SwingUtilities.invokeLater(new Runnable() {

							@Override
							public void run() {
								MyBrowser2.this.setTitle(newValue);
							}
						});
					}
				});

				// change status on the status bar as cursor is moved or click
				// on web link. (displays on the left bottom corner)
				engine.setOnStatusChanged(new EventHandler<WebEvent<String>>() {

					@Override
					public void handle(WebEvent<String> event) {
						SwingUtilities.invokeLater(new Runnable() {

							@Override
							public void run() {
								lblStatus.setText(event.getData());
							}
						});
					}
				});

				// changes the search text field according to the new Page
				engine.locationProperty().addListener(new ChangeListener<String>() {

					@Override
					public void changed(ObservableValue<? extends String> observable, String oldValue,
							String newValue) {
						SwingUtilities.invokeLater(new Runnable() {

							@Override
							public void run() {
								searchField.setText(newValue);
							}
						});
					}
				});

				// changes the progess on progress bar
				engine.getLoadWorker().workDoneProperty().addListener(new ChangeListener<Number>() {

					@Override
					public void changed(ObservableValue<? extends Number> observable, Number oldValue,
							Number newValue) {
						SwingUtilities.invokeLater(new Runnable() {

							@Override
							public void run() {
								progressBar.setValue(newValue.intValue());
							}
						});
					}
				});

				// displaying the error if page doesn't open up
				engine.getLoadWorker().exceptionProperty().addListener(new ChangeListener<Throwable>() {

					@Override
					public void changed(ObservableValue<? extends Throwable> observable, Throwable oldValue,
							Throwable newValue) {
						if (engine.getLoadWorker().getState() == State.FAILED) {
							SwingUtilities.invokeLater(new Runnable() {

								@Override
								public void run() {
									JOptionPane.showMessageDialog(panel,
											(newValue != null) ? engine.getLocation() + "\n" + newValue.getMessage()
													: engine.getLocation() + "\nUnexpected error.",
											"Loading error... ", JOptionPane.ERROR_MESSAGE);
								}
							});
						}
					}
				});

				jfxPanel.setScene(new Scene(view));
			}
		});
	}

	private void loadURL(String text) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				String url = toURL(text);

				if (url == null) {
					url = toURL("http://" + text);
				}

				engine.load(url);
			}
		});
	}

	private static String toURL(String text) {
		try {
			return new URL(text).toString();
		} catch (MalformedURLException exception) {
			return null;
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				MyBrowser2 browser = new MyBrowser2();
				browser.loadURL("http://google.com");
			}
		});
	}
}
