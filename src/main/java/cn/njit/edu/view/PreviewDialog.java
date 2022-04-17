package cn.njit.edu.view;

import javafx.geometry.Pos;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public class PreviewDialog extends WidgetBase {
    private static String FXML = "/fxml/preview.fxml";
    private final ImageView imageView;
    private final Slider zoom;

    public PreviewDialog() {
        super(FXML);
        StackPane.setAlignment(this.root, Pos.CENTER);
        this.imageView = this.$("imageView", ImageView.class);
        this.zoom = this.$("zoom", Slider.class);
        this.zoom.valueProperty().addListener((observable, oldValue, newValue) -> {
            double ratio = 1.0 + newValue.doubleValue() / 100 * 2;
            this.imageView.setScaleX(ratio);
            this.imageView.setScaleY(ratio);
        });
    }

    public void showImage(Image image) {
        double height = image.getHeight();
        double width = image.getWidth();
        this.imageView.setFitHeight(height);
        this.imageView.setFitWidth(width);
        this.imageView.setImage(image);
    }

}
