package cn.njit.edu.controller;


import cn.njit.edu.dao.StudentMapper;
import cn.njit.edu.module.print.PrintModule;
import com.dlsc.workbenchfx.Workbench;
import com.dlsc.workbenchfx.model.WorkbenchModule;
import de.felixroske.jfxsupport.FXMLController;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import org.springframework.beans.factory.annotation.Autowired;

@FXMLController
public class WorkspaceController {

    @FXML
    private Workbench workbench;
    @Autowired
    private PrintModule printModule;

    public WorkspaceController() {
    }

    @FXML
    private void initialize() {
        workbench.setModulesPerPage(2);
        workbench.getModules().addAll(printModule.module(), new WorkbenchModule("待开发", MaterialDesignIcon.NEW_BOX) {
            @Override
            public Node activate() {
                return new Label("开发中...");
            }
        });
    }


}
