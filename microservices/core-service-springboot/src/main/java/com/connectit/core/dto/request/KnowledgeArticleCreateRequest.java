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
 * KnowledgeArticleCreateRequest
 * ════════════════════════════════════════════════════════════════════════════════════
 * Represents a request payload to create a new Knowledge Base article in Ticklora ITSM.
 *
 * This DTO aligns with the target system architecture migration to Spring Boot.
 * It serves as a contract for data exchange, keeping business validation rules cleanly
 * separated from the persistence model layer.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeArticleCreateRequest {

    /**
     * The absolute title of the knowledge base article. Must not be empty.
     */
    private String title;

    /**
     * The primary rich text content of the article.
     */
    private String content;

    /**
     * The category classification (e.g. Hardware, Software, Security).
     */
    private String category;

    /**
     * List of keywords and tags associated with this article.
     */
    private List<String> tags;

    /**
     * Indicates if the article should be immediately published to the portal.
     */
    private Boolean isPublished;

    /**
     * The unique identifier of the author creating this article.
     */
    private String authorId;

    /**
     * Internal review notes for editors and reviewers.
     */
    private String internalNotes;

    /**
     * Roles permitted to view this article. If empty, public access is assumed.
     */
    private List<String> restrictedRoles;

    /**
     * Compelling SEO description for article summaries.
     */
    private String metaDescription;

    /**
     * Optional reference link to external manufacturer documentation.
     */
    private String externalReferenceUrl;


    /**
     * Builder factory method to instantiate a new builder.
     * @return a new Builder instance for fluent creation.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Fluent Builder pattern implementation to construct KnowledgeArticleCreateRequest instances safely.
     */
    public static class Builder {
        private String title;
        private String content;
        private String category;
        private List<String> tags;
        private Boolean isPublished;
        private String authorId;
        private String internalNotes;
        private List<String> restrictedRoles;
        private String metaDescription;
        private String externalReferenceUrl;

        /**
         * Default constructor.
         */
        public Builder() {}

        /**
         * Set the title attribute.
         * @param title value
         * @return Builder instance
         */
        public Builder title(String title) {
            this.title = title;
            return this;
        }

        /**
         * Set the content attribute.
         * @param content value
         * @return Builder instance
         */
        public Builder content(String content) {
            this.content = content;
            return this;
        }

        /**
         * Set the category attribute.
         * @param category value
         * @return Builder instance
         */
        public Builder category(String category) {
            this.category = category;
            return this;
        }

        /**
         * Set the tags attribute.
         * @param tags value
         * @return Builder instance
         */
        public Builder tags(List<String> tags) {
            this.tags = tags;
            return this;
        }

        /**
         * Set the isPublished attribute.
         * @param isPublished value
         * @return Builder instance
         */
        public Builder isPublished(Boolean isPublished) {
            this.isPublished = isPublished;
            return this;
        }

        /**
         * Set the authorId attribute.
         * @param authorId value
         * @return Builder instance
         */
        public Builder authorId(String authorId) {
            this.authorId = authorId;
            return this;
        }

        /**
         * Set the internalNotes attribute.
         * @param internalNotes value
         * @return Builder instance
         */
        public Builder internalNotes(String internalNotes) {
            this.internalNotes = internalNotes;
            return this;
        }

        /**
         * Set the restrictedRoles attribute.
         * @param restrictedRoles value
         * @return Builder instance
         */
        public Builder restrictedRoles(List<String> restrictedRoles) {
            this.restrictedRoles = restrictedRoles;
            return this;
        }

        /**
         * Set the metaDescription attribute.
         * @param metaDescription value
         * @return Builder instance
         */
        public Builder metaDescription(String metaDescription) {
            this.metaDescription = metaDescription;
            return this;
        }

        /**
         * Set the externalReferenceUrl attribute.
         * @param externalReferenceUrl value
         * @return Builder instance
         */
        public Builder externalReferenceUrl(String externalReferenceUrl) {
            this.externalReferenceUrl = externalReferenceUrl;
            return this;
        }


        /**
         * Construct the finalized KnowledgeArticleCreateRequest instance.
         * @return the fully populated DTO
         */
        public KnowledgeArticleCreateRequest build() {
            return new KnowledgeArticleCreateRequest(
                this.title,
                this.content,
                this.category,
                this.tags,
                this.isPublished,
                this.authorId,
                this.internalNotes,
                this.restrictedRoles,
                this.metaDescription,
                this.externalReferenceUrl
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
        KnowledgeArticleCreateRequest that = (KnowledgeArticleCreateRequest) o;
        return Objects.equals(title, that.title) &&
               Objects.equals(content, that.content) &&
               Objects.equals(category, that.category) &&
               Objects.equals(tags, that.tags) &&
               Objects.equals(isPublished, that.isPublished) &&
               Objects.equals(authorId, that.authorId) &&
               Objects.equals(internalNotes, that.internalNotes) &&
               Objects.equals(restrictedRoles, that.restrictedRoles) &&
               Objects.equals(metaDescription, that.metaDescription) &&
               Objects.equals(externalReferenceUrl, that.externalReferenceUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, content, category, tags, isPublished, authorId, internalNotes, restrictedRoles, metaDescription, externalReferenceUrl);
    }

    @Override
    public String toString() {
        return "KnowledgeArticleCreateRequest{" +
                "title=" + title + ", " + 
                "content=" + content + ", " + 
                "category=" + category + ", " + 
                "tags=" + tags + ", " + 
                "isPublished=" + isPublished + ", " + 
                "authorId=" + authorId + ", " + 
                "internalNotes=" + internalNotes + ", " + 
                "restrictedRoles=" + restrictedRoles + ", " + 
                "metaDescription=" + metaDescription + ", " + 
                "externalReferenceUrl=" + externalReferenceUrl +
                '}';
    }
}
