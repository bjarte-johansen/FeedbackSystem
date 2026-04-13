package root.controllers;

import root.app.includes.PageCursor;
import root.app.includes.PageCursorEncoder;
import root.common.utils.FunnyUserNameGenerator;
import root.common.utils.IpsumLoremGenerator;
import root.repositories.ReviewRepository;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class ControllerUtils {

    /**
     * Adds default values for the new review form to the model. This method can be called in the controller method that
     * handles the new review form to ensure that the default values are always available in the model when rendering
     * the form.
     *
     * @param modelMap
     */

    public static void addDefaultNewReviewFormValues(Map<String, Object> modelMap) {
        modelMap.put("displayNameSuggestion", FunnyUserNameGenerator.generate());
        modelMap.put("titleSuggestion", IpsumLoremGenerator.generate(2 + (int) (Math.random() * 4)).replace(".", ""));
        modelMap.put("commentSuggestion", IpsumLoremGenerator.generate(7 + (int) (Math.random() * 15)));
        modelMap.put("scoreSuggestion", 1 + new Random().nextInt(5));
    }


    /**
     * Adds a list of unique external IDs to the model for use in a select pill component. This method can be called in
     * the controller method that handles the page where the select pill is used to ensure that the unique external IDs
     * are always available in the model when rendering the page.
     *
     * @param modelMap
     * @param reviewRepo
     * @return
     * @throws Exception
     */

    public static List<String> addSelectExternalIdPillData(Map<String, Object> modelMap, ReviewRepository reviewRepo) throws Exception {
        List<String> uniqueExternalIds = reviewRepo.findDistinctExternalIdByExternalId();
        modelMap.put("uniqueExternalIds", uniqueExternalIds);
        return uniqueExternalIds;
    }


//    /**
//     * Adds pagination cursor data to the model for use in pagination controls. This method can be called in the
//     * controller method that handles the page where pagination is used to ensure that the cursor data is always
//     * available in the model when rendering the page.
//     *
//     * @param modelMap
//     * @param elementCount
//     * @param originalCursor
//     */

//    public static void addCursorToModel(Map<String, Object> modelMap, int elementCount, PageCursor originalCursor) {
//        modelMap.put("cursorOffset", originalCursor.getOffset());
//        modelMap.put("cursorLimit", originalCursor.getLimit());
//        //modelMap.put("cursorMaxOffset", elementCount);
//        modelMap.put("pageCursor", PageCursorEncoder.encodeCursor(originalCursor));
//    }


    /**
     * Decodes a pagination cursor from a string. If the string is null or blank, it creates a new cursor with an offset
     * of 0 and the specified default limit.
     *
     * @param cursorStr
     * @param defaultLimit
     * @return
     */

    public static PageCursor decodeOrCreateCursor(String cursorStr, int defaultLimit) {
        return PageCursorEncoder.decodeCursor(cursorStr, defaultLimit);
    }


//    /*
//    other methods
//     */
//
//    // used to extract the externalId from the request URI for the /reviews/{externalId} route. Must be used
//    // allow for complex routing
//    private String extractExternalIdFromRequest(HttpServletRequest req) {
//        String path = (String) req.getAttribute(
//            org.springframework.web.servlet.HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
//
//        String bestMatch = (String) req.getAttribute(
//            org.springframework.web.servlet.HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
//
//        String externalId = new org.springframework.util.AntPathMatcher()
//            .extractPathWithinPattern(bestMatch, path);
//
//        return externalId;
//    }
}
