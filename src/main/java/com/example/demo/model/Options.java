package com.example.demo.model;

import org.apache.kafka.common.protocol.types.Field.Bool;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Table(name = "options")
@Data
@Entity
@Builder
@NoArgsConstructor 
@AllArgsConstructor
public class Options {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "option_name", unique = true, nullable = false)
    String optionName;

    @Column(name = "is_active", nullable = false)
    Boolean isActive;
}
