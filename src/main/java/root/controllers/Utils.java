package root.controllers;

import root.common.utils.FunnyUserNameGenerator;
import root.common.utils.IpsumLoremGenerator;
import root.repositories.ReviewRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class Utils {

    /**
     * Adds default values for the new review form to the model. This method can be called in the controller method that
     * handles the new review form to ensure that the default values are always available in the model when rendering
     * the form.
     *
     * @param modelMap
     */

    public static void addDefaultNewReviewFormValues(Map<String, Object> modelMap) {
        Map<String, Object> a = new HashMap<>();
        a.put("displayNameSuggestion", FunnyUserNameGenerator.generate());
        a.put("titleSuggestion", IpsumLoremGenerator.generate(2 + (int) (Math.random() * 4)).replace(".", ""));
        a.put("commentSuggestion", IpsumLoremGenerator.generate(7 + (int) (Math.random() * 15)));
        a.put("scoreSuggestion", 1 + new Random().nextInt(5));
        modelMap.put("defaultNewReviewFormValues", a);
    }


    /**
     * Adds a list of unique external IDs to the model for use in a select pill component. This method can be called in
     * the controller method that handles the page where the select pill is used to ensure that the unique external IDs
     * are always available in the model when rendering the page.
     *
     * @param modelMap
     * @param reviewRepo
     * @return
     */

    public static List<String> addSelectExternalIdPillData(Map<String, Object> modelMap, ReviewRepository reviewRepo) {
        List<String> uniqueExternalIds = reviewRepo.findDistinctExternalIdByExternalId();
        modelMap.put("uniqueExternalIds", uniqueExternalIds);
        return uniqueExternalIds;
    }
}
