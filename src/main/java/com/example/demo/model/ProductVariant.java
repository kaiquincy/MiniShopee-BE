package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
  name = "product_variants",
  uniqueConstraints = {
    @UniqueConstraint(columnNames = {"product_id", "sku_code"})
  }
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProductVariant {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id", nullable = false)
  @ToString.Exclude @EqualsAndHashCode.Exclude
  private Product product;

  @Column(nullable = false)
  private Double price;

  private Integer stock;

  @Column(name = "sku_code", length = 100)
  private String skuCode;

  private String imageUrl;

  private Boolean active = true;

  // Mỗi biến thể gắn với 1 option ở mỗi group (Color/Size)
  @ManyToMany
  @JoinTable(
    name = "product_variant_option",
    joinColumns = @JoinColumn(name = "variant_id"),
    inverseJoinColumns = @JoinColumn(name = "option_id")
  )
  @Builder.Default
  private Set<VariantOption> options = new HashSet<>();
}
