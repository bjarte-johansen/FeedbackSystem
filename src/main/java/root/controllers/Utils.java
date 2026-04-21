package root.controllers;

import root.repositories.ReviewRepository;

import java.util.List;
import java.util.Map;


/**
 * utility class for controllers
 */

public class Utils {
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
