package com.event.cia103g1springboot.member.systemFunction.model;

import com.event.cia103g1springboot.member.empjob.model.EmpJobVO;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "funId")
@EqualsAndHashCode(of ="funId" )
@AllArgsConstructor
@Data
@NoArgsConstructor
@Entity
@Table(name = "systemFunction")
public class SystemFunctionVO implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(updatable = false)
	private Integer funId;

	@NotEmpty(message = "系統名稱請勿空白")
	@Size(max = 20, message = "長度需在20個字以內")
	private String funName;

	@NotEmpty(message = "功能描述請勿空白")
	private String funDesc;
	
	   @OneToMany(mappedBy = "systemFunctionVO", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	    private Set<EmpJobVO> empJobs;
	}

