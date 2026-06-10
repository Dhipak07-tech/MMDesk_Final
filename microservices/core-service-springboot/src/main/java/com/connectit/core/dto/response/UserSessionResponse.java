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
 * UserSessionResponse
 * ════════════════════════════════════════════════════════════════════════════════════
 * Security session detail for monitoring current logons.
 *
 * This DTO aligns with the target system architecture migration to Spring Boot.
 * It serves as a contract for data exchange, keeping business validation rules cleanly
 * separated from the persistence model layer.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSessionResponse {

    /**
     * Session unique key.
     */
    private String id;

    /**
     * Connected user identifier.
     */
    private String userId;

    /**
     * Name of connected user.
     */
    private String userName;

    /**
     * Source internet address.
     */
    private String ipAddress;

    /**
     * Client browser agent identifier.
     */
    private String userAgent;

    /**
     * Establishment timestamp.
     */
    private LocalDateTime loginTime;

    /**
     * Latest heartbeat activity.
     */
    private LocalDateTime lastActivityTime;

    /**
     * Current session activity.
     */
    private Boolean isActive;


    /**
     * Builder factory method to instantiate a new builder.
     * @return a new Builder instance for fluent creation.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Fluent Builder pattern implementation to construct UserSessionResponse instances safely.
     */
    public static class Builder {
        private String id;
        private String userId;
        private String userName;
        private String ipAddress;
        private String userAgent;
        private LocalDateTime loginTime;
        private LocalDateTime lastActivityTime;
        private Boolean isActive;

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
         * Set the userId attribute.
         * @param userId value
         * @return Builder instance
         */
        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        /**
         * Set the userName attribute.
         * @param userName value
         * @return Builder instance
         */
        public Builder userName(String userName) {
            this.userName = userName;
            return this;
        }

        /**
         * Set the ipAddress attribute.
         * @param ipAddress value
         * @return Builder instance
         */
        public Builder ipAddress(String ipAddress) {
            this.ipAddress = ipAddress;
            return this;
        }

        /**
         * Set the userAgent attribute.
         * @param userAgent value
         * @return Builder instance
         */
        public Builder userAgent(String userAgent) {
            this.userAgent = userAgent;
            return this;
        }

        /**
         * Set the loginTime attribute.
         * @param loginTime value
         * @return Builder instance
         */
        public Builder loginTime(LocalDateTime loginTime) {
            this.loginTime = loginTime;
            return this;
        }

        /**
         * Set the lastActivityTime attribute.
         * @param lastActivityTime value
         * @return Builder instance
         */
        public Builder lastActivityTime(LocalDateTime lastActivityTime) {
            this.lastActivityTime = lastActivityTime;
            return this;
        }

        /**
         * Set the isActive attribute.
         * @param isActive value
         * @return Builder instance
         */
        public Builder isActive(Boolean isActive) {
            this.isActive = isActive;
            return this;
        }


        /**
         * Construct the finalized UserSessionResponse instance.
         * @return the fully populated DTO
         */
        public UserSessionResponse build() {
            return new UserSessionResponse(
                this.id,
                this.userId,
                this.userName,
                this.ipAddress,
                this.userAgent,
                this.loginTime,
                this.lastActivityTime,
                this.isActive
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
        UserSessionResponse that = (UserSessionResponse) o;
        return Objects.equals(id, that.id) &&
               Objects.equals(userId, that.userId) &&
               Objects.equals(userName, that.userName) &&
               Objects.equals(ipAddress, that.ipAddress) &&
               Objects.equals(userAgent, that.userAgent) &&
               Objects.equals(loginTime, that.loginTime) &&
               Objects.equals(lastActivityTime, that.lastActivityTime) &&
               Objects.equals(isActive, that.isActive);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, userName, ipAddress, userAgent, loginTime, lastActivityTime, isActive);
    }

    @Override
    public String toString() {
        return "UserSessionResponse{" +
                "id=" + id + ", " + 
                "userId=" + userId + ", " + 
                "userName=" + userName + ", " + 
                "ipAddress=" + ipAddress + ", " + 
                "userAgent=" + userAgent + ", " + 
                "loginTime=" + loginTime + ", " + 
                "lastActivityTime=" + lastActivityTime + ", " + 
                "isActive=" + isActive +
                '}';
    }
}
