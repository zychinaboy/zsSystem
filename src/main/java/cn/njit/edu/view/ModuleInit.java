package cn.njit.edu.view;

import com.dlsc.workbenchfx.model.WorkbenchModule;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import javafx.beans.property.SimpleBooleanProperty;


public abstract class ModuleInit extends WidgetBase {

    protected Module workbenchModule;

    public ModuleInit(String fxmlFile, String moduleName, MaterialDesignIcon icon) {
        super(fxmlFile);
        workbenchModule = new Module(moduleName, icon, this.root());
        SimpleBooleanProperty activeProperty = workbenchModule.getActiveProperty();
        activeProperty.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                init();
            }
        });


    }


    public WorkbenchModule module() {
        return workbenchModule;
    }
}
