package cn.njit.edu.view;

import cn.njit.edu.task.PrintTask;
import com.dlsc.workbenchfx.Workbench;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

import static javafx.concurrent.WorkerStateEvent.*;

public class PrintDialog extends WidgetBase {
    private static String FXML = "/fxml/printdialog.fxml";
    private final Button btnCancel;
    private final ProgressBar pb;
    private final Label lbMessage;
    private final Workbench workbench;
    private PrintTask task;

    public PrintDialog(Workbench workbench) {
        super(FXML);
        this.workbench = workbench;
        this.btnCancel = this.$("btnCancel", Button.class);
        this.pb = this.$("pb", ProgressBar.class);
        this.lbMessage = this.$("lbMessage", Label.class);
    }

    private void initUi(ReadOnlyStringProperty statusProperty, ReadOnlyDoubleProperty pbProperty) {
        StackPane.setAlignment(this.root, Pos.CENTER);

        this.pb.progressProperty().bind(pbProperty);
        lbMessage.textProperty().bind(statusProperty);
        pb.progressProperty().bind(pbProperty);
        this.btnCancel.setOnAction(this::cancel);
        workbench.showOverlay(this.asRegion(), true);
    }

    private void cancel(ActionEvent actionEvent) {
        if (task == null || task.isDone()) {
            workbench.hideOverlay(this.asRegion());
            return;
        }
        this.task.pause();
         workbench.showConfirmationDialog("请确认", "确定取消打印吗？", buttonType -> {
            if (buttonType.getButtonData() == ButtonBar.ButtonData.YES) {
                this.task.cancel();
                workbench.hideOverlay(this.asRegion());
            }else
            {
                this.task.resume();
            }

        });
    }

    public void waitFor(PrintTask task) {
        this.task = task;
        Region region = this.asRegion();
        initUi(task.messageProperty(), task.progressProperty());
        task.addEventHandler(WORKER_STATE_SUCCEEDED,value -> {
            workbench.hideOverlay(region);
        });
        task.addEventHandler(WORKER_STATE_FAILED,value -> {
            workbench.hideOverlay(region);
        });
        task.addEventHandler(WORKER_STATE_CANCELLED,value -> {
            workbench.hideOverlay(region);
        });
        //启动任务
        new Thread(task).start();
    }

}
