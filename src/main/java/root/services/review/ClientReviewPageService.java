package root.services.review;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import root.app.AppConfig;
import root.controllers.helpers.ControllerConstantMaps;
import root.includes.ReviewQueryOptions;
import root.includes.ReviewQueryOptionsParser;
import root.includes.ReviewAggregateStatistics;
import root.includes.context.TenantContext;
import root.models.review.Review;
import root.models.review.ReviewSettings;
import root.models.tenant.Tenant;
import root.repositories.review.ReviewRepository;
import root.services.utils.IsAdminService;

import java.util.*;


@Service
public class ClientReviewPageService {
    private final ReviewRepository reviewRepo;
    private final ReviewService reviewService;

    @Autowired
    private CachedReviewSettingsService cachedReviewSettingsService;

    /**
     * constructor that injects services/repos
     */

    public ClientReviewPageService(ReviewRepository reviewRepo, ReviewService reviewService) {
        this.reviewRepo = reviewRepo;
        this.reviewService = reviewService;
    }


    /**
     * Build Map of attributes for page rendering
     * We did not have time to comment properly because of late refactoring
     */

    private ReviewSettings getTenantListingConfig() {
        var res = new ReviewSettings();
        Tenant tenant = TenantContext.get();
        res.setEnableListing(tenant.getEnableListing());
        res.setEnableSubmit(tenant.getEnableSubmit());
        return res;
    }

    public Map<String, Object> buildPageData(
        /*
        @RequestParam(required = false) String externalId,
        @RequestParam(required = false) String cursor,
        @RequestParam(required = false) Integer orderByEnum,
        @RequestParam(required = false) String scoreFilter,
        @RequestParam(required = false) String statusFilter,
        @RequestParam(required = false) LocalDate startDateFilter,
        @RequestParam(required = false) LocalDate endDateFilter,
        @RequestParam(required = false) Integer numDaysFilter,
        @RequestParam(required = false) Boolean showDemoPills,
        @RequestParam(required = false) Boolean realApi,
         */
        @RequestParam(required = false, defaultValue="false") boolean includeStats,
        @RequestParam(defaultValue = "false") Boolean includeJsonAsAttribute,
        @RequestParam(defaultValue = ("" + AppConfig.CLIENT_DEFAULT_MAX_VISIBLE_REVIEWS)) int defaultLimit,
        HttpServletRequest req
    ){
        final HashMap<String, Object> vm = new LinkedHashMap<>();
        final boolean bIsAdministrator = IsAdminService.isAdmin();

        String clientExternalId = req.getParameter("externalId");
        String externalId = clientExternalId;
        if(bIsAdministrator) {
            externalId = null;
        }else{
            externalId = (externalId == null) ? AppConfig.DEFAULT_INVALID_EXTERNAL_ID : externalId;
        }

        // evict cached review settings so we guaranteed fresh
        //if(bIsAdministrator) cachedReviewSettingsService.evict(externalId);

        final ReviewSettings tenantCfg = getTenantListingConfig();
        final ReviewSettings reviewCfg = getCachedReviewConfigAndSetAttributes(clientExternalId);
        if(reviewCfg == null) throw new RuntimeException("Failed to find review settings");

        vm.put("isClient", "true");
        vm.put("isAdministrator", String.valueOf(bIsAdministrator));

        // write tenant config
        buildGenericConfig(vm, "tenantConfig", tenantCfg);

        // write page config
        buildGenericConfig(vm, "reviewConfig", reviewCfg);

        // return if page settings has disabled listing


        // return if tenant settings or review has disabled listing
        final boolean enabled = tenantCfg.getEnableListing() && reviewCfg.getEnableListing();
        if(!bIsAdministrator && !enabled) {
            vm.put("enableListing", false);
            return vm;
        }

        vm.put("enableListing", true);

        ReviewQueryOptions qo = ReviewQueryOptionsParser.parseRequest(req, defaultLimit);

        if(!bIsAdministrator) applyNonAdministratorStatusFilter(qo);
        if(bIsAdministrator) applyAdministratorOnlyHandler(vm, externalId, reviewRepo);

        var tmp = this.buildListingData(externalId, qo, includeStats);
        vm.putAll(tmp);

        buildConstants(vm);

        if(Boolean.TRUE.equals(includeJsonAsAttribute)) vm.put("json", root.includes.Utils.toJson(vm));

        return vm;
    }


    /**
     * Build the model data for the review listing page based on the provided externalId and query options. This method
     * fetches the reviews from the database using the ReviewRepository, and adds them to the model map along with other
     * relevant data such as the externalId, page cursor, and optionally the review statistics if includeStats is true.
     * The model map is then returned for use in the JSP view.
     *
     * @param externalId
     * @param options
     * @param includeStats
     * @return
     */

    private Map<String, Object> buildListingData(
        String externalId,
        ReviewQueryOptions options,
        boolean includeStats
    ) {
        Map<String, Object> vm = new HashMap<>();

        List<Review> reviews;
        int totalReviewCount;

        // get list of reviews
        if(externalId == null) {
            reviews = reviewRepo.findByOptionalExternalIdWithPagination(null, options);

            totalReviewCount = (int) reviewRepo.count();
        }else{
            reviews = reviewRepo.findByExternalIdWithPagination(externalId, options);

            // get total review count (can be done via stats instead)
            totalReviewCount = reviewRepo.countByExternalId(externalId, options);
        }

        // add keys to map
        vm.put("reviews", reviews);
        vm.put("unpaginatedFilteredReviewCount", totalReviewCount);

        vm.put("externalId", externalId);
        vm.put("pageCursor", options.getPageCursor().encode());
        vm.put("filters", ReviewQueryOptionsParser.encodeOptions(options));

        if(includeStats && (externalId != null)) {
            ReviewAggregateStatistics reviewStats = reviewService.getReviewAggregateStatistics(externalId, AppConfig.DEFAULT_MAX_SCORE);
            vm.put("statistics", reviewStats);
        }

        return vm;
    }

    /*
    supporting methods
     */

    private void buildGenericConfig(HashMap<String, Object> vm, String key, boolean enableListing, boolean enableSubmit) {
        vm.put(key, root.includes.Utils.linkedMap(
            "enableListing", enableListing,
            "enableSubmit", enableSubmit
        ));
    }

    private void buildGenericConfig(HashMap<String, Object> vm, String key, ReviewSettings reviewCfg) {
        buildGenericConfig(vm, key, reviewCfg.getEnableListing(), reviewCfg.getEnableSubmit());
    }

    private ReviewSettings getCachedReviewConfigAndSetAttributes(String externalId){
        return cachedReviewSettingsService.findOrCreateByExternalId( Objects.requireNonNullElse(externalId, "") );
    }

    private static void applyNonAdministratorStatusFilter(ReviewQueryOptions qo){
        if(qo.getStatusFilterSet().isEmpty()){
            // if not admin and no status filter provided, default to approved
            qo.getStatusFilterSet().add(Review.REVIEW_STATUS_APPROVED);
        }
    }

    private static void applyAdministratorOnlyHandler(Map<String, Object> vm, String externalId, ReviewRepository reviewRepo){
        // TODO: use a method that groups counts by status, single query instead of three queries
        HashMap<Integer, Integer> countMap = new HashMap<>();
        countMap.put(Review.REVIEW_STATUS_APPROVED, reviewRepo.countByStatus(Review.REVIEW_STATUS_APPROVED));
        countMap.put(Review.REVIEW_STATUS_PENDING, reviewRepo.countByStatus(Review.REVIEW_STATUS_PENDING));
        countMap.put(Review.REVIEW_STATUS_REJECTED, reviewRepo.countByStatus(Review.REVIEW_STATUS_REJECTED));
        vm.put("countByReviewStatus", countMap);
    }

    private static void buildConstants(Map<String, Object> vm){
        // put in names/lookups etc
        vm.put("constants", ControllerConstantMaps.ALL_CONSTANTS);
    }
}
