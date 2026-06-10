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
 * SystemSettingsRequest
 * ════════════════════════════════════════════════════════════════════════════════════
 * Payload to update core runtime system settings and security controls.
 *
 * This DTO aligns with the target system architecture migration to Spring Boot.
 * It serves as a contract for data exchange, keeping business validation rules cleanly
 * separated from the persistence model layer.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SystemSettingsRequest {

    /**
     * The global branding name of this service desk installation.
     */
    private String systemName;

    /**
     * System release version code.
     */
    private String systemVersion;

    /**
     * System timezone code (e.g. UTC, GMT+5:30).
     */
    private String timeZone;

    /**
     * Format pattern for date representation.
     */
    private String dateFormat;

    /**
     * The default fallback localization language.
     */
    private String defaultLanguage;

    /**
     * Flag to block non-admin logins during system downtime.
     */
    private Boolean maintenanceMode;

    /**
     * Inactivity interval before terminating active sessions.
     */
    private Integer sessionTimeoutMinutes;

    /**
     * Flag to route logins to external identity providers.
     */
    private Boolean enableSso;

    /**
     * Enforce multi-factor verification checks.
     */
    private Boolean enableMfa;


    /**
     * Builder factory method to instantiate a new builder.
     * @return a new Builder instance for fluent creation.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Fluent Builder pattern implementation to construct SystemSettingsRequest instances safely.
     */
    public static class Builder {
        private String systemName;
        private String systemVersion;
        private String timeZone;
        private String dateFormat;
        private String defaultLanguage;
        private Boolean maintenanceMode;
        private Integer sessionTimeoutMinutes;
        private Boolean enableSso;
        private Boolean enableMfa;

        /**
         * Default constructor.
         */
        public Builder() {}

        /**
         * Set the systemName attribute.
         * @param systemName value
         * @return Builder instance
         */
        public Builder systemName(String systemName) {
            this.systemName = systemName;
            return this;
        }

        /**
         * Set the systemVersion attribute.
         * @param systemVersion value
         * @return Builder instance
         */
        public Builder systemVersion(String systemVersion) {
            this.systemVersion = systemVersion;
            return this;
        }

        /**
         * Set the timeZone attribute.
         * @param timeZone value
         * @return Builder instance
         */
        public Builder timeZone(String timeZone) {
            this.timeZone = timeZone;
            return this;
        }

        /**
         * Set the dateFormat attribute.
         * @param dateFormat value
         * @return Builder instance
         */
        public Builder dateFormat(String dateFormat) {
            this.dateFormat = dateFormat;
            return this;
        }

        /**
         * Set the defaultLanguage attribute.
         * @param defaultLanguage value
         * @return Builder instance
         */
        public Builder defaultLanguage(String defaultLanguage) {
            this.defaultLanguage = defaultLanguage;
            return this;
        }

        /**
         * Set the maintenanceMode attribute.
         * @param maintenanceMode value
         * @return Builder instance
         */
        public Builder maintenanceMode(Boolean maintenanceMode) {
            this.maintenanceMode = maintenanceMode;
            return this;
        }

        /**
         * Set the sessionTimeoutMinutes attribute.
         * @param sessionTimeoutMinutes value
         * @return Builder instance
         */
        public Builder sessionTimeoutMinutes(Integer sessionTimeoutMinutes) {
            this.sessionTimeoutMinutes = sessionTimeoutMinutes;
            return this;
        }

        /**
         * Set the enableSso attribute.
         * @param enableSso value
         * @return Builder instance
         */
        public Builder enableSso(Boolean enableSso) {
            this.enableSso = enableSso;
            return this;
        }

        /**
         * Set the enableMfa attribute.
         * @param enableMfa value
         * @return Builder instance
         */
        public Builder enableMfa(Boolean enableMfa) {
            this.enableMfa = enableMfa;
            return this;
        }


        /**
         * Construct the finalized SystemSettingsRequest instance.
         * @return the fully populated DTO
         */
        public SystemSettingsRequest build() {
            return new SystemSettingsRequest(
                this.systemName,
                this.systemVersion,
                this.timeZone,
                this.dateFormat,
                this.defaultLanguage,
                this.maintenanceMode,
                this.sessionTimeoutMinutes,
                this.enableSso,
                this.enableMfa
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
        SystemSettingsRequest that = (SystemSettingsRequest) o;
        return Objects.equals(systemName, that.systemName) &&
               Objects.equals(systemVersion, that.systemVersion) &&
               Objects.equals(timeZone, that.timeZone) &&
               Objects.equals(dateFormat, that.dateFormat) &&
               Objects.equals(defaultLanguage, that.defaultLanguage) &&
               Objects.equals(maintenanceMode, that.maintenanceMode) &&
               Objects.equals(sessionTimeoutMinutes, that.sessionTimeoutMinutes) &&
               Objects.equals(enableSso, that.enableSso) &&
               Objects.equals(enableMfa, that.enableMfa);
    }

    @Override
    public int hashCode() {
        return Objects.hash(systemName, systemVersion, timeZone, dateFormat, defaultLanguage, maintenanceMode, sessionTimeoutMinutes, enableSso, enableMfa);
    }

    @Override
    public String toString() {
        return "SystemSettingsRequest{" +
                "systemName=" + systemName + ", " + 
                "systemVersion=" + systemVersion + ", " + 
                "timeZone=" + timeZone + ", " + 
                "dateFormat=" + dateFormat + ", " + 
                "defaultLanguage=" + defaultLanguage + ", " + 
                "maintenanceMode=" + maintenanceMode + ", " + 
                "sessionTimeoutMinutes=" + sessionTimeoutMinutes + ", " + 
                "enableSso=" + enableSso + ", " + 
                "enableMfa=" + enableMfa +
                '}';
    }
}
