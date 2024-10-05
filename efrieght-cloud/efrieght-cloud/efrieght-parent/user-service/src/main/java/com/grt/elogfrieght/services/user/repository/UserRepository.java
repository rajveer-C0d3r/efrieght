package com.grt.elogfrieght.services.user.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.grt.elogfrieght.services.user.domain.User;

/**
 * Spring Data JPA repository for the {@link User} entity.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

	String USERS_BY_LOGIN_CACHE = "usersByLogin";

	String USERS_BY_EMAIL_CACHE = "usersByEmail";

	Optional<User> findOneByActivationKey(String activationKey);

	List<User> findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(Instant dateTime);

	Optional<User> findOneByResetKey(String resetKey);

	Optional<User> findOneByEmailIgnoreCase(String email);

	Optional<User> findOneByLogin(String login);

	@EntityGraph(attributePaths = { "authorities", "userAccess", "userRoles" })
	@Cacheable(cacheNames = USERS_BY_LOGIN_CACHE)
	Optional<User> findOneWithAuthoritiesByLogin(String login);

	@EntityGraph(attributePaths = { "authorities", "userAccess", "userRoles" })
	@Cacheable(cacheNames = USERS_BY_EMAIL_CACHE)
	Optional<User> findOneWithAuthoritiesByEmailIgnoreCase(String email);

	Page<User> findAllByLoginNot(Pageable pageable, String login);

	List<User> findByClientIdIn(List<Long> idList);

	List<User> findAllByEmailIn(List<String> emails);

//    @Query("select u form User u join u.userAccess ua where ua.companyId in ?1")
	List<User> findByUserAccessCompanyIdIn(List<Long> companyIdList);

	User getAuthoritiesById(Long id);

	boolean existsByEmailIgnoreCaseAndCompanyIdAndActivated(String email, Long companyId, Boolean activated);

	boolean existsByEmailIgnoreCase(String newEmail);

	Optional<User> getUserAccessById(Long userId);

	boolean existsByEmailIgnoreCaseAndCompanyIdAndActivatedAndIdNot(String email, Long companyId, Boolean false1,
			Long id);

	@Query(value = " select u.id from User u LEFT JOIN u.userRoles ur where ur.roles.id = ?1")
	List<Long> findByUserRolesRolesId(Long roleId);

	User findByEmailAndPassword(String email, String password);

	User findByEmail(String email);
	
	Optional<User> findOneByResetKeyAndVerificationCode(String resetKey,String verficationCode);

	boolean existsByEmailIgnoreCaseAndIdNot(String email, Long userId);

	List<User> findAllByUserRolesRolesIdInAndUserAccessCompanyId(Set<Long> roleIds,Long companyId);
	
	List<User> findAllByUserRolesRolesIdIn(Set<Long> roleIds);

	List<User> findAllByEmailInAndClientIdNot(List<String> emails,Long clientId);

	Optional<User> findByEmailAndClientId(String email, Long clientId);
}
