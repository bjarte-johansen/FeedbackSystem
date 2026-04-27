package root.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import root.annotations.AdminOnly;
import root.includes.context.SchemaContext;
import root.includes.context.TenantContext;
import root.includes.logger.Logger;
import root.models.tenant.Tenant;
import root.models.tenant.TenantDomain;
import root.repositories.tenant.TenantDomainRepository;
import root.repositories.tenant.TenantRepository;
import root.services.tenant.TenantResolverCache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class TenantSettingsController {
//    @Autowired
//    ReviewSettingsService reviewSettingsService;

    @Autowired
    TenantDomainRepository tenantDomainRepo;

    @Autowired
    private TenantRepository tenantRepository;

    // shallow copy by chatgpt
    static <T> T shallowCopy(T obj) {
        try {
            Class<?> c = obj.getClass();
            T copy = (T) c.getDeclaredConstructor().newInstance();

            for (var f : c.getDeclaredFields()) {
                f.setAccessible(true);
                f.set(copy, f.get(obj)); // just reference copy
            }
            return copy;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * render form for editing tenant settings
     * @param model
     * @return
     */

    @AdminOnly
    @GetMapping("/api/tenant/edit/form/html")
    public String createEditTenantSettingsForm(
        Model model
    ){
        Map<String, Object> vm = new HashMap<>();

        // create a shallow copy of the tenant to avoid modifying the original object in the context. This allows us to
        // safely add the tenant to the model without risking unintended side effects on the original tenant object.
        Tenant tenantCopy = shallowCopy(TenantContext.get());
        tenantCopy.setPasswordHash("********");
        tenantCopy.setPasswordSalt("********");
        vm.put("tenant", tenantCopy);

        // add domain list for tenant to model. We need to do this within the tenant schema context to ensure we are
        // querying the correct database schema for the tenant domains.
        SchemaContext.scopeSchema("public", () ->{
            List<TenantDomain> tenantDomain = tenantDomainRepo.findByTenantId(tenantCopy.getId());
            vm.put("tenantDomainList", tenantDomain);
        });

        vm.put("submitUrl", "/api/tenant/edit/form");

        model.addAllAttributes(vm);
        return "admin/forms/edit-tenant-settings";
    }


    /**
     * Save tenant settings
     * @param enableListing
     * @param enableSubmit
     * @param req
     * @return
     */

    @AdminOnly
    @PutMapping("/api/tenant/edit/form")
    public ResponseEntity<Void> saveTenantSettings(
        @RequestParam Boolean enableListing,
        @RequestParam Boolean enableSubmit,
        HttpServletRequest req
    ){
        Logger.log("see available fields in request printout");

        SchemaContext.scopeSchema("public", () -> {
            Tenant tenant = tenantRepository.findById(TenantContext.get().getId())
                .orElseThrow(() -> new RuntimeException("Tenant not found"));

            tenant.setEnableListing(enableListing);
            tenant.setEnableSubmit(enableSubmit);

            tenantRepository.save(tenant);

            // make sure future finds new tenant info, not cached from memory
            TenantResolverCache.evict(tenant.getId());
        });

        return ResponseEntity.ok().build();
    }
}
