package cn.dc.datajpa.bean;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "student")
@Data
public class Student {
    @Id
    private int sno;
    private String sname;
    private String ssex;
    private String sbirthday;
    @Column(name = "class")
    private String sclass;
}
