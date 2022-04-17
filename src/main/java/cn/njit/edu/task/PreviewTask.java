package cn.njit.edu.task;

import cn.njit.edu.entity.Student;
import cn.njit.edu.template.DocBuilder;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
public class PreviewTask extends Task<WritableImage>  {


    private final DocBuilder docBuilder;
    private final Student student;
    private final String wordtpl;

    public PreviewTask(DocBuilder docBuilder, Student student, String wordtpl) {
        this.docBuilder = docBuilder;
        this.student = student;
        this.wordtpl = wordtpl;
    }

    @Override
    protected WritableImage call() throws Exception {
        return doPreviewPdf();
    }

    private WritableImage doPreviewPdf() throws Exception {

        this.updateMessage("生成预览图片...");
        this.updateProgress(20,100);
        String docPath = docBuilder.genWord(wordtpl, student);
        if (docPath == null) {
            throw new Exception("预览失败,因为无法生成word文档。" );
        }
        this.updateProgress(50,100);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHmsS");
        String pdfPath = String.format("%s/output/%s.pdf", System.getProperty("user.dir"), LocalDateTime.now().format(dateTimeFormatter));
        docBuilder.wordToPDF(docPath, pdfPath);
        this.updateProgress(80,100);

        File file = new File(pdfPath);
        PDDocument doc = null;
        try {
            doc = PDDocument.load(file);
            PDFRenderer renderer = new PDFRenderer(doc);
            BufferedImage bufferedImage = renderer.renderImage(0);
            WritableImage image = new WritableImage(bufferedImage.getWidth(), bufferedImage.getHeight());
            this.updateProgress(100,100);
            return SwingFXUtils.toFXImage(bufferedImage, image);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw e;
        } finally {
            safeClosePdf(doc);
            safeDeleteFile(file);
           // safeDeleteFile(new File(docPath));
        }
    }

    private void safeDeleteFile(File file){
        try {
            if(file != null)
            {
                file.delete();
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
    private void safeClosePdf(PDDocument doc){
        try {
            if(doc != null)
            {
                doc.close();
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

}
