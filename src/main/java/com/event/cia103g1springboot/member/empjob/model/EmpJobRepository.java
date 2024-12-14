package com.event.cia103g1springboot.member.empjob.model;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface EmpJobRepository extends JpaRepository<EmpJobVO, EmpJobId> {

	@Query(value = "select funId.funId from EmpJobVO ej where ej.empId.empId=?1")
	List<Integer> findByEmpId(Integer empId);

	@Modifying
	@Transactional
	@Query(value = "delete from EmpJobVO ej where ej.empId.empId=?1 and ej.funId.funId = ?2")
	void deleteSelectedAuth(Integer empId, Integer funId);

	@Modifying
	@Transactional
	@Query(value = "delete from EmpJobVO ej where ej.empId.empId=?1")
	void deleteAllOldAuth(Integer empId);
}
