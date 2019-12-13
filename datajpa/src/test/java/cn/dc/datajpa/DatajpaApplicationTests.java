package cn.dc.datajpa;

import cn.dc.datajpa.bean.Student;
import cn.dc.datajpa.dao.StuDao;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

@SpringBootTest
class DatajpaApplicationTests {
    @Autowired
    StuDao stuDao;

    @Test
    void contextLoads() {
        Logger logger = LoggerFactory.getLogger(DatajpaApplicationTests.class);
        /*
        官方提供的和自定义的方法都可以用，
            findAll是默认提供的方法
            findList是自定义的方法
         */
        List<Student> studentList = stuDao.findAll();
        logger.error(String.format("%1$s", stuDao.findList(108)));
        /*
        dataJpa实现 ： 分页 + 排序
            findAll方法2个参数
                args0 Specification，用来封装查询条件
                args1 Pageable类型
                    其实现类一般用PageRequest，直接用静态方法返回，其构造器是protected的，不能直接用。
                    of方法构建时可以传入Sort对象，Sort对象通过静态的by方法构建
         */
        Page<Student> studentPage = stuDao.findAll(
                new Specification<Student>() {
                    @Override
                    public Predicate toPredicate(Root<Student> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                        return null;
                    }
                },
                PageRequest.of(0, 5, Sort.by("sclass")));

        System.out.println(studentPage.getContent());
        System.out.println(studentPage.getTotalPages());

    }
}
