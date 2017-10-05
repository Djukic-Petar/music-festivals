package validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

class PasswordConstraintValidator implements ConstraintValidator<Password, String> {

    @Override
    public void initialize(Password a) {

    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        int uppercaseLetterCount = 0, lowercaseLetterCount = 0, numberAndSpecialCharacterCount = 0;
        if (value.length() < 6 || value.length() > 12) {
            return false;
        }
        if (!Character.isAlphabetic(value.charAt(0))) {
            return false;
        }
        for (int i = 0; i < value.length(); i++) {
            if (Character.isLetter(value.charAt(i))) {
                if (Character.isLowerCase(value.charAt(i))) {
                    lowercaseLetterCount++;
                } else {
                    uppercaseLetterCount++;
                }
            } else {
                numberAndSpecialCharacterCount++;
            }
            if (i != 0 && value.charAt(i-1) == value.charAt(i)) {
                return false;
            }
        }
        return uppercaseLetterCount >= 1 && lowercaseLetterCount >= 3 && numberAndSpecialCharacterCount >= 1;
    }
    
}
