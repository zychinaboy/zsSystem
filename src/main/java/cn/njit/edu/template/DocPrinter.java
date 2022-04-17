package cn.njit.edu.template;

import cn.njit.edu.entity.PrinterConfig;
import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.Attribute;
import javax.print.attribute.AttributeSet;
import java.awt.print.PrinterJob;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class DocPrinter {

    private PrinterConfig printerConfig;
    private PrintService printService = null;

    public DocPrinter(PrinterConfig printerConfig) {
        this.printerConfig = printerConfig;
        this.printerConfig.setPrinterNameList(getPrinterNames());
        printerConfig.getPrinterNameProperty().addListener((observable, oldValue, newValue) -> {
            if (!StringUtils.equals(oldValue, newValue)) {
                lookupPrinter(newValue);
            }
        });
    }

    private List<String> getPrinterNames() {
        ArrayList<String> list = new ArrayList<>();
        // 查找并设置打印机
        PrintService[] printServices = PrinterJob.lookupPrintServices();
        if (printServices == null || printServices.length == 0) {
            log.error("打印失败，未找到可用打印机，请检查。");

        } else {
            for (int i = 0; i < printServices.length; i++) {
                list.add(printServices[i].getName());
            }
        }
        return list;
    }

    private void lookupPrinter(String printerName) {
        log.info(String.format("设置打印机:%s", printerName));
        // 查找并设置打印机
        PrintService[] printServices = PrinterJob.lookupPrintServices();
        if (printServices == null || printServices.length == 0) {
            log.error("打印失败，未找到可用打印机，请检查。");
            return;
        }
        if (StringUtils.isEmpty(printerName)) {
            printService = PrintServiceLookup.lookupDefaultPrintService();
        } else {
            for (int i = 0; i < printServices.length; i++) {
                String name = printServices[i].getName();
                if (name.equalsIgnoreCase(printerName)) {
                    printService = printServices[i];
                }
            }
        }


    }


    public void printWord(String filePath) {
        Optional<WordActiveXWrap> wordActiveXWrap = openWordDocument(filePath, false);
        wordActiveXWrap.map(wrap -> {
            try {
                //设置打印机名称
                String activePrinter = printerConfig.getPrinterNameProperty().getValue();
                if (activePrinter != null) {
                    wrap.wordComp.setProperty("ActivePrinter", new Variant(activePrinter));
                }
                Dispatch.callN(wrap.dispatch, "PrintOut");
                log.info("打印成功！" + filePath);
            } catch (Exception e) {
                e.printStackTrace();
                log.error("打印失败" + filePath);
            }
            return wrap;
        });
        releaseWord(wordActiveXWrap.orElseGet(null));

    }

    //是否打印缓存队列过长
    public boolean isQueuedJobOverhead() {
        int queue = 0;
        if (printService != null) {
            AttributeSet attributes = printService.getAttributes();

            for (Attribute attribute : attributes.toArray()) {
                String name = attribute.getName();
                String value = attributes.get(attribute.getClass()).toString();
                if (name.equals("queued-job-count")) {
                    queue = Integer.parseInt(value);
                    break;
                }


            }

        }
        return queue > printerConfig.getMaxQueuedJobCount();
    }

    private Optional<WordActiveXWrap> openWordDocument(String filePath, boolean visible) {
        Optional<WordActiveXWrap> opWordApp = Optional.empty();
        Dispatch doc = null;
        //        初始化线程
        ComThread.InitSTA();
        ActiveXComponent word = new ActiveXComponent("Word.Application");
        try {
            // 这里Visible是控制文档打开后是可见还是不可见，若是静默打印，那么第三个参数就设为false就好了
            Dispatch.put(word, "Visible", new Variant(visible));
            // 获取文档属性
            Dispatch document = word.getProperty("Documents").toDispatch();
            // 打开激活文挡
            doc = Dispatch.call(document, "Open", filePath).toDispatch();
            WordActiveXWrap wordApp = new WordActiveXWrap();
            wordApp.wordComp = word;
            wordApp.dispatch = doc;
            opWordApp = Optional.of(wordApp);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return opWordApp;
    }

    private void releaseWord(WordActiveXWrap app) {
        if (app == null) {
            return;
        }
        try {
            if (app.dispatch != null) {
                Dispatch.call(app.dispatch, "Close", new Variant(0));//word文档关闭
            }
        } catch (Exception e2) {
            e2.printStackTrace();
            log.error(e2.getMessage());
        } finally {
            //退出
            app.wordComp.invoke("Quit", new Variant[0]);
            //释放资源
            ComThread.Release();
            ComThread.quitMainSTA();
        }

    }
}
