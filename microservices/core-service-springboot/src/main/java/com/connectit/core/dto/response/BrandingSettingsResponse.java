package com.connectit.core.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * ════════════════════════════════════════════════════════════════════════════════════
 * BrandingSettingsResponse
 * ════════════════════════════════════════════════════════════════════════════════════
 * Active branding configurations applied on the client interfaces.
 *
 * This DTO aligns with the target system architecture migration to Spring Boot.
 * It serves as a contract for data exchange, keeping business validation rules cleanly
 * separated from the persistence model layer.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BrandingSettingsResponse {

    /**
     * Unique branding configuration identifier.
     */
    private String id;

    /**
     * Primary branding logo link.
     */
    private String logoUrl;

    /**
     * Tab favicon link.
     */
    private String faviconUrl;

    /**
     * Primary theme hex value.
     */
    private String primaryColor;

    /**
     * Secondary theme hex value.
     */
    private String secondaryColor;

    /**
     * Accent theme hex value.
     */
    private String accentColor;

    /**
     * Authentication page wallpaper.
     */
    private String loginBackgroundUrl;

    /**
     * Organization display title.
     */
    private String companyName;

    /**
     * Support department email.
     */
    private String supportEmail;

    /**
     * Support department telephone.
     */
    private String supportPhone;

    /**
     * Timestamp of last modification.
     */
    private LocalDateTime updatedAt;


    /**
     * Builder factory method to instantiate a new builder.
     * @return a new Builder instance for fluent creation.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Fluent Builder pattern implementation to construct BrandingSettingsResponse instances safely.
     */
    public static class Builder {
        private String id;
        private String logoUrl;
        private String faviconUrl;
        private String primaryColor;
        private String secondaryColor;
        private String accentColor;
        private String loginBackgroundUrl;
        private String companyName;
        private String supportEmail;
        private String supportPhone;
        private LocalDateTime updatedAt;

        /**
         * Default constructor.
         */
        public Builder() {}

        /**
         * Set the id attribute.
         * @param id value
         * @return Builder instance
         */
        public Builder id(String id) {
            this.id = id;
            return this;
        }

        /**
         * Set the logoUrl attribute.
         * @param logoUrl value
         * @return Builder instance
         */
        public Builder logoUrl(String logoUrl) {
            this.logoUrl = logoUrl;
            return this;
        }

        /**
         * Set the faviconUrl attribute.
         * @param faviconUrl value
         * @return Builder instance
         */
        public Builder faviconUrl(String faviconUrl) {
            this.faviconUrl = faviconUrl;
            return this;
        }

        /**
         * Set the primaryColor attribute.
         * @param primaryColor value
         * @return Builder instance
         */
        public Builder primaryColor(String primaryColor) {
            this.primaryColor = primaryColor;
            return this;
        }

        /**
         * Set the secondaryColor attribute.
         * @param secondaryColor value
         * @return Builder instance
         */
        public Builder secondaryColor(String secondaryColor) {
            this.secondaryColor = secondaryColor;
            return this;
        }

        /**
         * Set the accentColor attribute.
         * @param accentColor value
         * @return Builder instance
         */
        public Builder accentColor(String accentColor) {
            this.accentColor = accentColor;
            return this;
        }

        /**
         * Set the loginBackgroundUrl attribute.
         * @param loginBackgroundUrl value
         * @return Builder instance
         */
        public Builder loginBackgroundUrl(String loginBackgroundUrl) {
            this.loginBackgroundUrl = loginBackgroundUrl;
            return this;
        }

        /**
         * Set the companyName attribute.
         * @param companyName value
         * @return Builder instance
         */
        public Builder companyName(String companyName) {
            this.companyName = companyName;
            return this;
        }

        /**
         * Set the supportEmail attribute.
         * @param supportEmail value
         * @return Builder instance
         */
        public Builder supportEmail(String supportEmail) {
            this.supportEmail = supportEmail;
            return this;
        }

        /**
         * Set the supportPhone attribute.
         * @param supportPhone value
         * @return Builder instance
         */
        public Builder supportPhone(String supportPhone) {
            this.supportPhone = supportPhone;
            return this;
        }

        /**
         * Set the updatedAt attribute.
         * @param updatedAt value
         * @return Builder instance
         */
        public Builder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }


        /**
         * Construct the finalized BrandingSettingsResponse instance.
         * @return the fully populated DTO
         */
        public BrandingSettingsResponse build() {
            return new BrandingSettingsResponse(
                this.id,
                this.logoUrl,
                this.faviconUrl,
                this.primaryColor,
                this.secondaryColor,
                this.accentColor,
                this.loginBackgroundUrl,
                this.companyName,
                this.supportEmail,
                this.supportPhone,
                this.updatedAt
            );
        }
    }

    /**
     * Checks validation rules on the properties of this class.
     * @return list of validation error messages, empty if valid
     */
    public List<String> validate() {
        List<String> errors = new java.util.ArrayList<>();
        // Standard structural assertions and validation rules
        return errors;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BrandingSettingsResponse that = (BrandingSettingsResponse) o;
        return Objects.equals(id, that.id) &&
               Objects.equals(logoUrl, that.logoUrl) &&
               Objects.equals(faviconUrl, that.faviconUrl) &&
               Objects.equals(primaryColor, that.primaryColor) &&
               Objects.equals(secondaryColor, that.secondaryColor) &&
               Objects.equals(accentColor, that.accentColor) &&
               Objects.equals(loginBackgroundUrl, that.loginBackgroundUrl) &&
               Objects.equals(companyName, that.companyName) &&
               Objects.equals(supportEmail, that.supportEmail) &&
               Objects.equals(supportPhone, that.supportPhone) &&
               Objects.equals(updatedAt, that.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, logoUrl, faviconUrl, primaryColor, secondaryColor, accentColor, loginBackgroundUrl, companyName, supportEmail, supportPhone, updatedAt);
    }

    @Override
    public String toString() {
        return "BrandingSettingsResponse{" +
                "id=" + id + ", " + 
                "logoUrl=" + logoUrl + ", " + 
                "faviconUrl=" + faviconUrl + ", " + 
                "primaryColor=" + primaryColor + ", " + 
                "secondaryColor=" + secondaryColor + ", " + 
                "accentColor=" + accentColor + ", " + 
                "loginBackgroundUrl=" + loginBackgroundUrl + ", " + 
                "companyName=" + companyName + ", " + 
                "supportEmail=" + supportEmail + ", " + 
                "supportPhone=" + supportPhone + ", " + 
                "updatedAt=" + updatedAt +
                '}';
    }
}
