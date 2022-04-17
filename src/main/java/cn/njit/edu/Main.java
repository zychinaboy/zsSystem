package cn.njit.edu;

import cn.njit.edu.view.MainStageView;
import cn.njit.edu.view.MySplashScreen;
import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

@SpringBootApplication
@MapperScan("cn.njit.edu.dao")
public class Main extends AbstractJavaFxApplicationSupport {

    public static void main(String[] args) {
        launch(Main.class, MainStageView.class, new MySplashScreen(), args);

    }

    @Override
    public void beforeInitialView(final Stage stage, final ConfigurableApplicationContext ctx) {
        stage.setTitle("招生打印");
        stage.setMinHeight(600);
        stage.setMinWidth(500);
        stage.setMaximized(true);
        File output = new File("output");
        if(output.exists() == false){
            output.mkdir();
        }
    }

    @Override
    public Collection<Image> loadDefaultIcons() {

        return new ArrayList<>();
    }
}
