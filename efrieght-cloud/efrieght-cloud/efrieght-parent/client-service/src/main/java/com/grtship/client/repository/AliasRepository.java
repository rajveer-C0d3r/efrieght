package com.grtship.client.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.grtship.client.domain.Alias;

/**
 * Spring Data  repository for the Alias entity.
 */
@Repository
public interface AliasRepository extends JpaRepository<Alias, Long> {
}
