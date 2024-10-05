/**
 * 
 */
package com.grt.elogfrieght.services.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.grt.elogfrieght.services.user.domain.UserLoginDetails;

/**
 * @author ER Ajay Sharma
 *
 */
@Repository
public interface UserLoginRepository extends JpaRepository<UserLoginDetails, Long> {
	Optional<UserLoginDetails> findByTokenAndLoggedOut(String token, Boolean isLoggedOut);
}
