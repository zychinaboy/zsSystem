package cn.njit.edu.template;

import cn.njit.edu.entity.Student;
import com.deepoove.poi.XWPFTemplate;
import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class DocBuilder {
    @Value("${print.photo.width}")
    private int photoWidth;
    @Value("${print.photo.height}")
    private int photoHeight;

    @Value("${print.photo.path}")
    private String photoPath;

    //照片路径缓存
    private Map<String, String> photoCache = new HashMap<>();

    @PostConstruct
    public void loadPhotos() {
        File dir = new File(photoPath);
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();

            for (int i = 0; i < files.length; i++) {
                photoCache.put(FilenameUtils.getBaseName(files[i].getName()), files[i].getAbsolutePath());
            }
        } else {
            log.error("无效的照片路径：" + photoPath);
        }

    }

    private String findPhoto(String ksh) {
        if (photoCache.containsKey(ksh)) {
            return photoCache.get(ksh);
        } else {
            log.error("!!无照片!! KSH=" + ksh);
        }

        return "";
    }

    //输出录取通知书
    public String genWord(String wordtpl, Student student) {

        try {
            HashMap<String, Object> map = new HashMap();
            map.put("SNO", student.getSNO());
            map.put("SM", student.getSM());
            map.put("ZKZH", student.getZKZH());
            map.put("XM", student.getXM());
            String nz = student.getXZNX();
            if(NumberUtils.isNumber(nz) && NumberUtils.toInt(nz) == 2){
                map.put("BK", "专科起点本科");
            }else {
                map.put("BK", "本科");
            }
            map.put("ZYMC", student.getZYMC());
            map.put("XBMC", student.getXBMC());
            map.put("LQZY", student.getLQZY());
            map.put("SFZH", student.getSFZH());
            map.put("KSH", student.getKSH());
            map.put("SJR", student.getSJR());
            map.put("YZY", student.getYZY());
            map.put("XZNX", student.getXZNX());
            map.put("JTDZ", student.getJTDZ());
            map.put("YZBM", student.getYZBM());
            map.put("LXDH", student.getLXDH());
            map.put("BDDD", student.getBDDD());
            map.put("XBDM", student.getXBDM());
            map.put("CSNY", student.getCSNY());

            String photo = findPhoto(student.getKSH());

            String extension = FilenameUtils.getExtension(photo);
            File fs = new File(photo);
            EmbedImage pictureRenderData = new EmbedImage(photoWidth, photoHeight, fs);
            map.put("photo", pictureRenderData);

            XWPFTemplate template = XWPFTemplate.compile(wordtpl).render(map);

            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHmsS");
            String outputFile = String.format("%s/output/%s_%s_%s.docx",
                    System.getProperty("user.dir"), student.getSNO(), student.getXM(), LocalDateTime.now().format(dateTimeFormatter));
            FileOutputStream out = new FileOutputStream(outputFile);
            template.write(out);
            out.flush();
            out.close();
            template.close();
            return outputFile;
        } catch (Exception e) {
            log.error("输出失败！！！" + e.getMessage());
            return null;
        }
    }

    public void wordToPDF(String sFilePath, String toFilePath) {

        long start = System.currentTimeMillis();
        ActiveXComponent app = null;
        Dispatch doc = null;
        try {
            app = new ActiveXComponent("Word.Application");
            app.setProperty("Visible", new Variant(false));
            Dispatch docs = app.getProperty("Documents").toDispatch();
            doc = Dispatch.call(docs, "Open", sFilePath).toDispatch();
            log.info("打开文档:" + sFilePath);
            log.info("转换文档到 PDF:" + toFilePath);
            File tofile = new File(toFilePath);
            if (tofile.exists()) {
                tofile.delete();
            }
            Dispatch.call(doc, "SaveAs", toFilePath, 17);//17是pdf格式
            long end = System.currentTimeMillis();
            log.info("转换完成..用时：" + (end - start) + "ms.");

        } catch (Exception e) {
            log.error("========Error:文档转换失败：" + e.getMessage());
        } finally {
            Dispatch.call(doc, "Close", false);
            log.info("关闭文档");
            if (app != null)
                app.invoke("Quit", new Variant[]{});
        }
        // 如果没有这句话,winword.exe进程将不会关闭
        ComThread.Release();
    }

}
