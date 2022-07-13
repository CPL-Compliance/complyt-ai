package com.complyt.v1.model;

import com.complyt.domain.CustomerType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
public class CustomerDtoTest {

    private CustomerDto customerDto;
    private CustomerDto anotherCustomerDto;

    @BeforeEach
    void setUp() {
        String id = UUID.randomUUID().toString();
        String externalId = UUID.randomUUID().toString();
        String name = "Existing Customer";
        AddressDto address = new AddressDto("City", "Country", "County", "State", "Street", "Zip");
        customerDto = new CustomerDto(id, externalId, name, address, CustomerTypeDto.RETAIL);
        anotherCustomerDto = new CustomerDto(customerDto.getId(),customerDto.getExternalId(),customerDto.getName(),customerDto.getAddress(), customerDto.getCustomerType());
    }

    @Test
    void equals_IdenticalCustomers_Equal() {
        assertEquals(customerDto, anotherCustomerDto);
    }

    @Test
    void hashCode_IdenticalCustomers_Equal() {
        assertEquals(customerDto.hashCode(), anotherCustomerDto.hashCode());
    }

    @Test
    void testToString() {
        String referenceString = "CustomerDto(id=" + customerDto.getId() + ", externalId=" + customerDto.getExternalId() + ", name=" + customerDto.getName() + ", address=" + customerDto.getAddress() + ")";

        assertEquals(referenceString, customerDto.toString());
    }
}
