package com.grtship.mdm.mapper;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import com.grtship.core.dto.CurrencyDTO;
import com.grtship.mdm.domain.Currency;
import com.grtship.mdm.interfaces.EntityMapper;

/**
 * Mapper for the entity {@link Currency} and its DTO {@link CurrencyDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
@Component
public interface CurrencyMapper extends EntityMapper<CurrencyDTO, Currency> {

	default Currency fromId(Long id) {
		if (id == null) {
			return null;
		}
		Currency currency = new Currency();
		currency.setId(id);
		return currency;
	}
}
