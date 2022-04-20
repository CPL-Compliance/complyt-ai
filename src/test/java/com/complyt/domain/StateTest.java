package com.complyt.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class StateTest {
    private State state;
    private State anotherState;

    @BeforeEach
    void setUp() {
        String id = UUID.randomUUID().toString();
        double salesTaxRate = new Random().nextDouble();
        String abbreviation = "CA";
        String code = "08";
        String name = "California";
        List<Nexus> nexuses = new ArrayList<Nexus>() {
            {
                add(new Nexus("Economic", new ArrayList<NexusRule>() {
                    {
                        add(new NexusRule("Count", 500));
                    }
                }));
            }
        };

        state = new State(id, salesTaxRate, abbreviation, code, name, nexuses);
        anotherState = new State(state.getId(), state.getSalesTaxRate(), state.getAbbreviation(), state.getCode(), state.getName(), state.getNexuses());
    }

    @Test
    void equals_IdenticalStates_Equal() {
        assertEquals(state, anotherState);
    }

    @Test
    void hashCode_IdenticalStates_Equal() {
        assertEquals(state.hashCode(), anotherState.hashCode());
    }

    @Test
    void testToString() {
        String referenceString = "State(id=" + state.getId() + ", salesTaxRate=" + state.getSalesTaxRate() + ", abbreviation=" + state.getAbbreviation() + ", code=" + state.getCode() + ", name=" + state.getName() + ", nexuses=" + state.getNexuses() + ")";

        assertEquals(referenceString, state.toString());
    }
}