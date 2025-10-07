package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
  name = "variant_options",
  uniqueConstraints = @UniqueConstraint(columnNames = {"group_id", "value"})
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VariantOption {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "group_id", nullable = false)
  @ToString.Exclude @EqualsAndHashCode.Exclude
  private VariantGroup group;

  @Column(nullable = false, length = 50)
  private String value; // "Red", "M", ...
}
