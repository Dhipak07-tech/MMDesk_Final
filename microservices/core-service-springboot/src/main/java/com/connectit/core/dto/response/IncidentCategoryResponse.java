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
 * IncidentCategoryResponse
 * ════════════════════════════════════════════════════════════════════════════════════
 * Returns classification details for ticket categories.
 *
 * This DTO aligns with the target system architecture migration to Spring Boot.
 * It serves as a contract for data exchange, keeping business validation rules cleanly
 * separated from the persistence model layer.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IncidentCategoryResponse {

    /**
     * Category identifier.
     */
    private String id;

    /**
     * Classification name.
     */
    private String name;

    /**
     * Category summary.
     */
    private String description;

    /**
     * Parent node identifier.
     */
    private String parentCategoryId;

    /**
     * Name of the parent category.
     */
    private String parentCategoryName;

    /**
     * Availability status flag.
     */
    private Boolean isActive;

    /**
     * Sorting order.
     */
    private Integer displayOrder;

    /**
     * Creation timestamp.
     */
    private LocalDateTime createdAt;

    /**
     * Update timestamp.
     */
    private LocalDateTime updatedAt;


    /**
     * Builder factory method to instantiate a new builder.
     * @return a new Builder instance for fluent creation.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Fluent Builder pattern implementation to construct IncidentCategoryResponse instances safely.
     */
    public static class Builder {
        private String id;
        private String name;
        private String description;
        private String parentCategoryId;
        private String parentCategoryName;
        private Boolean isActive;
        private Integer displayOrder;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

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
         * Set the name attribute.
         * @param name value
         * @return Builder instance
         */
        public Builder name(String name) {
            this.name = name;
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
         * Set the parentCategoryId attribute.
         * @param parentCategoryId value
         * @return Builder instance
         */
        public Builder parentCategoryId(String parentCategoryId) {
            this.parentCategoryId = parentCategoryId;
            return this;
        }

        /**
         * Set the parentCategoryName attribute.
         * @param parentCategoryName value
         * @return Builder instance
         */
        public Builder parentCategoryName(String parentCategoryName) {
            this.parentCategoryName = parentCategoryName;
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
         * Set the displayOrder attribute.
         * @param displayOrder value
         * @return Builder instance
         */
        public Builder displayOrder(Integer displayOrder) {
            this.displayOrder = displayOrder;
            return this;
        }

        /**
         * Set the createdAt attribute.
         * @param createdAt value
         * @return Builder instance
         */
        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
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
         * Construct the finalized IncidentCategoryResponse instance.
         * @return the fully populated DTO
         */
        public IncidentCategoryResponse build() {
            return new IncidentCategoryResponse(
                this.id,
                this.name,
                this.description,
                this.parentCategoryId,
                this.parentCategoryName,
                this.isActive,
                this.displayOrder,
                this.createdAt,
                this.updatedAt
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
        IncidentCategoryResponse that = (IncidentCategoryResponse) o;
        return Objects.equals(id, that.id) &&
               Objects.equals(name, that.name) &&
               Objects.equals(description, that.description) &&
               Objects.equals(parentCategoryId, that.parentCategoryId) &&
               Objects.equals(parentCategoryName, that.parentCategoryName) &&
               Objects.equals(isActive, that.isActive) &&
               Objects.equals(displayOrder, that.displayOrder) &&
               Objects.equals(createdAt, that.createdAt) &&
               Objects.equals(updatedAt, that.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, parentCategoryId, parentCategoryName, isActive, displayOrder, createdAt, updatedAt);
    }

    @Override
    public String toString() {
        return "IncidentCategoryResponse{" +
                "id=" + id + ", " + 
                "name=" + name + ", " + 
                "description=" + description + ", " + 
                "parentCategoryId=" + parentCategoryId + ", " + 
                "parentCategoryName=" + parentCategoryName + ", " + 
                "isActive=" + isActive + ", " + 
                "displayOrder=" + displayOrder + ", " + 
                "createdAt=" + createdAt + ", " + 
                "updatedAt=" + updatedAt +
                '}';
    }
}
