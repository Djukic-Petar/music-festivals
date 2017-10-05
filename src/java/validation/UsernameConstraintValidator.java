package validation;

import db.helpers.KorisnikHelper;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

class UsernameConstraintValidator implements ConstraintValidator<Username, String> {

    private static final KorisnikHelper HELPER = new KorisnikHelper();
    
    @Override
    public void initialize(Username a) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(value == null)
            return false;

        return HELPER.getKorisnik(value) == null;
    }
}
