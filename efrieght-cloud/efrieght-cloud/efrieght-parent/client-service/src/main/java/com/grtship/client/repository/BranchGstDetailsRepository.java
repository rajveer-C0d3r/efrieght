package com.grtship.client.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.grtship.client.domain.BranchGstDetails;

/**
 * Spring Data  repository for the BranchGstDetails entity.
 */
@Repository
public interface BranchGstDetailsRepository extends JpaRepository<BranchGstDetails, Long> {
}
