package com.rcgraul.cripto_planet.services.coin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rcgraul.cripto_planet.exceptions.CoinNotFoundException;
import com.rcgraul.cripto_planet.models.Coin;
import com.rcgraul.cripto_planet.repositories.CoinRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
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

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public List<Coin> getCoinList(int page) {
        String url = coinsMarketsUrl + "/coins/markets?vs_currency=usd&per_page=10&page=" + page;

        try {
            ResponseEntity<Coin[]> response = restTemplate.getForEntity(url, Coin[].class);
            return Arrays.asList(response.getBody());

        } catch (HttpClientErrorException.NotFound e) {
            throw new CoinNotFoundException(e.getMessage());
        } catch (HttpClientErrorException.BadRequest e) {
            throw new IllegalArgumentException("Invalid page parameter: " + page);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new RuntimeException("Error while fetching coin list: " + e.getStatusCode());
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error while fetching coin list: " + e.getMessage());
        }
    }

    @Override
    public String getMarketChart(String coinId, int days) {
        String url = coinsMarketsUrl + "/coins/" + coinId + "/market_chart?vs_currency=usd&days=" + days;

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            return response.getBody();

        } catch (HttpClientErrorException.NotFound e) {
            throw new CoinNotFoundException("Coin with ID " + coinId + " not found");
        } catch (HttpClientErrorException.BadRequest e) {
            throw new IllegalArgumentException(e.getMessage());
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new RuntimeException("Error while fetching details for coin ID " + coinId + ": " + e.getStatusCode());
        }
    }

    private Coin saveCoin(JsonNode jsonNode) {
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
        coin.setPriceChange24h(marketData.get("price_change_24h").asDouble());
        coin.setPriceChangePercentage24h(marketData.get("price_change_percentage_24h").asDouble());
        coin.setMarketCapChange24h(marketData.get("market_cap_change_24h").asLong());
        coin.setMarketCapChangePercentage24h(marketData.get("market_cap_change_percentage_24h").asDouble());
        coin.setTotalSupply(marketData.get("total_supply").asLong());
        coin.setCirculatingSupply(marketData.get("circulating_supply").asLong());

        return coinRepository.save(coin);
    }

    @Override
    public String getCoinDetails(String coinId) {
        String url = coinsMarketsUrl + "/coins/" + coinId;

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            saveCoin(jsonNode);
            return response.getBody();

        } catch (HttpClientErrorException.NotFound e) {
            throw new CoinNotFoundException("Coin with ID " + coinId + " not found");
        } catch (HttpClientErrorException.BadRequest e) {
            throw new IllegalArgumentException(e.getMessage());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error processing JSON response: " + e.getMessage());
        }
    }

    @Override
    public Coin findById(String coinId) throws Exception {
        Optional<Coin> coin = coinRepository.findById(coinId);

        if (coin.isEmpty()) {
            String coinDetails = getCoinDetails(coinId);
            JsonNode jsonNode = objectMapper.readTree(coinDetails);
            return saveCoin(jsonNode);
        }

        return coin.get();
    }

    @Override
    public String searchCoin(String keyword) {
        String url = coinsMarketsUrl + "/search?query=" + keyword;

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            return response.getBody();

        } catch (HttpClientErrorException.BadRequest e) {
            throw new IllegalArgumentException("Invalid search keyword: " + keyword);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new RuntimeException("Server error while searching for keyword: " + keyword);
        }
    }

    @Override
    public String getTop50coinsbyMarketcapRank() throws Exception {
        String url = coinsMarketsUrl + "/coins/markets?vs_currency=usd&per_page=50&page=1";

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            return response.getBody();

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public String getTrendingCoin() throws Exception {
        String url = coinsMarketsUrl + "/search/trending";

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            return response.getBody();

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new Exception(e.getMessage());
        }
    }
}
