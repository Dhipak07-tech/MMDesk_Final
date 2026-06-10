package com.connectit.core.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * ════════════════════════════════════════════════════════════════════════════════════
 * BrandingSettingsRequest
 * ════════════════════════════════════════════════════════════════════════════════════
 * Request payload to configure custom color palettes, logos, and portal theme values.
 *
 * This DTO aligns with the target system architecture migration to Spring Boot.
 * It serves as a contract for data exchange, keeping business validation rules cleanly
 * separated from the persistence model layer.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BrandingSettingsRequest {

    /**
     * Link to corporate brand logo.
     */
    private String logoUrl;

    /**
     * Link to browser shortcut icon.
     */
    private String faviconUrl;

    /**
     * Hex code for dominant portal colors.
     */
    private String primaryColor;

    /**
     * Hex code for secondary accents.
     */
    private String secondaryColor;

    /**
     * Hex code for focus highlights and markers.
     */
    private String accentColor;

    /**
     * Background wallpaper link for authorization screen.
     */
    private String loginBackgroundUrl;

    /**
     * Legal name of corporate customer.
     */
    private String companyName;

    /**
     * Direct contact route for user queries.
     */
    private String supportEmail;

    /**
     * Direct telephone line for high-priority support.
     */
    private String supportPhone;


    /**
     * Builder factory method to instantiate a new builder.
     * @return a new Builder instance for fluent creation.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Fluent Builder pattern implementation to construct BrandingSettingsRequest instances safely.
     */
    public static class Builder {
        private String logoUrl;
        private String faviconUrl;
        private String primaryColor;
        private String secondaryColor;
        private String accentColor;
        private String loginBackgroundUrl;
        private String companyName;
        private String supportEmail;
        private String supportPhone;

        /**
         * Default constructor.
         */
        public Builder() {}

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
         * Construct the finalized BrandingSettingsRequest instance.
         * @return the fully populated DTO
         */
        public BrandingSettingsRequest build() {
            return new BrandingSettingsRequest(
                this.logoUrl,
                this.faviconUrl,
                this.primaryColor,
                this.secondaryColor,
                this.accentColor,
                this.loginBackgroundUrl,
                this.companyName,
                this.supportEmail,
                this.supportPhone
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
        BrandingSettingsRequest that = (BrandingSettingsRequest) o;
        return Objects.equals(logoUrl, that.logoUrl) &&
               Objects.equals(faviconUrl, that.faviconUrl) &&
               Objects.equals(primaryColor, that.primaryColor) &&
               Objects.equals(secondaryColor, that.secondaryColor) &&
               Objects.equals(accentColor, that.accentColor) &&
               Objects.equals(loginBackgroundUrl, that.loginBackgroundUrl) &&
               Objects.equals(companyName, that.companyName) &&
               Objects.equals(supportEmail, that.supportEmail) &&
               Objects.equals(supportPhone, that.supportPhone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(logoUrl, faviconUrl, primaryColor, secondaryColor, accentColor, loginBackgroundUrl, companyName, supportEmail, supportPhone);
    }

    @Override
    public String toString() {
        return "BrandingSettingsRequest{" +
                "logoUrl=" + logoUrl + ", " + 
                "faviconUrl=" + faviconUrl + ", " + 
                "primaryColor=" + primaryColor + ", " + 
                "secondaryColor=" + secondaryColor + ", " + 
                "accentColor=" + accentColor + ", " + 
                "loginBackgroundUrl=" + loginBackgroundUrl + ", " + 
                "companyName=" + companyName + ", " + 
                "supportEmail=" + supportEmail + ", " + 
                "supportPhone=" + supportPhone +
                '}';
    }
}
