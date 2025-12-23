// src/main/java/com/example/demo/service/ProductValidationService.java
package com.example.demo.service;

import com.example.demo.dto.ProductGuardrailResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import java.util.Base64;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductValidationService {

  private final ObjectMapper objectMapper = new ObjectMapper();

  private final WebClient webClient = WebClient.builder()
      .exchangeStrategies(ExchangeStrategies.builder()
          .codecs(cfg -> cfg.defaultCodecs().maxInMemorySize(8 * 1024 * 1024)) // 8MB
          .build())
      .build();

  @Value("${gemini.apiKey:}")
  private String apiKey;

  @Value("${gemini.url}")
  private String geminiUrl;

  @Value("${gemini.enabled:true}")
  private boolean enabled;

  @Value("${gemini.thresholds.nsfwBlockConfidence:0.60}")
  private double nsfwBlockConfidence;

  @Value("${gemini.thresholds.mismatchBlockConfidence:0.60}")
  private double mismatchBlockConfidence;

  public boolean isEnabled() {
    return enabled && apiKey != null && !apiKey.isBlank();
  }

  public ProductGuardrailResult analyze(byte[] imageBytes, String title) {
    if (!isEnabled()) {
      return null; // skip validation
    }

    String imageB64 = Base64.getEncoder().encodeToString(imageBytes);


    String prompt = """
        You are an e-commerce content and safety validator. Analyze the given product image and title.

        Tasks:
        1) SAFETY: detect NSFW/sensitive content.
        2) CONSISTENCY: check if the image matches the product title.
        3) SUGGESTIONS: propose a better product title & short description based mainly on the image.

        Rules:
        - Use the image as the primary source of truth.
        - If uncertain, say "uncertain" and explain.
        - Do not include any extra commentary.
        Product title: "%s"
        """.formatted(title);

    Map<String, Object> responseSchema = buildResponseSchema();
    Map<String, Object> payload = Map.of(
        "contents", new Object[] {
            Map.of("parts", new Object[] {
                Map.of("inline_data", Map.of(
                    "mime_type", "image/jpeg",
                    "data", imageB64
                )),
                Map.of("text", prompt)
            })
        },
        "generationConfig", Map.of(
            "responseMimeType", "application/json",
            "responseSchema", responseSchema
        )
    );

    JsonNode root = webClient.post()
        .uri(geminiUrl)
        .contentType(MediaType.APPLICATION_JSON)
        .header("x-goog-api-key", apiKey)
        .bodyValue(payload)
        .retrieve()
        .bodyToMono(JsonNode.class)
        .onErrorResume(ex -> Mono.error(new RuntimeException("Gemini call failed: " + ex.getMessage(), ex)))
        .block();

    // Lấy JSON text tại candidates[0].content.parts[0].text
    String jsonText = null;
    try {
      JsonNode candidates = root.path("candidates");
      if (candidates.isArray() && candidates.size() > 0) {
        JsonNode parts = candidates.get(0).path("content").path("parts");
        if (parts.isArray() && parts.size() > 0) {
          jsonText = parts.get(0).path("text").asText();
        }
      }
    } catch (Exception ignore) {}

    if (jsonText == null || jsonText.isBlank()) {
      throw new RuntimeException("Gemini response missing JSON text: " + root.toString());
    }

    try {
      return objectMapper.readValue(jsonText.getBytes(StandardCharsets.UTF_8), ProductGuardrailResult.class);
    } catch (Exception e) {
      throw new RuntimeException("Failed to parse guardrail JSON: " + e.getMessage() + "\nRawText=" + jsonText);
    }
  }

  public void enforceOrThrow(ProductGuardrailResult r) {
    if (r == null) return; // validation disabled

    // Chặn NSFW
    if (r.safety != null) {
      boolean blockNSFW = ("explicit".equalsIgnoreCase(r.safety.nsfw_label))
          || ("suspect".equalsIgnoreCase(r.safety.nsfw_label) && r.safety.nsfw_confidence >= nsfwBlockConfidence);
      if (blockNSFW) {
        String reason = String.join("; ", r.safety.reasons != null ? r.safety.reasons : java.util.List.of("NSFW"));
        throw new ProductValidationException(422, "NSFW content detected", reason, r);
      }
    }

    // Chặn mismatch Title ↔ Image
    if (r.consistency != null) {
      boolean mismatch = "no".equalsIgnoreCase(r.consistency.is_title_image_consistent)
          && r.consistency.confidence >= mismatchBlockConfidence;
      if (mismatch) {
        String reason = "Image/title mismatch: " +
            String.join("; ", r.consistency.mismatch_reasons != null ? r.consistency.mismatch_reasons : java.util.List.of());
        throw new ProductValidationException(422, "Image/title inconsistent", reason, r);
      }
    }
  }

  public static class ProductValidationException extends RuntimeException {
    public final int status;
    public final String error;
    public final String detail;
    public final ProductGuardrailResult result;
    public ProductValidationException(int status, String error, String detail, ProductGuardrailResult result) {
      super(error + " - " + detail);
      this.status = status;
      this.error = error;
      this.detail = detail;
      this.result = result;
    }
  }

  private Map<String, Object> buildResponseSchema() {
    Map<String, Object> safety = new HashMap<>();
    safety.put("type", "OBJECT");
    safety.put("properties", Map.of(
        "nsfw_label", Map.of("type", "STRING", "enum", new String[]{"safe","suspect","explicit"}),
        "nsfw_confidence", Map.of("type", "NUMBER"),
        "reasons", Map.of("type", "ARRAY", "items", Map.of("type", "STRING"))
    ));
    safety.put("required", new String[] {"nsfw_label","nsfw_confidence","reasons"});
    safety.put("propertyOrdering", new String[] {"nsfw_label","nsfw_confidence","reasons"});

    Map<String, Object> consistency = new HashMap<>();
    consistency.put("type", "OBJECT");
    consistency.put("properties", Map.of(
        "is_title_image_consistent", Map.of("type","STRING","enum", new String[]{"yes","no","uncertain"}),
        "confidence", Map.of("type","NUMBER"),
        "mismatch_reasons", Map.of("type","ARRAY","items", Map.of("type","STRING"))
    ));
    consistency.put("required", new String[] {"is_title_image_consistent","confidence","mismatch_reasons"});
    consistency.put("propertyOrdering", new String[] {"is_title_image_consistent","confidence","mismatch_reasons"});

    Map<String, Object> suggestions = new HashMap<>();
    suggestions.put("type", "OBJECT");
    suggestions.put("properties", Map.of(
        "suggested_title", Map.of("type","STRING"),
        "suggested_description", Map.of("type","STRING"),
        "keywords", Map.of("type","ARRAY","items", Map.of("type","STRING")),
        "category_guess", Map.of("type","STRING")
    ));
    suggestions.put("required", new String[] {"suggested_title","suggested_description","keywords"});
    suggestions.put("propertyOrdering", new String[] {"suggested_title","suggested_description","keywords","category_guess"});

    Map<String, Object> root = new HashMap<>();
    root.put("type", "OBJECT");
    root.put("properties", Map.of(
        "safety", safety,
        "consistency", consistency,
        "suggestions", suggestions
    ));
    root.put("required", new String[] {"safety","consistency","suggestions"});
    root.put("propertyOrdering", new String[] {"safety","consistency","suggestions"});
    return root;
  }

    public byte[] fetchImageBytes(String imageUrl) throws Exception {
        if (imageUrl == null) throw new IllegalArgumentException("imageUrl is null");

        if (Files.exists(Paths.get("uploads" +File.separator+ "products_img"+File.separator+imageUrl))) {
            return Files.readAllBytes(Paths.get("uploads" +File.separator+ "products_img"+File.separator+imageUrl));
        }
        return Files.readAllBytes(Paths.get("uploads" +File.separator+ "products_img"+File.separator+imageUrl));
    // // Nếu là URL HTTP(S)
    // try (InputStream in = new URL(imageUrl).openStream()) {
    //     return in.readAllBytes();
    // }
    }

    public String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            return "{}";
        }
    }


}
