package com.rcgraul.cripto_planet.services.coin;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rcgraul.cripto_planet.models.Coin;
import com.rcgraul.cripto_planet.repositories.CoinRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@Service
public class CoinService implements ICoinService {

    @Autowired
    private CoinRepository coinRepository;

    @Value("${coingecko.api.url}")
    private String coinsMarketsUrl;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public List<Coin> getCoinList(int page) throws Exception {

        String url = coinsMarketsUrl + "/coins/markets?vs_currency=usd&per_page=10&page=" + page;
        // Crear un WebClient con el builder
        WebClient webClient = WebClient.builder()
                .baseUrl(url)  // Establecer la URL base
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .build();

        try {
            Flux<Coin> coinFlux = webClient.get()
                    .retrieve()
                    .bodyToFlux(Coin.class); // Esta línea convierte la respuesta a un Flux de Coin (una secuencia de monedas)

            return coinFlux.collectList()  // collectList() convierte el Flux en una lista
                    .block();

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public String getMarketChart(String coinId, int days) throws Exception {
        String url = coinsMarketsUrl + "/coins/" + coinId + "/market_chart?vs_currency=usd&d&days=" + days;
        // Crear un WebClient con el builder
        WebClient webClient = WebClient.builder()
                .baseUrl(url)  // Establecer la URL base
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .build();

        try {
            Mono<String> responseMono = webClient.get()
                    .retrieve()
                    .bodyToMono(String.class);

            return responseMono.block();

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public String getCoinDetails(String coinId) throws Exception {
        String url = coinsMarketsUrl + "/coins/" + coinId;
        // Crear un WebClient con el builder
        WebClient webClient = WebClient.builder()
                .baseUrl(url)  // Establecer la URL base
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .build();

        try {
            Mono<String> responseMono = webClient.get()
                    .retrieve()
                    .bodyToMono(String.class);

            JsonNode jsonNode = objectMapper.readTree(responseMono.block());

            Coin coin = new Coin();
            coin.setId(jsonNode.get("id").asText());
            coin.setSymbol(jsonNode.get("symbol").asText());
            coin.setName(jsonNode.get("name").asText());
            coin.setImage(jsonNode.get("image").get("large").asText());

            JsonNode marketData = jsonNode.get("market_data");
            coin.setCurrentPrice(marketData.get("current_price").get("usd").asDouble());
            coin.setMarketCap(marketData.get("market_cap").get("usd").asLong());
            coin.setMarketCapRank(marketData.get("market_cap_rank").asInt());
            coin.setTotalVolume(marketData.get("total_volume").get("usd").asLong());
            coin.setHigh24h(marketData.get("high_24h").get("usd").asDouble());
            coin.setLow24h(marketData.get("low_24h").get("usd").asDouble());
            coin.setPriceChange24h(marketData.get("price_change_24h_in_currency").get("usd").asDouble());
            coin.setPriceChangePercentage24h(marketData.get("price_change_percentage_24h").asDouble());
            coin.setMarketCapChange24h(marketData.get("market_cap_change_24h_in_currency").get("usd").asLong());
            coin.setMarketCapChangePercentage24h(marketData.get("market_cap_change_percentage_24h").asDouble());
            coin.setTotalSupply(marketData.get("total_supply").asLong());

            coin.setCirculatingSupply(marketData.get("circulating_supply").asLong());

            coinRepository.save(coin);


            return responseMono.block();

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public Coin findById(String coinId) throws Exception {
        Optional<Coin> coin = coinRepository.findById(coinId);

        if (coin.isEmpty()) throw new Exception("Coin not found");
        return coin.get();
    }

    @Override
    public String searchCoin(String keyword) throws Exception {
        String url = coinsMarketsUrl + "/search?query=" + keyword;
        // Crear un WebClient con el builder
        WebClient webClient = WebClient.builder()
                .baseUrl(url)  // Establecer la URL base
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .build();

        try {
            Mono<String> responseMono = webClient.get()
                    .retrieve()
                    .bodyToMono(String.class);

            return responseMono.block();

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public String getTop50coinsbyMarketcapRank() throws Exception {
        String url = coinsMarketsUrl + "/coins/markets?vs_currency=usd&per_page=50&page=1";
        // Crear un WebClient con el builder
        WebClient webClient = WebClient.builder()
                .baseUrl(url)  // Establecer la URL base
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .build();

        try {
            Mono<String> responseMono = webClient.get()
                    .retrieve()
                    .bodyToMono(String.class);

            return responseMono.block();

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public String getTrendingCoin() throws Exception {
        String url = coinsMarketsUrl + "/search/trending";
        // Crear un WebClient con el builder
        WebClient webClient = WebClient.builder()
                .baseUrl(url)  // Establecer la URL base
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .build();

        try {
            Mono<String> responseMono = webClient.get()
                    .retrieve()
                    .bodyToMono(String.class);

            return responseMono.block();

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new Exception(e.getMessage());
        }
    }
}
