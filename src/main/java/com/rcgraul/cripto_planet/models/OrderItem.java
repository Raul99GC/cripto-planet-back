package com.rcgraul.cripto_planet.models;

import jakarta.persistence.*;
import lombok.Data;
import net.minidev.json.annotate.JsonIgnore;

import java.util.UUID;

@Data
@Entity
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private double quantity;

    @ManyToOne
    private Coin coin;

    private double buyPrice;

    private double sellPrice;

    @JsonIgnore
    @OneToOne
    private Order order;
}
