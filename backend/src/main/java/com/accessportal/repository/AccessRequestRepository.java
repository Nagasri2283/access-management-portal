package com.accessportal.repository;

import com.accessportal.entity.AccessRequest;
import com.accessportal.entity.AccessStatus;
import com.accessportal.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccessRequestRepository extends JpaRepository<AccessRequest, Long> {
    List<AccessRequest> findByRequesterOrderByCreatedAtDesc(User requester);
    List<AccessRequest> findByStatusOrderByCreatedAtDesc(AccessStatus status);
    List<AccessRequest> findAllByOrderByCreatedAtDesc();
}
