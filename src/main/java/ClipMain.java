
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by advman on 2016-09-27.
 */
public class ClipMain extends Application {

    public ClipController controller;
    private BorderPane mainLayout;
    private Stage primaryStage;
    static Logger log = Logger.getLogger(ClipMain.class.getName());
    private static String prevClip = "";
    private static boolean endLess = true;
    private static ObservableList<Clip> clipData = FXCollections.observableArrayList();
    private static final String clipSufix = "!!!advMan!!!";

    @Override
    public void start(Stage stage) throws IOException {
        startClipMng(stage);
    }
    public void startClipMng(Stage primaryStage) throws IOException {
        // Load the GUI. The MainController class will be automatically created and wired up.
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("MaxPaul ClipBoard Tracker");

        URL location = getClass().getResource("ClipBoard.fxml");
        FXMLLoader loader = new FXMLLoader(location);
        mainLayout = loader.load();

        controller = loader.getController();
        controller.setMain(this);

        Scene scene = new Scene(mainLayout);

        primaryStage.setScene(scene);
        primaryStage.show();

        primaryStage.setOnCloseRequest(event -> {
            endLess = false;
        });
    }

    public static ObservableList<Clip> getClipData() {
        //load the log file
        List<String> clipsOut= null;
        clipsOut = loadLog();

        //Upate the collection with clip data
        Collections.reverse(clipsOut);
        clipsOut
                .stream()
                .forEach(s -> {
                    clipData.add(new Clip(s.substring(0, 8), s.substring(12), false));
                });
        return clipData;
    }

    public static void main(String[] args) {

        log.debug("Clipboard check started.");

        // separate non-FX thread
        Runnable r = new Runnable() {
            public void run() {
                do {
                    Button refreshBtn = null;
                    String clipBoardRaw = getStringFromClipboard();
                    // bug fix remove the UFT-8 or non ASCII letters charactors
                    if (clipBoardRaw !=null) {
                        String clipBoard = clipBoardRaw.replaceAll("[^\\x00-\\x7F]", "");
                        if (!clipBoard.trim().isEmpty()) {
                            if (!prevClip.equals(clipBoard)) {
                                log.trace(clipBoard + clipSufix);
                                prevClip = clipBoard;
                            }
                        }
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        log.warn("Clipboard check thread interrupt " + ex.getMessage());
                        Thread.currentThread().interrupt();
                    }

                } while (endLess);
            }
        };
        new Thread(r).start();
        launch(args);
    }

    //retrieve string from clipboard
    public static String getStringFromClipboard() {
        Transferable transferable = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
        try {
            if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                String text = (String) transferable.getTransferData(DataFlavor.stringFlavor);
                return text;
            }
        } catch (UnsupportedFlavorException e) {
            log.warn("Clipboard content flavor is not supported " + e.getMessage());
        } catch (IOException e) {
            log.warn("Clipboard content could not be retrieved " + e.getMessage());
        }
        return null;
    }

    //Load the log into the list
    private static List<String> loadLog() {
        final String clipFile = "Logging.log";
        final String clipPrefix = "[TRACE]";
        final String clipSufix = "!!!advMan!!!";

        // read the log file into a list
        Stream<String> stream2 = null;
        try {
            stream2 = Files.lines(Paths.get(clipFile));
        } catch (IOException e) {
            log.warn("Stream log file exception " + e.getMessage());
            e.printStackTrace();
        }
        List arrayClipsLog = stream2 != null ? stream2.collect(Collectors.toList()) : null;

        // loop through the array and parse out the clipboard text into new array
        // append the related but individual line values.
        List<String> clipsOut = new ArrayList<>();
        int i = 0;
        int outI = 0;
        boolean needSuffix = false;
        StringBuilder strClip = new StringBuilder();

        while (i < (arrayClipsLog != null ? arrayClipsLog.size() : 0)) {
            String clipIn = arrayClipsLog.get(i).toString();
            if (!needSuffix) {
                strClip = new StringBuilder();
                if (clipIn.length() > 19) {
                    if (clipIn.substring(13, 20).equals(clipPrefix)) {
                        strClip.append(clipIn.substring(0, 12));
                        // Check for end tag
                        if (clipIn.lastIndexOf(clipSufix) > 0) {
                            strClip.append(clipIn.substring(21, clipIn.lastIndexOf(clipSufix)));
                            clipsOut.add(outI, strClip.toString());
                            outI++;
                            needSuffix = false;
                        } else {
                            strClip.append(clipIn.substring(21)).append("\r\n");
                            needSuffix = true;
                        }
                    }
                }
            } else {
                if (clipIn.lastIndexOf(clipSufix) > 0) {
                    strClip.append(clipIn.substring(0, clipIn.lastIndexOf(clipSufix)));
                    clipsOut.add(outI, strClip.toString());
                    outI++;
                    needSuffix = false;
                } else {
                    strClip.append(clipIn).append("\r\n");
                }
            }
            i++;
        }
        return clipsOut;
    }

}