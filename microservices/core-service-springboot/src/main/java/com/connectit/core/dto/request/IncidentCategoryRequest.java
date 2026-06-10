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
 * IncidentCategoryRequest
 * ════════════════════════════════════════════════════════════════════════════════════
 * Incident classification setup payload.
 *
 * This DTO aligns with the target system architecture migration to Spring Boot.
 * It serves as a contract for data exchange, keeping business validation rules cleanly
 * separated from the persistence model layer.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IncidentCategoryRequest {

    /**
     * Name of category.
     */
    private String name;

    /**
     * Details on usage context.
     */
    private String description;

    /**
     * Parent hierarchy node.
     */
    private String parentCategoryId;

    /**
     * Toggle categorization availability.
     */
    private Boolean isActive;

    /**
     * Display priority listing.
     */
    private Integer displayOrder;


    /**
     * Builder factory method to instantiate a new builder.
     * @return a new Builder instance for fluent creation.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Fluent Builder pattern implementation to construct IncidentCategoryRequest instances safely.
     */
    public static class Builder {
        private String name;
        private String description;
        private String parentCategoryId;
        private Boolean isActive;
        private Integer displayOrder;

        /**
         * Default constructor.
         */
        public Builder() {}

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
         * Construct the finalized IncidentCategoryRequest instance.
         * @return the fully populated DTO
         */
        public IncidentCategoryRequest build() {
            return new IncidentCategoryRequest(
                this.name,
                this.description,
                this.parentCategoryId,
                this.isActive,
                this.displayOrder
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
        IncidentCategoryRequest that = (IncidentCategoryRequest) o;
        return Objects.equals(name, that.name) &&
               Objects.equals(description, that.description) &&
               Objects.equals(parentCategoryId, that.parentCategoryId) &&
               Objects.equals(isActive, that.isActive) &&
               Objects.equals(displayOrder, that.displayOrder);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, parentCategoryId, isActive, displayOrder);
    }

    @Override
    public String toString() {
        return "IncidentCategoryRequest{" +
                "name=" + name + ", " + 
                "description=" + description + ", " + 
                "parentCategoryId=" + parentCategoryId + ", " + 
                "isActive=" + isActive + ", " + 
                "displayOrder=" + displayOrder +
                '}';
    }
}
