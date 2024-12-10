package com.complyt.domain;

/**
 * this interface is being implemented by Address.class, and AdderssWithDate.class
 *
 * to use the Internal and External functions, without "knowing" which service is loaded
 * we needed another interface that represented a minimum similarity point that both flows are using
 * External - only needs the address
 * Internal - needs the address + a relevant date
 *
 * so this interfaces is the abstraction of "not knowing which service is loaded",
 * and letting the service itself cast to the object it needs
 */
public interface TaxableLocation {
}
