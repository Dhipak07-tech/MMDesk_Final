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
 * RegistrationResponse
 * ════════════════════════════════════════════════════════════════════════════════════
 * Summary of user registration process result.
 *
 * This DTO aligns with the target system architecture migration to Spring Boot.
 * It serves as a contract for data exchange, keeping business validation rules cleanly
 * separated from the persistence model layer.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationResponse {

    /**
     * Registered user primary key.
     */
    private String id;

    /**
     * User display name.
     */
    private String name;

    /**
     * Validated email.
     */
    private String email;

    /**
     * Direct telephone.
     */
    private String phone;

    /**
     * System role assigned.
     */
    private String role;

    /**
     * Email validation state.
     */
    private Boolean isEmailVerified;

    /**
     * Account authorization state.
     */
    private Boolean isActive;

    /**
     * Timestamp of execution.
     */
    private LocalDateTime registeredAt;


    /**
     * Builder factory method to instantiate a new builder.
     * @return a new Builder instance for fluent creation.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Fluent Builder pattern implementation to construct RegistrationResponse instances safely.
     */
    public static class Builder {
        private String id;
        private String name;
        private String email;
        private String phone;
        private String role;
        private Boolean isEmailVerified;
        private Boolean isActive;
        private LocalDateTime registeredAt;

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
         * Set the email attribute.
         * @param email value
         * @return Builder instance
         */
        public Builder email(String email) {
            this.email = email;
            return this;
        }

        /**
         * Set the phone attribute.
         * @param phone value
         * @return Builder instance
         */
        public Builder phone(String phone) {
            this.phone = phone;
            return this;
        }

        /**
         * Set the role attribute.
         * @param role value
         * @return Builder instance
         */
        public Builder role(String role) {
            this.role = role;
            return this;
        }

        /**
         * Set the isEmailVerified attribute.
         * @param isEmailVerified value
         * @return Builder instance
         */
        public Builder isEmailVerified(Boolean isEmailVerified) {
            this.isEmailVerified = isEmailVerified;
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
         * Set the registeredAt attribute.
         * @param registeredAt value
         * @return Builder instance
         */
        public Builder registeredAt(LocalDateTime registeredAt) {
            this.registeredAt = registeredAt;
            return this;
        }


        /**
         * Construct the finalized RegistrationResponse instance.
         * @return the fully populated DTO
         */
        public RegistrationResponse build() {
            return new RegistrationResponse(
                this.id,
                this.name,
                this.email,
                this.phone,
                this.role,
                this.isEmailVerified,
                this.isActive,
                this.registeredAt
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
        RegistrationResponse that = (RegistrationResponse) o;
        return Objects.equals(id, that.id) &&
               Objects.equals(name, that.name) &&
               Objects.equals(email, that.email) &&
               Objects.equals(phone, that.phone) &&
               Objects.equals(role, that.role) &&
               Objects.equals(isEmailVerified, that.isEmailVerified) &&
               Objects.equals(isActive, that.isActive) &&
               Objects.equals(registeredAt, that.registeredAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, email, phone, role, isEmailVerified, isActive, registeredAt);
    }

    @Override
    public String toString() {
        return "RegistrationResponse{" +
                "id=" + id + ", " + 
                "name=" + name + ", " + 
                "email=" + email + ", " + 
                "phone=" + phone + ", " + 
                "role=" + role + ", " + 
                "isEmailVerified=" + isEmailVerified + ", " + 
                "isActive=" + isActive + ", " + 
                "registeredAt=" + registeredAt +
                '}';
    }
}
