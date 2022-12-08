package com.complyt.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Field;

public class TransactionTest {

    @Test
    void testingAmountOfPropertiesInTransaction() {
        /* In case there is a new property added, If its of type Taxable - handle rates and amount calculation for it */
        Field[] fields = Transaction.class.getDeclaredFields();
        Assertions.assertEquals(15, fields.length);
    }
}
