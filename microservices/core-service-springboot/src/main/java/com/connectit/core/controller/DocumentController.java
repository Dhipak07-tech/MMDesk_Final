package com.connectit.core.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
@Slf4j
public class DocumentController {

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private String getStr(Map<String, Object> body, String... keys) {
        for (String key : keys) {
            Object val = body.get(key);
            if (val != null) return val.toString();
        }
        return null;
    }

    @GetMapping("/{collectionName}")
    public ResponseEntity<?> getDocuments(@PathVariable String collectionName) {
        log.info("[Documents] Fetching all documents in collection: {}", collectionName);
        try {
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT document_json FROM generic_documents WHERE collection_name = ?", collectionName
            );
            List<Map<String, Object>> docs = new ArrayList<>();
            for (Map<String, Object> row : rows) {
                String json = (String) row.get("document_json");
                if (json != null) {
                    try {
                        Map<String, Object> map = objectMapper.readValue(json, Map.class);
                        docs.add(map);
                    } catch (Exception e) {
                        log.warn("[Documents] Failed to parse document JSON: {}", json, e);
                    }
                }
            }
            return ResponseEntity.ok(docs);
        } catch (Exception e) {
            log.error("[Documents] Error fetching collection {}: {}", collectionName, e.getMessage(), e);
            return ResponseEntity.ok(List.of());
        }
    }

    @GetMapping("/{collectionName}/{id}")
    public ResponseEntity<?> getDocument(@PathVariable String collectionName, @PathVariable String id) {
        log.info("[Documents] Fetching document {}/{}", collectionName, id);
        try {
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT document_json FROM generic_documents WHERE collection_name = ? AND id = ?",
                collectionName, id
            );
            if (!rows.isEmpty()) {
                String json = (String) rows.get(0).get("document_json");
                if (json != null) {
                    Map<String, Object> map = objectMapper.readValue(json, Map.class);
                    return ResponseEntity.ok(map);
                }
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("[Documents] Error fetching document {}/{}: {}", collectionName, id, e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{collectionName}")
    @Transactional
    public ResponseEntity<?> createDocument(@PathVariable String collectionName, @RequestBody Map<String, Object> body) {
        log.info("[Documents] Creating document in {}: {}", collectionName, body);
        try {
            String id = (String) body.get("id");
            if (id == null) {
                id = (String) body.get("uid");
            }
            if (id == null) {
                id = UUID.randomUUID().toString();
                body.put("id", id);
            }

            Map<String, Object> normalized = new HashMap<>(body);
            normalized.put("id", id);

            String json = objectMapper.writeValueAsString(normalized);

            List<Map<String, Object>> existing = jdbcTemplate.queryForList(
                "SELECT id FROM generic_documents WHERE collection_name = ? AND id = ?",
                collectionName, id
            );

            if (!existing.isEmpty()) {
                jdbcTemplate.update(
                    "UPDATE generic_documents SET document_json = ?, updated_at = CURRENT_TIMESTAMP WHERE collection_name = ? AND id = ?",
                    json, collectionName, id
                );
            } else {
                jdbcTemplate.update(
                    "INSERT INTO generic_documents (id, collection_name, document_json) VALUES (?, ?, ?)",
                    id, collectionName, json
                );
            }

            return ResponseEntity.ok(normalized);
        } catch (Exception e) {
            log.error("[Documents] Error creating document in {}: {}", collectionName, e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{collectionName}/{id}")
    @Transactional
    public ResponseEntity<?> updateDocument(@PathVariable String collectionName, @PathVariable String id, @RequestBody Map<String, Object> body) {
        log.info("[Documents] Updating document {}/{}: {}", collectionName, id, body);
        try {
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT document_json FROM generic_documents WHERE collection_name = ? AND id = ?",
                collectionName, id
            );
            Map<String, Object> current = new HashMap<>();
            if (!rows.isEmpty()) {
                String json = (String) rows.get(0).get("document_json");
                if (json != null) {
                    current = new HashMap<>(objectMapper.readValue(json, Map.class));
                }
            }

            for (Map.Entry<String, Object> entry : body.entrySet()) {
                current.put(entry.getKey(), entry.getValue());
            }
            current.put("id", id);

            String json = objectMapper.writeValueAsString(current);

            List<Map<String, Object>> existing = jdbcTemplate.queryForList(
                "SELECT id FROM generic_documents WHERE collection_name = ? AND id = ?",
                collectionName, id
            );

            if (!existing.isEmpty()) {
                jdbcTemplate.update(
                    "UPDATE generic_documents SET document_json = ?, updated_at = CURRENT_TIMESTAMP WHERE collection_name = ? AND id = ?",
                    json, collectionName, id
                );
            } else {
                jdbcTemplate.update(
                    "INSERT INTO generic_documents (id, collection_name, document_json) VALUES (?, ?, ?)",
                    id, collectionName, json
                );
            }

            return ResponseEntity.ok(current);
        } catch (Exception e) {
            log.error("[Documents] Error updating document {}/{}: {}", collectionName, id, e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{collectionName}/{id}")
    @Transactional
    public ResponseEntity<?> deleteDocument(@PathVariable String collectionName, @PathVariable String id) {
        log.info("[Documents] Deleting document {}/{}", collectionName, id);
        try {
            jdbcTemplate.update(
                "DELETE FROM generic_documents WHERE collection_name = ? AND id = ?",
                collectionName, id
            );
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            log.error("[Documents] Error deleting document {}/{}: {}", collectionName, id, e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}
