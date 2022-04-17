package cn.njit.edu;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPrintable;
import org.apache.pdfbox.printing.Scaling;

import javax.print.PrintService;
import javax.print.attribute.Attribute;
import javax.print.attribute.AttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.standard.Sides;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;

public class JacobPrintDemo {
    public static void main(String[] args) throws Exception {
        String filePath = "F:\\626.docx";//文件路径
        String toFilePath = "F:\\666.pdf";//文件路径
        String printerName = "TOSHIBA e-STUDIO457-03958646";//打印机名包含字串
        printWord(filePath, printerName);
        //  wordToPDF(filePath,toFilePath);
        //  PDFprint(new File(toFilePath),null);
    }

    public static void printWord(String filePath, String printerName) {
//        初始化线程
        ComThread.InitSTA();
        ActiveXComponent word = new ActiveXComponent("Word.Application");
        //设置打印机名称
        word.setProperty("ActivePrinter", new Variant(printerName));
        // 这里Visible是控制文档打开后是可见还是不可见，若是静默打印，那么第三个参数就设为false就好了
          Dispatch.put(word, "Visible", new Variant(true));
        // 获取文档属性
        Dispatch document = word.getProperty("Documents").toDispatch();
        // 打开激活文挡
        Dispatch doc = Dispatch.call(document, "Open", filePath).toDispatch();
        //打印预览
        doc.call(doc, "PrintPreview");

        //打印对话框
//        Dispatch oDialogs = word.getProperty("Dialogs").toDispatch();
//        Dispatch oDialog = Dispatch.call(oDialogs, "Item", "88").toDispatch();
//        oDialog.call(oDialog, "Display");


        try {
            Dispatch.callN(doc, "PrintOut");
            System.out.println("打印成功！");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("打印失败");
        } finally {
            try {
                if (doc != null) {
                    Dispatch.call(doc, "Close", new Variant(0));//word文档关闭
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            //退出
            word.invoke("Quit", new Variant[0]);
            //释放资源
            ComThread.Release();
            ComThread.quitMainSTA();
        }
    }

    public static void wordToPDF(String sFilePath, String toFilePath) {
        System.out.println("启动 Word...");
        long start = System.currentTimeMillis();
        ActiveXComponent app = null;
        Dispatch doc = null;
        try {
            app = new ActiveXComponent("Word.Application");
            app.setProperty("Visible", new Variant(false));
            Dispatch docs = app.getProperty("Documents").toDispatch();
            doc = Dispatch.call(docs, "Open", sFilePath).toDispatch();
            System.out.println("打开文档:" + sFilePath);
            System.out.println("转换文档到 PDF:" + toFilePath);
            File tofile = new File(toFilePath);
            if (tofile.exists()) {
                tofile.delete();
            }
            Dispatch.call(doc, "SaveAs", toFilePath, // FileName
                    17);//17是pdf格式
            long end = System.currentTimeMillis();
            System.out.println("转换完成..用时：" + (end - start) + "ms.");

        } catch (Exception e) {
            System.out.println("========Error:文档转换失败：" + e.getMessage());
        } finally {
            Dispatch.call(doc, "Close", false);
            System.out.println("关闭文档");
            if (app != null)
                app.invoke("Quit", new Variant[]{});
        }
        // 如果没有这句话,winword.exe进程将不会关闭
        ComThread.Release();
    }


    public static void PDFprint(File file, String printerName) throws Exception {
        PDDocument document = null;
        try {
            document = PDDocument.load(file);
            PrinterJob printJob = PrinterJob.getPrinterJob();

            printJob.setJobName(file.getName());
            if (printerName != null) {
                // 查找并设置打印机
                //获得本台电脑连接的所有打印机
                PrintService[] printServices = PrinterJob.lookupPrintServices();
                if (printServices == null || printServices.length == 0) {
                    System.out.print("打印失败，未找到可用打印机，请检查。");
                    return;
                }
                PrintService printService = null;
                //匹配指定打印机
                for (int i = 0; i < printServices.length; i++) {
                    System.out.println(printServices[i].getName());
                    if (printServices[i].getName().contains(printerName)) {
                        printService = printServices[i];
                        break;
                    }
                }
                if (printService != null) {
                    printJob.setPrintService(printService);
                    AttributeSet attributes = printService.getAttributes();
                    int queue;
                    for (Attribute attribute : attributes.toArray()) {
                        String name = attribute.getName();
                        String value = attributes.get(attribute.getClass()).toString();
                        if (name.equals("queued-job-count")) {
                            queue = Integer.parseInt(value);
                            System.out.println("queued-job-count = " + queue);
                            break;
                        }


                    }

                } else {
                    System.out.print("打印失败，未找到名称为" + printerName + "的打印机，请检查。");
                    return;
                }
            }
            //设置纸张及缩放
            PDFPrintable pdfPrintable = new PDFPrintable(document, Scaling.ACTUAL_SIZE);
            //设置多页打印
            Book book = new Book();
            PageFormat pageFormat = new PageFormat();
            //设置打印方向
            pageFormat.setOrientation(PageFormat.PORTRAIT);//纵向
            pageFormat.setPaper(getPaper());//设置纸张
            book.append(pdfPrintable, pageFormat, document.getNumberOfPages());
            printJob.setPageable(book);
            printJob.setCopies(1);//设置打印份数
            //添加打印属性
            HashPrintRequestAttributeSet pars = new HashPrintRequestAttributeSet();
            pars.add(Sides.DUPLEX); //设置单双页
            printJob.print(pars);
        } finally {
            if (document != null) {
                try {
                    document.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static Paper getPaper() {
        Paper paper = new Paper();
        // 默认为A4纸张，对应像素宽和高分别为 595, 842
        int width = 595;
        int height = 842;
        // 设置边距，单位是像素，10mm边距，对应 28px
        int marginLeft = 10;
        int marginRight = 0;
        int marginTop = 10;
        int marginBottom = 0;
        paper.setSize(width, height);
        // 下面一行代码，解决了打印内容为空的问题
        paper.setImageableArea(marginLeft, marginRight, width - (marginLeft + marginRight), height - (marginTop + marginBottom));
        return paper;
    }

}
