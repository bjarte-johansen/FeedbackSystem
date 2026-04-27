class NewReviewFormValidator{
    static validate(form) {
        // support multiple errors
        let valid = true;

        // support methods
        const validate = (input, cond, msg) => {
            input.setCustomValidity(!cond ? msg : "");      // clear or set msg
            valid = valid && cond;                          // modify flag
        }
        const validateNonBlank = (input, msg) => validate(input, input.value.trim() !== "", msg);
        const validateEmail = (input, msg) => validate(input, input.value.includes("@"), msg);
        const validateBetween = (input, value, min, max, msg) => validate(input, (value >= min) && (value <= max), msg);

        // validate
        validateEmail(form.email, "Ugyldig epost");
        validateNonBlank(form.displayName, "Navn mangler");
        validateNonBlank(form.title, "Tittel mangler");
        validateNonBlank(form.comment, "Kommentar mangler");
        validateBetween(form.score, parseInt(form.score.value.trim(), 10), 1, 5, "Ugyldig score");

        if (!valid) form.reportValidity();

        return valid;
    }
}