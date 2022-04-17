package cn.njit.edu.entity;

import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.List;
import java.util.Map;


@Configuration
public class PrinterConfig {

    @Value("${print.maxQueuedJobCount}")
    private int maxQueuedJobCount;

    private SimpleStringProperty printerNameProperty = new SimpleStringProperty();

    private SimpleListProperty printerNameListProperty = new SimpleListProperty();

    public PrinterConfig(@Value("${print.printerName}") String printerName){
        this.printerNameProperty.setValue(printerName);
        printerNameProperty.addListener((observable, oldValue, newValue) -> {
            if (!StringUtils.equals(oldValue, newValue)) {
               // updateYaml(newValue);
            }
        });
    }

    private void updateYaml(String val){
        try {
            DumperOptions dumperOptions = new DumperOptions();
            dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            dumperOptions.setDefaultScalarStyle(DumperOptions.ScalarStyle.PLAIN);
            dumperOptions.setPrettyFlow(false);
            Yaml yaml = new Yaml(dumperOptions);
            Map map =(Map)yaml.load(new FileInputStream("application.yml"));

            Map printMap = (Map) map.get("print");
            printMap.put("printerName",val);
            yaml.dump(map, new OutputStreamWriter(new FileOutputStream("application.yml")));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
    public SimpleListProperty getPrinterNameListProperty() {
        return printerNameListProperty;
    }


    public SimpleStringProperty getPrinterNameProperty() {
        return printerNameProperty;
    }


    public int getMaxQueuedJobCount() {
        return maxQueuedJobCount;
    }

    public void setPrinterNameList(List<String> printerNames) {
        this.printerNameListProperty.set(FXCollections.observableArrayList(printerNames));
    }
}
