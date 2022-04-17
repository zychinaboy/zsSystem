package cn.njit.edu.module.print;


import cn.njit.edu.dao.StudentMapper;
import cn.njit.edu.entity.PrinterConfig;
import cn.njit.edu.entity.Student;
import cn.njit.edu.task.PreviewTask;
import cn.njit.edu.task.PrintTask;
import cn.njit.edu.task.PrintTaskResult;
import cn.njit.edu.template.DocBuilder;
import cn.njit.edu.template.DocPrinter;
import cn.njit.edu.view.*;
import cn.njit.edu.vo.StudentVO;
import com.dlsc.workbenchfx.model.WorkbenchDialog;
import com.dlsc.workbenchfx.view.controls.ToolbarItem;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Region;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 毕业生模块
 */

@Component
@Slf4j
public class PrintModule extends ModuleInit implements IPrintMethod {
    private static String FXML = "/fxml/main.fxml";
    private static String All_SM = "<全部>";

    @Value("${print.wordtpl}")
    private String wordtpl;
    @Autowired
    private DocBuilder docBuilder;
    @Autowired
    private DocPrinter printer;

    private PrintEventDefine eventDefine;
    @Autowired
    private PrintView view;

    @Autowired
    private PrinterConfig printerConfig;
    private StudentMapper dao;
    private ObservableList<StudentVO> viewTableData;

    private ArrayList<StudentVO> stdList = new ArrayList<StudentVO>();
    ;

    public PrintModule(StudentMapper dao) {
        super(FXML, "打印录取通知书", MaterialDesignIcon.PRINTER_3D);
        this.dao = dao;
    }


    @Override
    public void initializeParts() {
        //  this.view = new PrintView(this);
        this.view.initUI();
        this.setSmItems();
        this.setPrinters();
        this.eventDefine = new PrintEventDefine(this, this);
    }


    private void loadData(String sm) {
        this.view.disableSearchCtrl(true);
        this.stdList.clear();

        AtomicInteger i = new AtomicInteger(1);
        this.dao.filterBySm(sm).stream().forEach(it -> {
            StudentVO data = new StudentVO(it, false, i.get());
            i.set(i.get() + 1);
            stdList.add(data);
        });
        this.viewTableData = FXCollections.observableArrayList(stdList);
        this.view.setItems(this.viewTableData);

    }


    @Override
    public void setupEventHandlers() {
        ToolbarItem preview = new ToolbarItem("预览", new MaterialDesignIconView(MaterialDesignIcon.EYE));
        ToolbarItem print = new ToolbarItem("打印通知书", new MaterialDesignIconView(MaterialDesignIcon.PRINTER));
        workbenchModule.getToolbarControlsLeft().addAll(preview, print);
        this.eventDefine.doEventPrintTzs(print);
        this.eventDefine.doEventPreview(preview);

    }

    @Override
    public void chooseAll() {
        this.viewTableData.forEach(it -> it.setSelected(true));
    }

    @Override
    public void clearAll() {
        this.viewTableData.forEach(it -> it.setSelected(false));
    }

    @Override
    public void filterBySm(String sm) {
        if (All_SM.equals(sm)) {
            sm = null;
        }
        loadData(sm);
    }

    @Override
    public void setSmItems() {
        //设置SM
        List<String> uniqueSM = this.dao.getUniqueSM();
        uniqueSM.add(0, All_SM);
        this.view.setSmItems(FXCollections.observableArrayList(uniqueSM));
    }

    @Override
    public void setPrinters() {
        // this.view.setPrinters(FXCollections.observableArrayList(printer.getPrinterNames()));
    }


    @Override
    public void preview() {
        ObservableList selectItem = this.view.getSelectItem();

        if (selectItem.isEmpty()) {
            this.workbenchModule.getWorkbench().showInformationDialog("提示","请选择一条数据",null);
            return;
        }
        Student student = ((StudentVO) selectItem.get(0)).getStudent();
        WaitDialog waitDialog = new WaitDialog(this.workbenchModule.getWorkbench());
        PreviewTask previewTask = new PreviewTask(docBuilder, student, wordtpl);
        waitDialog.waitFor(previewTask);
        previewTask.setOnSucceeded(state -> {
            PreviewDialog previewDialog = new PreviewDialog();
            previewDialog.showImage((WritableImage) state.getSource().getValue());
            this.workbenchModule.getWorkbench().showOverlay(
                    (Region) previewDialog.root(),
                    false
            );

        });
        previewTask.setOnFailed(state -> {
            String message = previewTask.exceptionProperty().getValue().getMessage();
            this.workbenchModule.getWorkbench().showInformationDialog("失败", message, null);
        });
    }

    public void print() {
        if (this.viewTableData.isEmpty()) {
            return;
        }
        PrintSettingDialog settingDialog = new PrintSettingDialog(this.stdList.size(), printerConfig);
        WorkbenchDialog dialog = WorkbenchDialog.builder(
                "打印设置", settingDialog.root(), ButtonType.OK, ButtonType.CANCEL)
                .blocking(true)
                .build();

        dialog.setOnResult(buttonType -> {
            if (buttonType == ButtonType.OK) {
                int fromPage = settingDialog.getFromPage();
                int endPage = settingDialog.getEndPage();
                if(fromPage <= endPage){
                    printTzs(this.stdList.subList(fromPage - 1, endPage));
                }

            }
        });

        this.workbenchModule.getWorkbench().showDialog(dialog);
    }


    private void printTzs(List<StudentVO> filtered) {

        this.workbenchModule.getWorkbench().showConfirmationDialog("请确认", "确定开始打印吗？", buttonType -> {
            if (buttonType.getButtonData() == ButtonBar.ButtonData.YES) {
                List<Student> list = new ArrayList();
                filtered.forEach(it -> {
                    list.add(it.getStudent());
                });

                PrintTask printTask = new PrintTask(printer, docBuilder, list, wordtpl);
                PrintDialog waitDialog = new PrintDialog(this.workbenchModule.getWorkbench());
                waitDialog.waitFor(printTask);
                printTask.setOnSucceeded(state -> {
                    PrintTaskResult result = (PrintTaskResult) state.getSource().getValue();
                    String msg = String.format("总数：%d，成功：%d，失败：%d ", result.total, result.succ, result.total - result.succ);
                    this.workbenchModule.getWorkbench().showInformationDialog("打印结果统计", msg, null);

                });
                printTask.setOnFailed(state -> {
                    this.workbenchModule.getWorkbench().showInformationDialog("失败", "message", null);
                });
            }

        });

    }

}
