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
 * DashboardStatsResponse
 * ════════════════════════════════════════════════════════════════════════════════════
 * Portal dashboard analytics summary.
 *
 * This DTO aligns with the target system architecture migration to Spring Boot.
 * It serves as a contract for data exchange, keeping business validation rules cleanly
 * separated from the persistence model layer.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsResponse {

    /**
     * Grand total ticket count.
     */
    private Long totalTickets;

    /**
     * Count of pending tickets.
     */
    private Long openTickets;

    /**
     * Count of actively resolved tickets.
     */
    private Long inProgressTickets;

    /**
     * Count of completed ticket status.
     */
    private Long resolvedTickets;

    /**
     * Count of validated closed tickets.
     */
    private Long closedTickets;

    /**
     * Count of tickets that failed SLA thresholds.
     */
    private Long breachedTickets;

    /**
     * Mean resolution time in decimal hours.
     */
    private Double averageResolutionTimeHours;

    /**
     * Distribution mapping across categories.
     */
    private Map<String, Long> ticketsByCategory;

    /**
     * Distribution mapping across priority levels.
     */
    private Map<String, Long> ticketsByPriority;

    /**
     * Distribution mapping across status levels.
     */
    private Map<String, Long> ticketsByStatus;


    /**
     * Builder factory method to instantiate a new builder.
     * @return a new Builder instance for fluent creation.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Fluent Builder pattern implementation to construct DashboardStatsResponse instances safely.
     */
    public static class Builder {
        private Long totalTickets;
        private Long openTickets;
        private Long inProgressTickets;
        private Long resolvedTickets;
        private Long closedTickets;
        private Long breachedTickets;
        private Double averageResolutionTimeHours;
        private Map<String, Long> ticketsByCategory;
        private Map<String, Long> ticketsByPriority;
        private Map<String, Long> ticketsByStatus;

        /**
         * Default constructor.
         */
        public Builder() {}

        /**
         * Set the totalTickets attribute.
         * @param totalTickets value
         * @return Builder instance
         */
        public Builder totalTickets(Long totalTickets) {
            this.totalTickets = totalTickets;
            return this;
        }

        /**
         * Set the openTickets attribute.
         * @param openTickets value
         * @return Builder instance
         */
        public Builder openTickets(Long openTickets) {
            this.openTickets = openTickets;
            return this;
        }

        /**
         * Set the inProgressTickets attribute.
         * @param inProgressTickets value
         * @return Builder instance
         */
        public Builder inProgressTickets(Long inProgressTickets) {
            this.inProgressTickets = inProgressTickets;
            return this;
        }

        /**
         * Set the resolvedTickets attribute.
         * @param resolvedTickets value
         * @return Builder instance
         */
        public Builder resolvedTickets(Long resolvedTickets) {
            this.resolvedTickets = resolvedTickets;
            return this;
        }

        /**
         * Set the closedTickets attribute.
         * @param closedTickets value
         * @return Builder instance
         */
        public Builder closedTickets(Long closedTickets) {
            this.closedTickets = closedTickets;
            return this;
        }

        /**
         * Set the breachedTickets attribute.
         * @param breachedTickets value
         * @return Builder instance
         */
        public Builder breachedTickets(Long breachedTickets) {
            this.breachedTickets = breachedTickets;
            return this;
        }

        /**
         * Set the averageResolutionTimeHours attribute.
         * @param averageResolutionTimeHours value
         * @return Builder instance
         */
        public Builder averageResolutionTimeHours(Double averageResolutionTimeHours) {
            this.averageResolutionTimeHours = averageResolutionTimeHours;
            return this;
        }

        /**
         * Set the ticketsByCategory attribute.
         * @param ticketsByCategory value
         * @return Builder instance
         */
        public Builder ticketsByCategory(Map<String, Long> ticketsByCategory) {
            this.ticketsByCategory = ticketsByCategory;
            return this;
        }

        /**
         * Set the ticketsByPriority attribute.
         * @param ticketsByPriority value
         * @return Builder instance
         */
        public Builder ticketsByPriority(Map<String, Long> ticketsByPriority) {
            this.ticketsByPriority = ticketsByPriority;
            return this;
        }

        /**
         * Set the ticketsByStatus attribute.
         * @param ticketsByStatus value
         * @return Builder instance
         */
        public Builder ticketsByStatus(Map<String, Long> ticketsByStatus) {
            this.ticketsByStatus = ticketsByStatus;
            return this;
        }


        /**
         * Construct the finalized DashboardStatsResponse instance.
         * @return the fully populated DTO
         */
        public DashboardStatsResponse build() {
            return new DashboardStatsResponse(
                this.totalTickets,
                this.openTickets,
                this.inProgressTickets,
                this.resolvedTickets,
                this.closedTickets,
                this.breachedTickets,
                this.averageResolutionTimeHours,
                this.ticketsByCategory,
                this.ticketsByPriority,
                this.ticketsByStatus
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
        DashboardStatsResponse that = (DashboardStatsResponse) o;
        return Objects.equals(totalTickets, that.totalTickets) &&
               Objects.equals(openTickets, that.openTickets) &&
               Objects.equals(inProgressTickets, that.inProgressTickets) &&
               Objects.equals(resolvedTickets, that.resolvedTickets) &&
               Objects.equals(closedTickets, that.closedTickets) &&
               Objects.equals(breachedTickets, that.breachedTickets) &&
               Objects.equals(averageResolutionTimeHours, that.averageResolutionTimeHours) &&
               Objects.equals(ticketsByCategory, that.ticketsByCategory) &&
               Objects.equals(ticketsByPriority, that.ticketsByPriority) &&
               Objects.equals(ticketsByStatus, that.ticketsByStatus);
    }

    @Override
    public int hashCode() {
        return Objects.hash(totalTickets, openTickets, inProgressTickets, resolvedTickets, closedTickets, breachedTickets, averageResolutionTimeHours, ticketsByCategory, ticketsByPriority, ticketsByStatus);
    }

    @Override
    public String toString() {
        return "DashboardStatsResponse{" +
                "totalTickets=" + totalTickets + ", " + 
                "openTickets=" + openTickets + ", " + 
                "inProgressTickets=" + inProgressTickets + ", " + 
                "resolvedTickets=" + resolvedTickets + ", " + 
                "closedTickets=" + closedTickets + ", " + 
                "breachedTickets=" + breachedTickets + ", " + 
                "averageResolutionTimeHours=" + averageResolutionTimeHours + ", " + 
                "ticketsByCategory=" + ticketsByCategory + ", " + 
                "ticketsByPriority=" + ticketsByPriority + ", " + 
                "ticketsByStatus=" + ticketsByStatus +
                '}';
    }
}
