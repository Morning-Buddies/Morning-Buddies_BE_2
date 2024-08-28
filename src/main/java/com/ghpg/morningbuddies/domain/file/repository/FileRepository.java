package com.ghpg.morningbuddies.domain.file.repository;

import com.ghpg.morningbuddies.domain.file.entity.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends JpaRepository<FileEntity, Long> {
}
