package com.grtship.authorisation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.grtship.authorisation.domain.ObjectApprovalAction;

@Repository
public interface ObjectApprovalActionRepository extends JpaRepository<ObjectApprovalAction,Long> {

}
