package cn.njit.edu.module.print;

import cn.njit.edu.vo.StudentVO;

import java.util.List;

public interface IPrintMethod {

    void chooseAll();

    void clearAll();

    void filterBySm(String sm);

    void setSmItems();

    void setPrinters();

    void print();

    void preview();
}
