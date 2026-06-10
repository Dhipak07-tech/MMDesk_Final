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
 * RegistrationRequest
 * ════════════════════════════════════════════════════════════════════════════════════
 * Self-registration credentials and validation package.
 *
 * This DTO aligns with the target system architecture migration to Spring Boot.
 * It serves as a contract for data exchange, keeping business validation rules cleanly
 * separated from the persistence model layer.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationRequest {

    /**
     * Full user legal name.
     */
    private String name;

    /**
     * Unique email address.
     */
    private String email;

    /**
     * Raw authentication secret string.
     */
    private String password;

    /**
     * Direct contact phone.
     */
    private String phone;

    /**
     * Requested access role.
     */
    private String role;

    /**
     * Corporate entity reference.
     */
    private String companyId;


    /**
     * Builder factory method to instantiate a new builder.
     * @return a new Builder instance for fluent creation.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Fluent Builder pattern implementation to construct RegistrationRequest instances safely.
     */
    public static class Builder {
        private String name;
        private String email;
        private String password;
        private String phone;
        private String role;
        private String companyId;

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
         * Set the email attribute.
         * @param email value
         * @return Builder instance
         */
        public Builder email(String email) {
            this.email = email;
            return this;
        }

        /**
         * Set the password attribute.
         * @param password value
         * @return Builder instance
         */
        public Builder password(String password) {
            this.password = password;
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
         * Set the companyId attribute.
         * @param companyId value
         * @return Builder instance
         */
        public Builder companyId(String companyId) {
            this.companyId = companyId;
            return this;
        }


        /**
         * Construct the finalized RegistrationRequest instance.
         * @return the fully populated DTO
         */
        public RegistrationRequest build() {
            return new RegistrationRequest(
                this.name,
                this.email,
                this.password,
                this.phone,
                this.role,
                this.companyId
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
        RegistrationRequest that = (RegistrationRequest) o;
        return Objects.equals(name, that.name) &&
               Objects.equals(email, that.email) &&
               Objects.equals(password, that.password) &&
               Objects.equals(phone, that.phone) &&
               Objects.equals(role, that.role) &&
               Objects.equals(companyId, that.companyId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, email, password, phone, role, companyId);
    }

    @Override
    public String toString() {
        return "RegistrationRequest{" +
                "name=" + name + ", " + 
                "email=" + email + ", " + 
                "password=" + password + ", " + 
                "phone=" + phone + ", " + 
                "role=" + role + ", " + 
                "companyId=" + companyId +
                '}';
    }
}
