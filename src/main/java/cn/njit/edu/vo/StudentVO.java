package cn.njit.edu.vo;


import cn.njit.edu.entity.Student;
import javafx.beans.property.SimpleBooleanProperty;
import lombok.Data;

@Data
public class StudentVO {

    private final String xm;

    private final SimpleBooleanProperty selected;
    private final int    num;
    private final String sno;
    private final String ksh;
    private final String sm;
    private final String lqzy;
    private final String zymc;
    private final String xbmc;
    private final String xznx;
    private final String sfzh;

    private Student student;


    public StudentVO(Student student, boolean chose,int num) {
        this.sno = student.getSNO();
        this.xm = student.getXM();
        this.ksh = student.getKSH();
        this.sm = student.getSM();
        this.lqzy = student.getLQZY();
        this.zymc = student.getZYMC();
        this.xbmc = student.getXBMC();
        this.xznx = student.getXZNX();
        this.sfzh = student.getSFZH();
        this.student = student;
        this.num = num;
        this.selected = new SimpleBooleanProperty(chose);
    }


    public boolean isSelected() {
        return selected.get();
    }

    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }

    public SimpleBooleanProperty selectedProperty() {
        return selected;
    }

    public Student getStudent() {
        return student;
    }
}
