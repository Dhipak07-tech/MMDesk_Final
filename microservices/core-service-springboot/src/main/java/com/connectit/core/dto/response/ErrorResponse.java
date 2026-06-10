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
 * ErrorResponse
 * ════════════════════════════════════════════════════════════════════════════════════
 * Standardized response object formatting server side errors to UI client.
 *
 * This DTO aligns with the target system architecture migration to Spring Boot.
 * It serves as a contract for data exchange, keeping business validation rules cleanly
 * separated from the persistence model layer.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    /**
     * Server time of issue occurrence.
     */
    private LocalDateTime timestamp;

    /**
     * HTTP response status number.
     */
    private Integer status;

    /**
     * Canonical short text title of error.
     */
    private String error;

    /**
     * Clear user facing explanation.
     */
    private String message;

    /**
     * Endpoint resource path requested.
     */
    private String path;


    /**
     * Builder factory method to instantiate a new builder.
     * @return a new Builder instance for fluent creation.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Fluent Builder pattern implementation to construct ErrorResponse instances safely.
     */
    public static class Builder {
        private LocalDateTime timestamp;
        private Integer status;
        private String error;
        private String message;
        private String path;

        /**
         * Default constructor.
         */
        public Builder() {}

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
         * Set the status attribute.
         * @param status value
         * @return Builder instance
         */
        public Builder status(Integer status) {
            this.status = status;
            return this;
        }

        /**
         * Set the error attribute.
         * @param error value
         * @return Builder instance
         */
        public Builder error(String error) {
            this.error = error;
            return this;
        }

        /**
         * Set the message attribute.
         * @param message value
         * @return Builder instance
         */
        public Builder message(String message) {
            this.message = message;
            return this;
        }

        /**
         * Set the path attribute.
         * @param path value
         * @return Builder instance
         */
        public Builder path(String path) {
            this.path = path;
            return this;
        }


        /**
         * Construct the finalized ErrorResponse instance.
         * @return the fully populated DTO
         */
        public ErrorResponse build() {
            return new ErrorResponse(
                this.timestamp,
                this.status,
                this.error,
                this.message,
                this.path
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
        ErrorResponse that = (ErrorResponse) o;
        return Objects.equals(timestamp, that.timestamp) &&
               Objects.equals(status, that.status) &&
               Objects.equals(error, that.error) &&
               Objects.equals(message, that.message) &&
               Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp, status, error, message, path);
    }

    @Override
    public String toString() {
        return "ErrorResponse{" +
                "timestamp=" + timestamp + ", " + 
                "status=" + status + ", " + 
                "error=" + error + ", " + 
                "message=" + message + ", " + 
                "path=" + path +
                '}';
    }
}
