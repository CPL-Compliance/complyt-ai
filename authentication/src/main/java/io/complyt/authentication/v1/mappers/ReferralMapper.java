package io.complyt.authentication.v1.mappers;

import io.complyt.authentication.domain.Referral;
import io.complyt.authentication.v1.models.ReferralDto;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface ReferralMapper {
    ReferralMapper INSTANCE = Mappers.getMapper(ReferralMapper.class);

    ReferralDto referralToReferralDto(Referral referral);
    Referral referralDtoToReferral(ReferralDto referralDto);
}