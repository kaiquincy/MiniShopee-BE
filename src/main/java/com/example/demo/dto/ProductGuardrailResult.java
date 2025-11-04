package com.example.demo.dto;

import java.util.List;

public class ProductGuardrailResult {
  public Safety safety;
  public Consistency consistency;
  public Suggestions suggestions;

  public static class Safety {
    public String nsfw_label; // "safe" | "suspect" | "explicit"
    public double nsfw_confidence;
    public List<String> reasons;
  }

  public static class Consistency {
    public String is_title_image_consistent; // "yes" | "no" | "uncertain"
    public double confidence;
    public List<String> mismatch_reasons;
  }

  public static class Suggestions {
    public String suggested_title;
    public String suggested_description;
    public List<String> keywords;
    public String category_guess;
  }
}
