package com.grtship.account.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.grtship.account.domain.TdsType;

/**
 * Spring Data repository for the TdsType entity.
 */

@Repository
public interface TdsTypeRepository extends JpaRepository<TdsType, Long> {

}
