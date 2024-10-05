package com.grtship.authorisation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.grtship.authorisation.domain.ObjectApprovalData;

@Repository
public interface ObjectApprovalDataRepository extends JpaRepository<ObjectApprovalData,Long> {

}
