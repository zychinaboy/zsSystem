package cn.njit.edu.module.print;


import cn.njit.edu.vo.StudentVO;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import org.springframework.stereotype.Component;

@Component
public class PrintView {

    private ProgressBar pb;
    private Label progressInfo;
    private PrintModule module;

    private TableView table;
    private HBox statusBar;
    private ComboBox cbSm;
    private Label lb_total;

    private SimpleStringProperty rowCount = new SimpleStringProperty();


    public void setItems(ObservableList<StudentVO> viewTableData) {
        this.table.setItems(viewTableData);
        rowCount.setValue(String.format("%d", viewTableData.size()));
    }

    //设置省份下拉列表
    public void setSmItems(ObservableList<String> cmList) {
        this.cbSm.setItems(cmList);
    }


    public void reset() {
        this.table.setItems(null);
    }

    public ObservableList getSelectItem() {
        return this.table.getSelectionModel().getSelectedItems();
    }

    //列名枚举
    private enum TableColumnEnum {
        NUM,
        SNO,
        SM,
        KSH,
        XM,
        LQZY,
        ZYMC,
        XBMC,
        XZNX,
        SFZH,
    }

    public PrintView(PrintModule module) {
        this.module = module;
    }

    public void initUI() {
        this.cbSm = this.module.$("cbSM", ComboBox.class);
        this.cbSm = this.module.$("cbSM", ComboBox.class);
        this.lb_total = this.module.$("lb_total", Label.class);
        this.pb = this.module.$("pb", ProgressBar.class);
        this.progressInfo = this.module.$("statusInfo", Label.class);
        this.table = this.module.$("table", TableView.class);
        table.setEditable(true);
        setTableColumnProperty(table, TableColumnEnum.NUM, "num");
        setTableColumnProperty(table, TableColumnEnum.SNO, "sno");
        setTableColumnProperty(table, TableColumnEnum.SM, "sm");
        setTableColumnProperty(table, TableColumnEnum.KSH, "ksh");
        setTableColumnProperty(table, TableColumnEnum.XM, "xm");
        setTableColumnProperty(table, TableColumnEnum.LQZY, "lqzy");
        setTableColumnProperty(table, TableColumnEnum.ZYMC, "zymc");
        setTableColumnProperty(table, TableColumnEnum.XBMC, "xbmc");
        setTableColumnProperty(table, TableColumnEnum.XZNX, "xznx");
        setTableColumnProperty(table, TableColumnEnum.SFZH, "sfzh");

        //状态栏
        this.statusBar = this.module.$("statusBar", HBox.class);
        this.statusBar.setVisible(false);
        //事件
        this.cbSm.valueProperty().addListener((ChangeListener<String>) (ov, oldValue, newValue) -> {
            this.module.filterBySm(newValue);
        });
        this.lb_total.textProperty().bind(rowCount);

    }


    private void setTableColumnProperty(TableView table, TableColumnEnum col, String name) {
        ((TableColumn) table.getColumns().get(col.ordinal())).setCellValueFactory(new PropertyValueFactory<>(name));
    }

    public void bind(ReadOnlyDoubleProperty progress, ReadOnlyStringProperty info) {
        pb.progressProperty().bind(progress);
        progressInfo.textProperty().bind(info);
        statusBar.setVisible(true);
    }


    public void disableSearchCtrl(boolean flag) {

    }

}
