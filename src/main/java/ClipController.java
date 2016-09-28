import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

/**
 * Created by advman on 2016-09-27.
 */
public class ClipController implements ClipboardOwner {

    @FXML
    private TableView<Clip> clipTable;
    @FXML
    private TableColumn<Clip, String> clipTextCol;
    @FXML
    private TableColumn<Clip, String> clipTimeCol;
    @FXML
    private TableColumn<Clip, Boolean> clickToCopyCol;
    @FXML
    private Button refreshBtn;
    private Clip model = new Clip();
    public ClipController (){

    }
    @FXML
    private void initialize() {
        clipTextCol.setCellValueFactory(cellData -> cellData.getValue().textValueProperty());
        clipTimeCol.setCellValueFactory(cellData -> cellData.getValue().timeStampProperty());
        // define a simple boolean cell value for the action column so that the column will only be shown for non-empty rows.
        clickToCopyCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Clip, Boolean>, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<Clip, Boolean> param) {
                return new SimpleBooleanProperty(param.getValue() != null);
            }
        });
        // create a cell value factory with an add button for each row in the table.
        clickToCopyCol.setCellFactory(new Callback<TableColumn<Clip, Boolean>, TableCell<Clip, Boolean>>() {
            @Override public TableCell<Clip, Boolean> call(TableColumn<Clip, Boolean> p) {
                return new ButtonCell();
            }
        });

        refreshBtn.defaultButtonProperty().bind(model.refreshBtnProperty());

        Tooltip tooltip = new Tooltip("Click to refresh");
        Image imageClip = new Image(getClass().getResourceAsStream("refreshImg.png"));
        ImageView butView = new ImageView();
        butView.setImage(imageClip);
        butView.setFitHeight(20);
        butView.setFitWidth(15);

        refreshBtn.setGraphic(butView);
        refreshBtn.setTooltip(tooltip);
        refreshBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                clipTable.getItems().clear();
                clipTable.setItems(ClipMain.getClipData());
                clipTable.scrollTo(0);
            }
        });
    }

    public void setMain(ClipMain clipCheck){
        clipTable.setItems(clipCheck.getClipData());
    }
    /**
     * Place a String on the clipboard, and make this class the
     * owner of the Clipboard's contents.
     */
    public void setClipboardContents(String aString){
        StringSelection stringSelection = new StringSelection(aString);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection,this);
    }

    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents) {
    }

    //define button cell
    private class ButtonCell extends TableCell<Clip, Boolean> {
        private Button cellButton;
        final Tooltip tooltip = new Tooltip("Click to copy\r\nto clipboard");

        ButtonCell() {
            Image imageClip = new Image(getClass().getResourceAsStream("clipBoardImg.png"));
            cellButton = new Button();
            ImageView butView = new ImageView();
            butView.setImage(imageClip);
            butView.setFitHeight(20);
            butView.setFitWidth(15);
            cellButton.setGraphic(butView);
            cellButton.setTooltip(tooltip);
            cellButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    int selIndex = getTableRow().getIndex();
                    Clip selectdClip = (Clip)clipTable.getItems().get(selIndex);
                    setClipboardContents(selectdClip.getTextValue());

                }
            });
            DropShadow shadow = new DropShadow();
            //Adding the shadow when the mouse cursor is on
            cellButton.addEventHandler(MouseEvent.MOUSE_ENTERED,
                    new EventHandler<MouseEvent>() {
                        @Override public void handle(MouseEvent e) {
                            cellButton.setEffect(shadow);
                        }
                    });
            //Removing the shadow when the mouse cursor is off
            cellButton.addEventHandler(MouseEvent.MOUSE_EXITED,
                    new EventHandler<MouseEvent>() {
                        @Override public void handle(MouseEvent e) {
                            cellButton.setEffect(null);
                        }
                    });
        }
        /** places a button in the tableview row only if the row is not empty. */
        @Override protected void updateItem(Boolean item, boolean empty) {
            super.updateItem(item, empty);
            if (!empty) {
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                setGraphic(cellButton);
            } else {
                setGraphic(null);
            }
        }
        private void setSelection(IndexedCell cell){
            if(cell.isSelected()) {
                clipTable.getSelectionModel().clearSelection(cell.getIndex());
            } else {
                clipTable.getSelectionModel().select(cell.getIndex());
            }
        }
    }
}