package com.ghpg.morningbuddies.domain.group.repository;

import com.ghpg.morningbuddies.domain.group.entity.Groups;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GroupRepository extends JpaRepository<Groups, Long> {

    Optional<Groups> findByGroupName(String groupName);
}
