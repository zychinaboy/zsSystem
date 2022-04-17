package cn.njit.edu.view;

import com.dlsc.workbenchfx.model.WorkbenchModule;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;

public class Module extends WorkbenchModule {

    private final Node view;


    public SimpleBooleanProperty getActiveProperty() {
        return activeProperty;
    }

    //激活时触发事件
    private SimpleBooleanProperty activeProperty = new SimpleBooleanProperty();

    public Module(String moduleName, MaterialDesignIcon icon, Node view) {
        super(moduleName, icon);
        this.view = view;
    }


    @Override
    public Node activate() {
        activeProperty.setValue(true);
        return view;
    }

}
