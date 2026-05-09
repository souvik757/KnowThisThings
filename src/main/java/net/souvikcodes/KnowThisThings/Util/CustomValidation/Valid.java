package net.souvikcodes.KnowThisThings.Util.CustomValidation;

import org.springframework.beans.factory.annotation.Value;

public class Valid {
    @Value("${validation.email.regex}")
    private String emailRegex;

    public boolean isValidEmail(String email) {
        return email != null && email.matches(emailRegex);
    }
}
