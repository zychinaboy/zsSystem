package cn.njit.edu.view;

import cn.njit.edu.entity.PrinterConfig;
import cn.njit.edu.task.PrintTask;
import com.dlsc.workbenchfx.Workbench;
import com.dlsc.workbenchfx.model.WorkbenchDialog;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.util.StringConverter;

import static javafx.concurrent.WorkerStateEvent.*;

public class PrintSettingDialog extends WidgetBase {
    private static String FXML = "/fxml/printsetting.fxml";
    private final ComboBox cbPrinter;
    private final Spinner<Integer> fromPage;
    private final Spinner<Integer> endPage;
    private final PrinterConfig printerConfig;

    public PrintSettingDialog(int max, PrinterConfig printerConfig) {
        super(FXML);
        this.printerConfig = printerConfig;
        this.cbPrinter = this.$("comPrinter", ComboBox.class);
        this.fromPage = this.$("fromPage", Spinner.class);
        this.endPage = this.$("endPage", Spinner.class);

        this.fromPage.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1,max));
        this.endPage.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1,max,max));
        initUi();

    }

    private void initPrinter() {
        this.cbPrinter.itemsProperty().bind(printerConfig.getPrinterNameListProperty());
        this.cbPrinter.valueProperty().bindBidirectional(printerConfig.getPrinterNameProperty());
    }
    private void initUi() {
        StackPane.setAlignment(this.root, Pos.CENTER);
        // useage in client code
        fromPage.focusedProperty().addListener((s, ov, nv) -> {
            if (nv) return;
            commitEditorText(fromPage);
        });
        endPage.focusedProperty().addListener((s, ov, nv) -> {
            if (nv) return;
            commitEditorText(endPage);
        });
        initPrinter();
    }
    private <T> void commitEditorText(Spinner<T> spinner) {
        if (!spinner.isEditable()) return;
        String text = spinner.getEditor().getText();
        SpinnerValueFactory<T> valueFactory = spinner.getValueFactory();
        if (valueFactory != null) {
            StringConverter<T> converter = valueFactory.getConverter();
            if (converter != null) {
                T value = converter.fromString(text);
                valueFactory.setValue(value);
            }
        }
    }


    public int getFromPage() {
        return this.fromPage.getValue();
    }
    public int getEndPage() {
        return this.endPage.getValue();
    }
}
