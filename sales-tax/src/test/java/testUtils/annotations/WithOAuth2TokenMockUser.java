package testUtils.annotations;

import org.springframework.core.annotation.AliasFor;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@WithSecurityContext(factory = WithOAuth2TokenMockUserSecurityContextFactory.class)
public @interface WithOAuth2TokenMockUser {
    String value() default "user";

    String username() default "";

    String[] roles() default {"USER"};

    String[] authorities() default {};

    String password() default "password";

    @AliasFor(
            annotation = WithSecurityContext.class
    )
    TestExecutionEvent setupBefore() default TestExecutionEvent.TEST_METHOD;
}
