package com.connectit.core.repository;

import com.connectit.core.model.AssignmentGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AssignmentGroupRepository extends JpaRepository<AssignmentGroup, Long> {
    Optional<AssignmentGroup> findByName(String name);
}
