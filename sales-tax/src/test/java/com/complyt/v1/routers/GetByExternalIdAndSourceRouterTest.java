package com.complyt.v1.routers;

import org.junit.jupiter.api.Test;

public interface GetByExternalIdAndSourceRouterTest {
    @Test
    void GetByExternalIdAndSource_Exists_Returns200();
    @Test
    void GetByExternalIdAndSource_DoesntExists_Returns404();
    @Test
    void GetByExternalIdAndSource_invalidSource_Returns404();
    @Test
    void GetByExternalIdAndSource_invalidExternalId_Returns404();
    @Test
    void GetByExternalIdAndSource_NoUserDetails_Returns401();
    @Test
    void GetByExternalIdAndSource_UserDetailsWithoutAuthorities_Returns401();
    @Test
    void GetByExternalIdAndSource_NullHandler_ThrowsNullPointerException();
}

