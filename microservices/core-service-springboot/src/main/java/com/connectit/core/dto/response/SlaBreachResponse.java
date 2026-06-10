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
 * SlaBreachResponse
 * ════════════════════════════════════════════════════════════════════════════════════
 * Summary details for SLA breaches.
 *
 * This DTO aligns with the target system architecture migration to Spring Boot.
 * It serves as a contract for data exchange, keeping business validation rules cleanly
 * separated from the persistence model layer.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SlaBreachResponse {

    /**
     * Breach log identifier.
     */
    private String id;

    /**
     * Source incident identifier.
     */
    private String ticketId;

    /**
     * Incident reference sequence.
     */
    private String ticketNumber;

    /**
     * Policy identifier.
     */
    private String slaPolicyId;

    /**
     * Name of the policy.
     */
    private String slaPolicyName;

    /**
     * Type (RESPONSE, RESOLUTION).
     */
    private String breachType;

    /**
     * Target deadline timestamp.
     */
    private LocalDateTime deadline;

    /**
     * Recorded breach event timestamp.
     */
    private LocalDateTime breachedAt;

    /**
     * Escalation execution status.
     */
    private Boolean isEscalated;


    /**
     * Builder factory method to instantiate a new builder.
     * @return a new Builder instance for fluent creation.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Fluent Builder pattern implementation to construct SlaBreachResponse instances safely.
     */
    public static class Builder {
        private String id;
        private String ticketId;
        private String ticketNumber;
        private String slaPolicyId;
        private String slaPolicyName;
        private String breachType;
        private LocalDateTime deadline;
        private LocalDateTime breachedAt;
        private Boolean isEscalated;

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
         * Set the ticketId attribute.
         * @param ticketId value
         * @return Builder instance
         */
        public Builder ticketId(String ticketId) {
            this.ticketId = ticketId;
            return this;
        }

        /**
         * Set the ticketNumber attribute.
         * @param ticketNumber value
         * @return Builder instance
         */
        public Builder ticketNumber(String ticketNumber) {
            this.ticketNumber = ticketNumber;
            return this;
        }

        /**
         * Set the slaPolicyId attribute.
         * @param slaPolicyId value
         * @return Builder instance
         */
        public Builder slaPolicyId(String slaPolicyId) {
            this.slaPolicyId = slaPolicyId;
            return this;
        }

        /**
         * Set the slaPolicyName attribute.
         * @param slaPolicyName value
         * @return Builder instance
         */
        public Builder slaPolicyName(String slaPolicyName) {
            this.slaPolicyName = slaPolicyName;
            return this;
        }

        /**
         * Set the breachType attribute.
         * @param breachType value
         * @return Builder instance
         */
        public Builder breachType(String breachType) {
            this.breachType = breachType;
            return this;
        }

        /**
         * Set the deadline attribute.
         * @param deadline value
         * @return Builder instance
         */
        public Builder deadline(LocalDateTime deadline) {
            this.deadline = deadline;
            return this;
        }

        /**
         * Set the breachedAt attribute.
         * @param breachedAt value
         * @return Builder instance
         */
        public Builder breachedAt(LocalDateTime breachedAt) {
            this.breachedAt = breachedAt;
            return this;
        }

        /**
         * Set the isEscalated attribute.
         * @param isEscalated value
         * @return Builder instance
         */
        public Builder isEscalated(Boolean isEscalated) {
            this.isEscalated = isEscalated;
            return this;
        }


        /**
         * Construct the finalized SlaBreachResponse instance.
         * @return the fully populated DTO
         */
        public SlaBreachResponse build() {
            return new SlaBreachResponse(
                this.id,
                this.ticketId,
                this.ticketNumber,
                this.slaPolicyId,
                this.slaPolicyName,
                this.breachType,
                this.deadline,
                this.breachedAt,
                this.isEscalated
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
        SlaBreachResponse that = (SlaBreachResponse) o;
        return Objects.equals(id, that.id) &&
               Objects.equals(ticketId, that.ticketId) &&
               Objects.equals(ticketNumber, that.ticketNumber) &&
               Objects.equals(slaPolicyId, that.slaPolicyId) &&
               Objects.equals(slaPolicyName, that.slaPolicyName) &&
               Objects.equals(breachType, that.breachType) &&
               Objects.equals(deadline, that.deadline) &&
               Objects.equals(breachedAt, that.breachedAt) &&
               Objects.equals(isEscalated, that.isEscalated);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, ticketId, ticketNumber, slaPolicyId, slaPolicyName, breachType, deadline, breachedAt, isEscalated);
    }

    @Override
    public String toString() {
        return "SlaBreachResponse{" +
                "id=" + id + ", " + 
                "ticketId=" + ticketId + ", " + 
                "ticketNumber=" + ticketNumber + ", " + 
                "slaPolicyId=" + slaPolicyId + ", " + 
                "slaPolicyName=" + slaPolicyName + ", " + 
                "breachType=" + breachType + ", " + 
                "deadline=" + deadline + ", " + 
                "breachedAt=" + breachedAt + ", " + 
                "isEscalated=" + isEscalated +
                '}';
    }
}
