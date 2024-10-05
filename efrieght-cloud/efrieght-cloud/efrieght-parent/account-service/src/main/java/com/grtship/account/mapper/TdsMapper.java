package com.grtship.account.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.grtship.account.domain.Tds;
import com.grtship.account.interfaces.EntityMapper;
import com.grtship.core.dto.TdsDTO;

/**
 * Mapper for the entity {@link Tds} and its DTO {@link TdsDTO}.
 */
@Mapper(componentModel = "spring", uses = { TdsRateMapper.class, LedgerMapper.class, TdsTypeMapper.class })
public interface TdsMapper extends EntityMapper<TdsDTO, Tds> {

	@Mapping(source = "ledger.id", target = "ledgerId")
	@Mapping(source = "ledger.name", target = "ledgerName")
	@Mapping(source = "ledger.group.id", target = "groupId")
	@Mapping(source = "ledger.group.name", target = "groupName")
	@Mapping(source = "tdsType.id", target = "tdsTypeId")
	@Mapping(source = "tdsType.name", target = "tdsTypeName")
	TdsDTO toDto(Tds entity);

	@Mapping(source = "ledgerId", target = "ledger.id")
	@Mapping(source = "tdsTypeId", target = "tdsType.id")
	Tds toEntity(TdsDTO tdsDTO);

	default Tds fromId(Long id) {
		if (id == null) {
			return null;
		}
		Tds tds = new Tds();
		tds.setId(id);
		return tds;
	}
}
