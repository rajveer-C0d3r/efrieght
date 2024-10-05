package com.grtship.notification.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.grtship.notification.domain.NotificationDetail;

@Repository
public interface NotificationRepository
		extends JpaRepository<NotificationDetail, Long>, JpaSpecificationExecutor<NotificationDetail> {

	List<NotificationDetail> findByUser(String user);
}
