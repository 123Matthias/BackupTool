package my.backup.backupTool.Controller;

import javafx.animation.PauseTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToolBar;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.FlowPane;
import javafx.util.Duration;

public class MergeOverviewController {

    @FXML
    private ToolBar toolBar;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private FlowPane flowPane; // Dein FlowPane


    @FXML
    private void initialize() {
        // Listener für Änderungen an der Höhe der ToolBar
        toolBar.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                // Sobald die Höhe der ToolBar sich ändert, das Padding des FlowPane anpassen
                double toolBarHeight = toolBar.getHeight();
                flowPane.setStyle("-fx-padding: " + (toolBarHeight) + " 0 0 0;");
            }
        });
    }


    @FXML
    private void handleScroll(ScrollEvent event) {
        // Berechne die neue Y-Position der ToolBar
        double deltaY = event.getDeltaY();
        System.out.println(deltaY);
        System.out.println(scrollPane.getVvalue());
        // Wenn das Scrollen am unteren Ende des ScrollPane angekommen ist und deltaY > 0 (nach unten scrollen), nichts weiter tun

        // Wenn das Scrollen am oberen Ende des ScrollPane angekommen ist und deltaY < 0 (nach oben scrollen), nichts weiter tun
        if (scrollPane.getVvalue() == 0.0) {
            return; // Verhindert das Scrollen nach oben
        }
        if (scrollPane.getVvalue() == 0.0 && deltaY < 0) {
            toolBar.setTranslateY(toolBar.getTranslateY() - deltaY);
        }

        if (scrollPane.getVvalue() == 1 && deltaY > 0) {
            toolBar.setTranslateY(toolBar.getTranslateY() - deltaY);
            return;
        }


        if (scrollPane.getVvalue() == 1) {
            return; // Verhindert das Scrollen nach unten
        }


        System.out.println(toolBar.getTranslateY());
        // Passe die Y-Position der ToolBar an
        toolBar.setTranslateY(toolBar.getTranslateY() - deltaY);

        // Verhindern, dass die ToolBar nach oben hinaus verschoben wird
        if (toolBar.getTranslateY() < 0) {
            toolBar.setTranslateY(0);
        }

        // Optional: Wenn die ToolBar zu weit nach unten verschoben wird, setze sie auf 0 zurück
        if (toolBar.getTranslateY() > scrollPane.getHeight()) {
            toolBar.setTranslateY(scrollPane.getHeight());
        }
    }




    @FXML
    private void setTop(ScrollEvent event) {
        toolBar.setTranslateY(0);

    }

}
