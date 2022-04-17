package cn.njit.edu.task;

import cn.njit.edu.entity.Student;
import cn.njit.edu.template.DocBuilder;
import cn.njit.edu.template.DocPrinter;
import javafx.concurrent.Task;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.List;

@Slf4j
public class PrintTask extends Task<PrintTaskResult> {

    private final DocPrinter printer;
    private final DocBuilder docBuilder;
    private final List<Student> students;
    private final String wordtpl;
    private boolean bStop = false;

    public PrintTask(DocPrinter printer, DocBuilder docBuilder, List<Student> studentList, String wordtpl) {
        this.printer = printer;
        this.docBuilder = docBuilder;
        this.students = studentList;
        this.wordtpl = wordtpl;
    }

    @Override
    protected PrintTaskResult call() throws Exception {
        PrintTaskResult printTaskResult = new PrintTaskResult();

        int size = students.size();
        printTaskResult.total = size;
        this.updateProgress(0, size);
        for (int i = 0; i < size; i++) {
            boolean queuedJobOverhead = printer.isQueuedJobOverhead();
            while (bStop || queuedJobOverhead) {
                if (bStop) {
                    Thread.sleep(500);
                    continue;
                }
                this.updateMessage(String.format("打印队列过长，稍后继续..."));
                Thread.sleep(1000 * 3);
                this.updateMessage(String.format("检测打印队列..."));
                queuedJobOverhead = printer.isQueuedJobOverhead();
            }
            Student student = students.get(i);
            this.updateMessage(String.format("正在打印: %s-%s", student.getKSH(), student.getXM()));
            String docPath = docBuilder.genWord(wordtpl, student);
            printer.printWord(docPath);
            this.updateProgress(i + 1, size);
            printTaskResult.succ +=1;
            safeDeleteFile(new File(docPath));
        }
        this.updateMessage("打印完成");
        return printTaskResult;
    }

    public void pause() {
        bStop = true;
    }

    public void resume() {
        bStop = false;
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
}
