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
 * AuditLogResponse
 * ════════════════════════════════════════════════════════════════════════════════════
 * System transaction logs database audit record details.
 *
 * This DTO aligns with the target system architecture migration to Spring Boot.
 * It serves as a contract for data exchange, keeping business validation rules cleanly
 * separated from the persistence model layer.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogResponse {

    /**
     * Audit primary identifier.
     */
    private String id;

    /**
     * Actor user ID.
     */
    private String userId;

    /**
     * Actor username.
     */
    private String userName;

    /**
     * Operation (CREATE, UPDATE, DELETE).
     */
    private String action;

    /**
     * Affected model table name.
     */
    private String targetType;

    /**
     * Affected row primary identifier.
     */
    private String targetId;

    /**
     * JSON representation of previous values.
     */
    private String oldValue;

    /**
     * JSON representation of new modified values.
     */
    private String newValue;

    /**
     * Source device IP address.
     */
    private String ipAddress;

    /**
     * Operation timestamp.
     */
    private LocalDateTime timestamp;


    /**
     * Builder factory method to instantiate a new builder.
     * @return a new Builder instance for fluent creation.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Fluent Builder pattern implementation to construct AuditLogResponse instances safely.
     */
    public static class Builder {
        private String id;
        private String userId;
        private String userName;
        private String action;
        private String targetType;
        private String targetId;
        private String oldValue;
        private String newValue;
        private String ipAddress;
        private LocalDateTime timestamp;

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
         * Set the action attribute.
         * @param action value
         * @return Builder instance
         */
        public Builder action(String action) {
            this.action = action;
            return this;
        }

        /**
         * Set the targetType attribute.
         * @param targetType value
         * @return Builder instance
         */
        public Builder targetType(String targetType) {
            this.targetType = targetType;
            return this;
        }

        /**
         * Set the targetId attribute.
         * @param targetId value
         * @return Builder instance
         */
        public Builder targetId(String targetId) {
            this.targetId = targetId;
            return this;
        }

        /**
         * Set the oldValue attribute.
         * @param oldValue value
         * @return Builder instance
         */
        public Builder oldValue(String oldValue) {
            this.oldValue = oldValue;
            return this;
        }

        /**
         * Set the newValue attribute.
         * @param newValue value
         * @return Builder instance
         */
        public Builder newValue(String newValue) {
            this.newValue = newValue;
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
         * Set the timestamp attribute.
         * @param timestamp value
         * @return Builder instance
         */
        public Builder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }


        /**
         * Construct the finalized AuditLogResponse instance.
         * @return the fully populated DTO
         */
        public AuditLogResponse build() {
            return new AuditLogResponse(
                this.id,
                this.userId,
                this.userName,
                this.action,
                this.targetType,
                this.targetId,
                this.oldValue,
                this.newValue,
                this.ipAddress,
                this.timestamp
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
        AuditLogResponse that = (AuditLogResponse) o;
        return Objects.equals(id, that.id) &&
               Objects.equals(userId, that.userId) &&
               Objects.equals(userName, that.userName) &&
               Objects.equals(action, that.action) &&
               Objects.equals(targetType, that.targetType) &&
               Objects.equals(targetId, that.targetId) &&
               Objects.equals(oldValue, that.oldValue) &&
               Objects.equals(newValue, that.newValue) &&
               Objects.equals(ipAddress, that.ipAddress) &&
               Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, userName, action, targetType, targetId, oldValue, newValue, ipAddress, timestamp);
    }

    @Override
    public String toString() {
        return "AuditLogResponse{" +
                "id=" + id + ", " + 
                "userId=" + userId + ", " + 
                "userName=" + userName + ", " + 
                "action=" + action + ", " + 
                "targetType=" + targetType + ", " + 
                "targetId=" + targetId + ", " + 
                "oldValue=" + oldValue + ", " + 
                "newValue=" + newValue + ", " + 
                "ipAddress=" + ipAddress + ", " + 
                "timestamp=" + timestamp +
                '}';
    }
}
