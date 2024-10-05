/**
 * 
 */
package com.grt.elogfrieght.services.user.serviceimpl;

import org.springframework.stereotype.Component;

import com.grt.elogfrieght.services.user.domain.User;
import com.grtship.core.annotation.Auditable;
import com.grtship.core.annotation.Auditable.ActionType;
import com.grtship.core.annotation.Auditable.Module;

/**
 * @author hp
 *
 */
@Component
public class UserAuditor {
	@Auditable(action = ActionType.SAVE,module = Module.USER)
	public User getAuditUser(User user) {
		return user;
	}
}
