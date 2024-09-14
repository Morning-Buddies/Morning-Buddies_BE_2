package com.ghpg.morningbuddies.domain.group.repository;

import com.ghpg.morningbuddies.domain.group.entity.GroupJoinRequest;
import com.ghpg.morningbuddies.domain.group.entity.Groups;
import com.ghpg.morningbuddies.domain.group.entity.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupJoinRequestRepository extends JpaRepository<GroupJoinRequest, Long> {

    List<GroupJoinRequest> findByGroupAndStatus(Groups group, RequestStatus status);


}
