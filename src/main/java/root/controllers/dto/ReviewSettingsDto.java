package root.controllers.dto;

public record ReviewSettingsDto(
    Long id,
    String name,
    String externalId,
    String enableListing,
    String enableSubmit
) {
}
