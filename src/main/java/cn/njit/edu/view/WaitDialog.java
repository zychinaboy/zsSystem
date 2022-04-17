package cn.njit.edu.view;


import com.dlsc.workbenchfx.Workbench;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import static javafx.concurrent.WorkerStateEvent.*;

public class WaitDialog<T> {
    private final Label progressInfo = new Label();
    private final HBox waitBox = new HBox();
    private final ProgressBar pb = new ProgressBar();
    private final Workbench workbench;
    private SimpleStringProperty status = new SimpleStringProperty();

    public WaitDialog(Workbench workbench) {

        this.workbench = workbench;
    }

    private void initUi(ReadOnlyStringProperty statusProperty, ReadOnlyDoubleProperty pbProperty){
        StackPane.setAlignment(waitBox, Pos.CENTER);
        waitBox.getChildren().clear();
        waitBox.setSpacing(5);
        waitBox.setAlignment(Pos.CENTER);
        waitBox.getChildren().addAll(pb, progressInfo);
        progressInfo.textProperty().bind(status);
        status.setValue("请稍候...");
        workbench.showOverlay(waitBox, true);
        status.bind(statusProperty);
        pb.progressProperty().bind(pbProperty);
    }

    public void waitFor(Task<T> task) {

        initUi(task.messageProperty(),task.progressProperty());

        task.addEventHandler(WORKER_STATE_SUCCEEDED,value -> {
            workbench.hideOverlay(waitBox);
        });
        task.addEventHandler(WORKER_STATE_FAILED,value -> {
            workbench.hideOverlay(waitBox);
        });
        task.addEventHandler(WORKER_STATE_CANCELLED,value -> {
            workbench.hideOverlay(waitBox);
        });
        //启动任务
        new Thread(task).start();

    }
    public void waitFor(Service<T> service) {
        initUi(service.messageProperty(),service.progressProperty());
        service.setOnSucceeded(value -> {
            workbench.hideOverlay(waitBox);
        });
        service.setOnFailed(value -> {
            workbench.hideOverlay(waitBox);
        });
        service.setOnCancelled(value -> {
            workbench.hideOverlay(waitBox);
        });

        workbench.showOverlay(waitBox, true);
        status.bind(service.messageProperty());
        pb.progressProperty().bind(service.progressProperty());
        //启动任务
        service.start();

    }

}
