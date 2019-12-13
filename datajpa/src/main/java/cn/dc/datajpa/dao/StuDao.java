package cn.dc.datajpa.dao;

import cn.dc.datajpa.bean.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StuDao extends JpaRepository<Student,String>, JpaSpecificationExecutor<Student> {
    @Query(value="select * from student where sno = ?",nativeQuery=true)
    public List<String> findList(int sno);
}
