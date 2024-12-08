package com.rcgraul.cripto_planet.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rcgraul.cripto_planet.models.Coin;
import com.rcgraul.cripto_planet.services.coin.CoinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/coin")
public class CoinController {

    @Autowired
    private CoinService coinService;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping("/list")
    ResponseEntity<List<Coin>> getCoinList(@RequestParam("page") int page) throws Exception {
        return ResponseEntity.ok(coinService.getCoinList(page));
    }

    @GetMapping("/{id}/chart")
    ResponseEntity<JsonNode> getMarketChart(@PathVariable String id, @RequestParam("days") int days) throws Exception {
        return ResponseEntity.ok(objectMapper.readTree(coinService.getMarketChart(id, days)));
    }

    @GetMapping("/search")
    ResponseEntity<JsonNode> searchCoin(@RequestParam("q") String keyword) throws Exception {

        String coin = coinService.searchCoin(keyword);
        return ResponseEntity.ok(objectMapper.readTree(coin));
    }

    @GetMapping("/top50")
    ResponseEntity<JsonNode> getTop50coinsbyMarketcapRank() throws Exception {
        String coin = coinService.getTop50coinsbyMarketcapRank();
        return ResponseEntity.ok(objectMapper.readTree(coin));
    }

    @GetMapping("/trending")
    ResponseEntity<JsonNode> getTrendingCoin() throws Exception {
        String coin = coinService.getTrendingCoin();
        return ResponseEntity.ok(objectMapper.readTree(coin));
    }

    @GetMapping("/details/{coinId}")
    ResponseEntity<JsonNode> getCoinDetails(@PathVariable String coinId) throws Exception {
        String coin = coinService.getCoinDetails(coinId);
        return ResponseEntity.ok(objectMapper.readTree(coin));
    }


}
