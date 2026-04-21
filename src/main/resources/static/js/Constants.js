/**
 * THIS FILE MUST REFLECT CONSTANTS IN JAVA, MAKE EXPORT SCRIPT FOR IT
 */

class Constants{
    static REVIEW_STATUS_APPROVED = 1;
    static REVIEW_STATUS_PENDING = 2;
    static REVIEW_STATUS_REJECTED = 3;

    static ALL_REVIEW_STATUSES = [Constants.REVIEW_STATUS_APPROVED, Constants.REVIEW_STATUS_PENDING, Constants.REVIEW_STATUS_REJECTED];

    static REVIEW_STATUS_FRIENDLY_NAMES = {
        [Constants.REVIEW_STATUS_APPROVED]: "Godkjent",
        [Constants.REVIEW_STATUS_PENDING]: "Under arbeid",
        [Constants.REVIEW_STATUS_REJECTED]: "Avvist"
    };

    static REVIEW_STATUS_CSS_NAMES = {
        [Constants.REVIEW_STATUS_APPROVED]: "approved",
        [Constants.REVIEW_STATUS_PENDING]: "pending",
        [Constants.REVIEW_STATUS_REJECTED]: "rejected"
    };

    static getReviewStatusFriendlyCssName(id_const){
        return this.REVIEW_STATUS_CSS_NAMES[id_const] ?? "review-status-unknown review-status-any";
    }
    static getReviewStatusFriendlyDisplayName(id_const){
        return this.REVIEW_STATUS_FRIENDLY_NAMES[id_const] ?? "Ingen statusfilter";
    }
}