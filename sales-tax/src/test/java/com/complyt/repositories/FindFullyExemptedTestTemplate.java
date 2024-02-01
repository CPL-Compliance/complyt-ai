package com.complyt.repositories;

public interface FindFullyExemptedTestTemplate {
    void findFullyExempted_FindsFullyExemption_ReturnsExemption();
    void findFullyExempted_ExemptionDoesNotExist_ReturnsMonoEmpty();
    void findFullyExempted_NullStatePassed_ThrowsException();
    void findFullyExempted_NullIdPassed_ThrowsException();
}
