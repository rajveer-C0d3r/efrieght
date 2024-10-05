package com.grt.elogfrieght.services.user.service;

import java.util.List;
import java.util.Optional;

import org.springframework.transaction.annotation.Transactional;

import com.grt.elogfrieght.services.user.domain.User;
import com.grtship.common.exception.InvalidDataException;
import com.grtship.core.dto.ApprovalRequestDTO;
import com.grtship.core.dto.ClientIdDTO;
import com.grtship.core.dto.CompanyDTO;
import com.grtship.core.dto.CsaDetailsDTO;
import com.grtship.core.dto.EmailPresentDto;
import com.grtship.core.dto.GsaDetailsDTO;
import com.grtship.core.dto.UserApprovalRequestDTO;
import com.grtship.core.dto.UserCreationRequestDTO;
import com.grtship.core.dto.UserDTO;
import com.grtship.core.dto.UserSpecificBranchDTO;
import com.grtship.core.dto.UserSpecificCBResponse;
import com.grtship.core.dto.UserSpecificCompanyDTO;

import javassist.tools.rmi.ObjectNotFoundException;

/**
 * Service class for managing users.
 */
@Transactional
public interface UserService {

	public Optional<User> activateRegistration(String key);

	public Optional<User> completePasswordReset(String newPassword, String key);

	public Optional<User> requestPasswordReset(String mail);

	public User registerUser(UserDTO userDTO, String password);

	@Transactional(readOnly = false)
	public User createUser(UserCreationRequestDTO userDTO) throws ObjectNotFoundException, InvalidDataException;

	/**
	 * Update all information for a specific user, and return the modified user.
	 *
	 * @param userDTO user to update.
	 * @return updated user.
	 * @throws InvalidDataException
	 */
	public Optional<UserDTO> updateUser(UserCreationRequestDTO userDTO) throws InvalidDataException;

	public void deleteUser(Long id);

	public void changePassword(String currentClearTextPassword, String newPassword);

	public Boolean generateGsaUsers(List<GsaDetailsDTO> gsaUserDetails) throws InvalidDataException;

	public Boolean generateCsaUsers(List<CsaDetailsDTO> csaUserDetails);

	public void lockUser(Long id, Boolean locked);

	public void deactivate(Long id, boolean isPermanentDeactivate);

	public void reactivate(Long id);
	
	public Optional<User> completePasswordByResetKeyAndVerficationCode(String newPassword, String key, String verficationCode);

	public Optional<User> findOneWithAuthoritiesByEmailIgnoreCase(String username);

	public Optional<User> findById(Long userId);

	public User findByEmail(String email);
	
	public Optional<User> findOneWithAuthoritiesByLogin(String login);

	public UserSpecificCBResponse getUserSpecificCompanyDetails();
	
	public ClientIdDTO getClientIdAsPerLogggedInUser();

	public EmailPresentDto checkEmailPresent(List<String> emailIds);
	
	public List<UserSpecificCompanyDTO> getUserSpecificCompanies();
	
	public List<UserSpecificBranchDTO> getUserSpecificBranches(Long companyId);
	
	public void getUsersByRoleIds(ApprovalRequestDTO approvalRequestDTO);

	public void saveUserApprovalRequestSentData(UserApprovalRequestDTO userApprovalRequestDTO);
	
	public String validateCsaUsers(CompanyDTO companyDTO);
}
