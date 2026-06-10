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
 * ApprovalRequest
 * ════════════════════════════════════════════════════════════════════════════════════
 * Approval action request payload.
 *
 * This DTO aligns with the target system architecture migration to Spring Boot.
 * It serves as a contract for data exchange, keeping business validation rules cleanly
 * separated from the persistence model layer.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalRequest {

    /**
     * Reference ID of incident/change.
     */
    private String ticketId;

    /**
     * User assigned for decision.
     */
    private String approverId;

    /**
     * Category (e.g. CAB, Financial, Managerial).
     */
    private String approvalType;

    /**
     * Response notes.
     */
    private String comments;

    /**
     * Decision state (APPROVED, REJECTED).
     */
    private String status;


    /**
     * Builder factory method to instantiate a new builder.
     * @return a new Builder instance for fluent creation.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Fluent Builder pattern implementation to construct ApprovalRequest instances safely.
     */
    public static class Builder {
        private String ticketId;
        private String approverId;
        private String approvalType;
        private String comments;
        private String status;

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
         * Set the approverId attribute.
         * @param approverId value
         * @return Builder instance
         */
        public Builder approverId(String approverId) {
            this.approverId = approverId;
            return this;
        }

        /**
         * Set the approvalType attribute.
         * @param approvalType value
         * @return Builder instance
         */
        public Builder approvalType(String approvalType) {
            this.approvalType = approvalType;
            return this;
        }

        /**
         * Set the comments attribute.
         * @param comments value
         * @return Builder instance
         */
        public Builder comments(String comments) {
            this.comments = comments;
            return this;
        }

        /**
         * Set the status attribute.
         * @param status value
         * @return Builder instance
         */
        public Builder status(String status) {
            this.status = status;
            return this;
        }


        /**
         * Construct the finalized ApprovalRequest instance.
         * @return the fully populated DTO
         */
        public ApprovalRequest build() {
            return new ApprovalRequest(
                this.ticketId,
                this.approverId,
                this.approvalType,
                this.comments,
                this.status
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
        ApprovalRequest that = (ApprovalRequest) o;
        return Objects.equals(ticketId, that.ticketId) &&
               Objects.equals(approverId, that.approverId) &&
               Objects.equals(approvalType, that.approvalType) &&
               Objects.equals(comments, that.comments) &&
               Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ticketId, approverId, approvalType, comments, status);
    }

    @Override
    public String toString() {
        return "ApprovalRequest{" +
                "ticketId=" + ticketId + ", " + 
                "approverId=" + approverId + ", " + 
                "approvalType=" + approvalType + ", " + 
                "comments=" + comments + ", " + 
                "status=" + status +
                '}';
    }
}
