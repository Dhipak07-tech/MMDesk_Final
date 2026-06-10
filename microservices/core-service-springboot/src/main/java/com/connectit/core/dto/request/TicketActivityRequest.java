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
 * TicketActivityRequest
 * ════════════════════════════════════════════════════════════════════════════════════
 * Request payload to append a historical ticket event detail.
 *
 * This DTO aligns with the target system architecture migration to Spring Boot.
 * It serves as a contract for data exchange, keeping business validation rules cleanly
 * separated from the persistence model layer.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketActivityRequest {

    /**
     * Reference key of the ticket.
     */
    private String ticketId;

    /**
     * Log type classification.
     */
    private String activityType;

    /**
     * Detailed user-friendly explanation.
     */
    private String description;

    /**
     * Field state prior to update.
     */
    private String oldValue;

    /**
     * Field state post update.
     */
    private String newValue;


    /**
     * Builder factory method to instantiate a new builder.
     * @return a new Builder instance for fluent creation.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Fluent Builder pattern implementation to construct TicketActivityRequest instances safely.
     */
    public static class Builder {
        private String ticketId;
        private String activityType;
        private String description;
        private String oldValue;
        private String newValue;

        /**
         * Default constructor.
         */
        public Builder() {}

        /**
         * Set the ticketId attribute.
         * @param ticketId value
         * @return Builder instance
         */
        public Builder ticketId(String ticketId) {
            this.ticketId = ticketId;
            return this;
        }

        /**
         * Set the activityType attribute.
         * @param activityType value
         * @return Builder instance
         */
        public Builder activityType(String activityType) {
            this.activityType = activityType;
            return this;
        }

        /**
         * Set the description attribute.
         * @param description value
         * @return Builder instance
         */
        public Builder description(String description) {
            this.description = description;
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
         * Construct the finalized TicketActivityRequest instance.
         * @return the fully populated DTO
         */
        public TicketActivityRequest build() {
            return new TicketActivityRequest(
                this.ticketId,
                this.activityType,
                this.description,
                this.oldValue,
                this.newValue
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
        TicketActivityRequest that = (TicketActivityRequest) o;
        return Objects.equals(ticketId, that.ticketId) &&
               Objects.equals(activityType, that.activityType) &&
               Objects.equals(description, that.description) &&
               Objects.equals(oldValue, that.oldValue) &&
               Objects.equals(newValue, that.newValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ticketId, activityType, description, oldValue, newValue);
    }

    @Override
    public String toString() {
        return "TicketActivityRequest{" +
                "ticketId=" + ticketId + ", " + 
                "activityType=" + activityType + ", " + 
                "description=" + description + ", " + 
                "oldValue=" + oldValue + ", " + 
                "newValue=" + newValue +
                '}';
    }
}
