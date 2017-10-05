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
@Constraint(validatedBy = PasswordConstraintValidator.class)
@ClientConstraint(resolvedBy = PasswordClientValidationConstraint.class)
public @interface Password {

    String message() default "Lozinka mora sadrzati izmedju 6 i 12 karaktera, mora pocinjati slovom, i mora da ima najmanje jedno veliko slovo, 3 mala slova i jedan broj ili specijalan karakter.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}