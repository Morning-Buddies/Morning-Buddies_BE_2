package com.ghpg.morningbuddies.domain.groups.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ghpg.morningbuddies.domain.groups.entity.GroupJoinRequest;
import com.ghpg.morningbuddies.domain.groups.entity.Groups;
import com.ghpg.morningbuddies.domain.groups.entity.enums.RequestStatus;

public interface GroupJoinRequestRepository extends JpaRepository<GroupJoinRequest, Long> {

	List<GroupJoinRequest> findByGroupAndStatus(Groups group, RequestStatus status);

}
