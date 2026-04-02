package root.app;

public class Todo {
    /**
    // TODO: Legge til index på review tabellen for å optimalisere spørringer som filtrerer på status og sorterer på
    //  created_at feltet. Dette vil forbedre ytelsen for spørringer som henter anmeldelser basert på status og sorterer
    //  dem etter opprettelsestidspunkt.
    //  CREATE INDEX idx_review_status_1_created
    //  ON review(created_at DESC)
    //  WHERE status = 1;
     */

    /**
     * TODO: Controllers for ruter som håndterer CRUD-operasjoner for reviews, users, og courses.
     *  - CREATE
     *  - DELETE
     *  - READ
     *  - UPDATE
     *  - SET APPROVED (bruk custom SQL så en slipper å hente review, endre status og så oppdatere review, kan heller gjøre det i en operasjon)
     *      ala FSQLQuery.Create("UPDATE review SET status = ? WHERE id = ?")
     *
     *  - SET REJECTED (bruk custom SQL så en slipper å hente review, endre status og så oppdatere review, kan heller gjøre det i en operasjon)
     *  - ADD like / dislike (bruk custom SQL så en slipper å hente review, endre like/dislike count og så oppdatere review, kan heller gjøre det i en operasjon)
     *      pass på wraparound på smallint, må ikke skje, thrower error, vi lar teller gå til max
     */
}
