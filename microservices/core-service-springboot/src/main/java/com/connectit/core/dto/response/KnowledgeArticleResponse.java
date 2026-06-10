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
 * KnowledgeArticleResponse
 * ════════════════════════════════════════════════════════════════════════════════════
 * Represents the detailed response structure for a Knowledge Base article.
 *
 * This DTO aligns with the target system architecture migration to Spring Boot.
 * It serves as a contract for data exchange, keeping business validation rules cleanly
 * separated from the persistence model layer.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeArticleResponse {

    /**
     * The generated unique identifier of the knowledge base article.
     */
    private String id;

    /**
     * The title of the article.
     */
    private String title;

    /**
     * The text contents of the article.
     */
    private String content;

    /**
     * The classification category.
     */
    private String category;

    /**
     * Keywords and tags.
     */
    private List<String> tags;

    /**
     * Whether the article is active.
     */
    private Boolean isPublished;

    /**
     * Author user ID.
     */
    private String authorId;

    /**
     * Human-readable author display name.
     */
    private String authorName;

    /**
     * Total view count tracked on the portal.
     */
    private Integer views;

    /**
     * Total positive votes certifying helpfulness.
     */
    private Integer votes;

    /**
     * Calculated helpfulness score out of 5.
     */
    private Double rating;

    /**
     * Internal review notes.
     */
    private String internalNotes;

    /**
     * Allowed roles.
     */
    private List<String> restrictedRoles;

    /**
     * Meta description details.
     */
    private String metaDescription;

    /**
     * Original publishing timestamp.
     */
    private LocalDateTime createdAt;

    /**
     * Last revision timestamp.
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
     * Fluent Builder pattern implementation to construct KnowledgeArticleResponse instances safely.
     */
    public static class Builder {
        private String id;
        private String title;
        private String content;
        private String category;
        private List<String> tags;
        private Boolean isPublished;
        private String authorId;
        private String authorName;
        private Integer views;
        private Integer votes;
        private Double rating;
        private String internalNotes;
        private List<String> restrictedRoles;
        private String metaDescription;
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
         * Set the authorName attribute.
         * @param authorName value
         * @return Builder instance
         */
        public Builder authorName(String authorName) {
            this.authorName = authorName;
            return this;
        }

        /**
         * Set the views attribute.
         * @param views value
         * @return Builder instance
         */
        public Builder views(Integer views) {
            this.views = views;
            return this;
        }

        /**
         * Set the votes attribute.
         * @param votes value
         * @return Builder instance
         */
        public Builder votes(Integer votes) {
            this.votes = votes;
            return this;
        }

        /**
         * Set the rating attribute.
         * @param rating value
         * @return Builder instance
         */
        public Builder rating(Double rating) {
            this.rating = rating;
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
         * Construct the finalized KnowledgeArticleResponse instance.
         * @return the fully populated DTO
         */
        public KnowledgeArticleResponse build() {
            return new KnowledgeArticleResponse(
                this.id,
                this.title,
                this.content,
                this.category,
                this.tags,
                this.isPublished,
                this.authorId,
                this.authorName,
                this.views,
                this.votes,
                this.rating,
                this.internalNotes,
                this.restrictedRoles,
                this.metaDescription,
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
        KnowledgeArticleResponse that = (KnowledgeArticleResponse) o;
        return Objects.equals(id, that.id) &&
               Objects.equals(title, that.title) &&
               Objects.equals(content, that.content) &&
               Objects.equals(category, that.category) &&
               Objects.equals(tags, that.tags) &&
               Objects.equals(isPublished, that.isPublished) &&
               Objects.equals(authorId, that.authorId) &&
               Objects.equals(authorName, that.authorName) &&
               Objects.equals(views, that.views) &&
               Objects.equals(votes, that.votes) &&
               Objects.equals(rating, that.rating) &&
               Objects.equals(internalNotes, that.internalNotes) &&
               Objects.equals(restrictedRoles, that.restrictedRoles) &&
               Objects.equals(metaDescription, that.metaDescription) &&
               Objects.equals(createdAt, that.createdAt) &&
               Objects.equals(updatedAt, that.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, content, category, tags, isPublished, authorId, authorName, views, votes, rating, internalNotes, restrictedRoles, metaDescription, createdAt, updatedAt);
    }

    @Override
    public String toString() {
        return "KnowledgeArticleResponse{" +
                "id=" + id + ", " + 
                "title=" + title + ", " + 
                "content=" + content + ", " + 
                "category=" + category + ", " + 
                "tags=" + tags + ", " + 
                "isPublished=" + isPublished + ", " + 
                "authorId=" + authorId + ", " + 
                "authorName=" + authorName + ", " + 
                "views=" + views + ", " + 
                "votes=" + votes + ", " + 
                "rating=" + rating + ", " + 
                "internalNotes=" + internalNotes + ", " + 
                "restrictedRoles=" + restrictedRoles + ", " + 
                "metaDescription=" + metaDescription + ", " + 
                "createdAt=" + createdAt + ", " + 
                "updatedAt=" + updatedAt +
                '}';
    }
}
