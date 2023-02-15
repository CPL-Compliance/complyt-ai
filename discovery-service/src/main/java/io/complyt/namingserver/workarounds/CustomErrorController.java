package io.complyt.namingserver.workarounds;

import io.complyt.namingserver.annotations.Generated;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
* This is a workaround to overcome Spring Boot 3 bug that prevents from the client to reregister once the
* discovery service restarts.
* */
@Generated
@RestController
class CustomErrorController implements ErrorController {
    private static final String ERROR_MAPPING = "/error";

    @RequestMapping(ERROR_MAPPING)
    public ResponseEntity<Void> error() {
        return ResponseEntity.notFound().build();
    }
}
