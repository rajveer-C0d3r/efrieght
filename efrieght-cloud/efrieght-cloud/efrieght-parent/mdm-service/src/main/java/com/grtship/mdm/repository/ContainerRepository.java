package com.grtship.mdm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.grtship.mdm.domain.Container;

/**
 * Spring Data  repository for the Container entity.
 */
@Repository
public interface ContainerRepository extends JpaRepository<Container, Long> {
}
