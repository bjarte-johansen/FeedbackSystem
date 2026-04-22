package root.A_TODO;


/**
 * utility class for controllers
 */

@Deprecated
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

//    public static List<String> addSelectExternalIdPillData(Map<String, Object> modelMap, ReviewRepository reviewRepo) {
//        List<String> uniqueExternalIds = reviewRepo.findDistinctExternalIdByExternalId();
//        modelMap.put("uniqueExternalIds", uniqueExternalIds);
//        return uniqueExternalIds;
//    }
}
