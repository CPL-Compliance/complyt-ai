package com.complyt.business;

import com.complyt.business.sales_tax.sales_tax_web_clients.FastTaxWebClientWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FastTaxWebClientWrapperTest {


    FastTaxWebClientWrapper fastTaxWebClientWrapper;

    @BeforeEach
    void setUp(){

    }

}
