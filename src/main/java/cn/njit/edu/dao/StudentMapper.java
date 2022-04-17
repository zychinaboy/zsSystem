package cn.njit.edu.dao;

import cn.njit.edu.entity.Student;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface StudentMapper {
    List<Student> listAll();
    List<Student> filterBySm(@Param("sm")  String sm);
    List<String>  getUniqueSM();
}
