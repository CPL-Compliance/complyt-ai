package io.complyt.domain.here;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.complyt.domain.AddressData;
import lombok.*;

import java.util.List;

@Getter
@EqualsAndHashCode
@With
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonFormat(with = JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
public class HereAddressData implements AddressData {
        List<HereAddressItem> items;
}
