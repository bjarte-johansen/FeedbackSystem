/**
 * THIS FILE MUST REFLECT CONSTANTS IN JAVA, MAKE EXPORT SCRIPT FOR IT
 */

class Constants{
    /*
    review status
     */

    static REVIEW_STATUS_APPROVED = 1;
    static REVIEW_STATUS_PENDING = 2;
    static REVIEW_STATUS_REJECTED = 3;

    static ALL_REVIEW_STATUSES = [Constants.REVIEW_STATUS_APPROVED, Constants.REVIEW_STATUS_PENDING, Constants.REVIEW_STATUS_REJECTED];

    static REVIEW_STATUS_FRIENDLY_NAMES = {
        [Constants.REVIEW_STATUS_APPROVED]: "Godkjent",
        [Constants.REVIEW_STATUS_PENDING]: "Til vurdering",
        [Constants.REVIEW_STATUS_REJECTED]: "Avvist"
    };

    static REVIEW_STATUS_CSS_NAMES = {
        [Constants.REVIEW_STATUS_APPROVED]: "approved",
        [Constants.REVIEW_STATUS_PENDING]: "pending",
        [Constants.REVIEW_STATUS_REJECTED]: "rejected"
    };

    static getReviewStatusFriendlyCssName(id_const, def = "review-status-unknown review-status-any"){
        return Constants.REVIEW_STATUS_CSS_NAMES[id_const] ?? def;
    }
    static getReviewStatusFriendlyDisplayName(id_const, def = "Ingen statusfilter"){
        return Constants.REVIEW_STATUS_FRIENDLY_NAMES[id_const] ?? def;
    }




    /*
    order by options
    */

    static ORDER_BY_OPTIONS = {
        "Nyest først": 2,
        "Eldst først": 1,
        "Nyttigst først": 24,
        "Score (høyest først)": 9,
        "Score (lavest først)": 8,
        "Alle": ""
    };
}