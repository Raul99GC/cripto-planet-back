package com.rcgraul.cripto_planet.repositories;

import com.rcgraul.cripto_planet.models.Coin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoinRepository extends JpaRepository<Coin, String> {


}
