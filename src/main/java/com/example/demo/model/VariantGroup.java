package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
  name = "variant_groups",
  uniqueConstraints = @UniqueConstraint(columnNames = {"product_id", "name"})
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VariantGroup {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id", nullable = false)
  @ToString.Exclude @EqualsAndHashCode.Exclude
  private Product product;

  @Column(nullable = false, length = 50)
  private String name; // "Color", "Size"

  private Integer sortOrder; // 1,2

  @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private Set<VariantOption> options = new HashSet<>();
}
