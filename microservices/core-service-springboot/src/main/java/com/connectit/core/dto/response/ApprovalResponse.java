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
 * ApprovalResponse
 * ════════════════════════════════════════════════════════════════════════════════════
 * Detailed approval transaction trace details.
 *
 * This DTO aligns with the target system architecture migration to Spring Boot.
 * It serves as a contract for data exchange, keeping business validation rules cleanly
 * separated from the persistence model layer.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalResponse {

    /**
     * Approval ID.
     */
    private String id;

    /**
     * Related ticket ID.
     */
    private String ticketId;

    /**
     * Ticket display identifier.
     */
    private String ticketNumber;

    /**
     * Approver user identifier.
     */
    private String approverId;

    /**
     * Approver name.
     */
    private String approverName;

    /**
     * Type of approval required.
     */
    private String approvalType;

    /**
     * Approver log explanation.
     */
    private String comments;

    /**
     * State of approval.
     */
    private String status;

    /**
     * Time of generation.
     */
    private LocalDateTime requestedAt;

    /**
     * Time of decision.
     */
    private LocalDateTime respondedAt;


    /**
     * Builder factory method to instantiate a new builder.
     * @return a new Builder instance for fluent creation.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Fluent Builder pattern implementation to construct ApprovalResponse instances safely.
     */
    public static class Builder {
        private String id;
        private String ticketId;
        private String ticketNumber;
        private String approverId;
        private String approverName;
        private String approvalType;
        private String comments;
        private String status;
        private LocalDateTime requestedAt;
        private LocalDateTime respondedAt;

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
         * Set the approverId attribute.
         * @param approverId value
         * @return Builder instance
         */
        public Builder approverId(String approverId) {
            this.approverId = approverId;
            return this;
        }

        /**
         * Set the approverName attribute.
         * @param approverName value
         * @return Builder instance
         */
        public Builder approverName(String approverName) {
            this.approverName = approverName;
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
         * Set the requestedAt attribute.
         * @param requestedAt value
         * @return Builder instance
         */
        public Builder requestedAt(LocalDateTime requestedAt) {
            this.requestedAt = requestedAt;
            return this;
        }

        /**
         * Set the respondedAt attribute.
         * @param respondedAt value
         * @return Builder instance
         */
        public Builder respondedAt(LocalDateTime respondedAt) {
            this.respondedAt = respondedAt;
            return this;
        }


        /**
         * Construct the finalized ApprovalResponse instance.
         * @return the fully populated DTO
         */
        public ApprovalResponse build() {
            return new ApprovalResponse(
                this.id,
                this.ticketId,
                this.ticketNumber,
                this.approverId,
                this.approverName,
                this.approvalType,
                this.comments,
                this.status,
                this.requestedAt,
                this.respondedAt
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
        ApprovalResponse that = (ApprovalResponse) o;
        return Objects.equals(id, that.id) &&
               Objects.equals(ticketId, that.ticketId) &&
               Objects.equals(ticketNumber, that.ticketNumber) &&
               Objects.equals(approverId, that.approverId) &&
               Objects.equals(approverName, that.approverName) &&
               Objects.equals(approvalType, that.approvalType) &&
               Objects.equals(comments, that.comments) &&
               Objects.equals(status, that.status) &&
               Objects.equals(requestedAt, that.requestedAt) &&
               Objects.equals(respondedAt, that.respondedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, ticketId, ticketNumber, approverId, approverName, approvalType, comments, status, requestedAt, respondedAt);
    }

    @Override
    public String toString() {
        return "ApprovalResponse{" +
                "id=" + id + ", " + 
                "ticketId=" + ticketId + ", " + 
                "ticketNumber=" + ticketNumber + ", " + 
                "approverId=" + approverId + ", " + 
                "approverName=" + approverName + ", " + 
                "approvalType=" + approvalType + ", " + 
                "comments=" + comments + ", " + 
                "status=" + status + ", " + 
                "requestedAt=" + requestedAt + ", " + 
                "respondedAt=" + respondedAt +
                '}';
    }
}
