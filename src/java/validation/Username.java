package validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;
import org.primefaces.validate.bean.ClientConstraint;

@Documented
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UsernameConstraintValidator.class)
@ClientConstraint(resolvedBy = UsernameClientValidationConstraint.class)
public @interface Username {

    String message() default "Korisnik sa datim imenom vec postoji!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
