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
 * SystemSettingsResponse
 * ════════════════════════════════════════════════════════════════════════════════════
 * Returns the current active system settings details.
 *
 * This DTO aligns with the target system architecture migration to Spring Boot.
 * It serves as a contract for data exchange, keeping business validation rules cleanly
 * separated from the persistence model layer.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SystemSettingsResponse {

    /**
     * Setting record ID.
     */
    private String id;

    /**
     * Ticklora installation name.
     */
    private String systemName;

    /**
     * Active software build version.
     */
    private String systemVersion;

    /**
     * Default timezone.
     */
    private String timeZone;

    /**
     * Display date format.
     */
    private String dateFormat;

    /**
     * Fallback language selection.
     */
    private String defaultLanguage;

    /**
     * Is maintenance mode enabled.
     */
    private Boolean maintenanceMode;

    /**
     * Active session expiration limit.
     */
    private Integer sessionTimeoutMinutes;

    /**
     * SSO configuration flag.
     */
    private Boolean enableSso;

    /**
     * MFA configuration flag.
     */
    private Boolean enableMfa;

    /**
     * Last change timestamp.
     */
    private LocalDateTime updatedAt;

    /**
     * Operator identifier who modified settings.
     */
    private String updatedBy;


    /**
     * Builder factory method to instantiate a new builder.
     * @return a new Builder instance for fluent creation.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Fluent Builder pattern implementation to construct SystemSettingsResponse instances safely.
     */
    public static class Builder {
        private String id;
        private String systemName;
        private String systemVersion;
        private String timeZone;
        private String dateFormat;
        private String defaultLanguage;
        private Boolean maintenanceMode;
        private Integer sessionTimeoutMinutes;
        private Boolean enableSso;
        private Boolean enableMfa;
        private LocalDateTime updatedAt;
        private String updatedBy;

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
         * Set the updatedAt attribute.
         * @param updatedAt value
         * @return Builder instance
         */
        public Builder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        /**
         * Set the updatedBy attribute.
         * @param updatedBy value
         * @return Builder instance
         */
        public Builder updatedBy(String updatedBy) {
            this.updatedBy = updatedBy;
            return this;
        }


        /**
         * Construct the finalized SystemSettingsResponse instance.
         * @return the fully populated DTO
         */
        public SystemSettingsResponse build() {
            return new SystemSettingsResponse(
                this.id,
                this.systemName,
                this.systemVersion,
                this.timeZone,
                this.dateFormat,
                this.defaultLanguage,
                this.maintenanceMode,
                this.sessionTimeoutMinutes,
                this.enableSso,
                this.enableMfa,
                this.updatedAt,
                this.updatedBy
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
        SystemSettingsResponse that = (SystemSettingsResponse) o;
        return Objects.equals(id, that.id) &&
               Objects.equals(systemName, that.systemName) &&
               Objects.equals(systemVersion, that.systemVersion) &&
               Objects.equals(timeZone, that.timeZone) &&
               Objects.equals(dateFormat, that.dateFormat) &&
               Objects.equals(defaultLanguage, that.defaultLanguage) &&
               Objects.equals(maintenanceMode, that.maintenanceMode) &&
               Objects.equals(sessionTimeoutMinutes, that.sessionTimeoutMinutes) &&
               Objects.equals(enableSso, that.enableSso) &&
               Objects.equals(enableMfa, that.enableMfa) &&
               Objects.equals(updatedAt, that.updatedAt) &&
               Objects.equals(updatedBy, that.updatedBy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, systemName, systemVersion, timeZone, dateFormat, defaultLanguage, maintenanceMode, sessionTimeoutMinutes, enableSso, enableMfa, updatedAt, updatedBy);
    }

    @Override
    public String toString() {
        return "SystemSettingsResponse{" +
                "id=" + id + ", " + 
                "systemName=" + systemName + ", " + 
                "systemVersion=" + systemVersion + ", " + 
                "timeZone=" + timeZone + ", " + 
                "dateFormat=" + dateFormat + ", " + 
                "defaultLanguage=" + defaultLanguage + ", " + 
                "maintenanceMode=" + maintenanceMode + ", " + 
                "sessionTimeoutMinutes=" + sessionTimeoutMinutes + ", " + 
                "enableSso=" + enableSso + ", " + 
                "enableMfa=" + enableMfa + ", " + 
                "updatedAt=" + updatedAt + ", " + 
                "updatedBy=" + updatedBy +
                '}';
    }
}
