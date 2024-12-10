package com.event.cia103g1springboot.member.emp.model;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

import com.event.cia103g1springboot.member.empjob.model.EmpJobVO;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(of = "empId")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "employee")
public class EmployeeVO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "empId")
    private Integer empId;
    
    @Column(name = "empName", nullable = false, length = 30)
    private String empName;
    
    @Column(name = "empAcct", nullable = false, length = 50)
    private String empAcct;
    
    @Column(name = "empPwd", nullable = false, length = 100)
    private String empPwd;
    
    @Column(name = "empJobTitle", nullable = false, length = 50)
    private String empJobTitle;
    
    @Column(name = "hireDate")
    @Temporal(TemporalType.DATE)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date hireDate;
    
    @Column(name = "empStat", nullable = false)
    private Integer empStat;
    
    @Lob
    @Column(name = "empImg", columnDefinition = "mediumblob")
    private byte[] empImg;

    @OneToMany(mappedBy = "employee")
    private Set<EmpJobVO> empJobs;
}