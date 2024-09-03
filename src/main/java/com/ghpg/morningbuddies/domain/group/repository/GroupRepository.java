package com.ghpg.morningbuddies.domain.group.repository;

import java.time.LocalTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ghpg.morningbuddies.domain.group.entity.Groups;
import org.springframework.web.bind.annotation.RequestParam;

public interface GroupRepository extends JpaRepository<Groups, Long> {

	Optional<Groups> findByGroupName(String groupName);

	@Query("select g from Groups g where g.groupName like %:keyword% or g.description like %:keyword%")
	Page<Groups> findByGroupNameOrDescriptionContaining(@Param("keyword") String keyword, Pageable pageable);

	// 핫한 그룹 기준
	@Query("SELECT g FROM Groups g ORDER BY g.successCount DESC")
	Page<Groups> getHotGroups(Pageable pageable);

	// 일찍 일어나는 그룹 기준
	@Query("SELECT g FROM Groups g WHERE g.wakeupTime <= :time")
	Page<Groups> getGroupsByEarlyMorning(@RequestParam("time") LocalTime time, Pageable pageable);
}
