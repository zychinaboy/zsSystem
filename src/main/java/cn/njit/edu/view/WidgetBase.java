package cn.njit.edu.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.Region;

import java.io.IOException;
import java.util.function.Supplier;

public class WidgetBase implements UIObject {
    protected Parent root = null;

    public <T> T $(String id, Class<T> clazz) {
        if (root == null) {
            return null;
        }
        return (T) root.lookup("#" + id);
    }

    public WidgetBase(String fxmlFile) {
        try {
            root = FXMLLoader.load(getClass().getResource(fxmlFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public WidgetBase(Supplier<Parent> supplier) {
        root = supplier.get();
    }

    @Override
    public void initializeParts() {

    }

    @Override
    public void layoutParts() {

    }

    public Parent root() {
        return root;
    }
    public Region asRegion() {
        return (Region)root;
    }

}
