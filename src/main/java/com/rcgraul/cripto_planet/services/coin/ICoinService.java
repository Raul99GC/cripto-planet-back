package com.rcgraul.cripto_planet.services.coin;

import com.rcgraul.cripto_planet.models.Coin;

import java.util.List;

public interface ICoinService {

    List<Coin> getCoinList(int page) throws Exception;

    String getMarketChart(String coinIdn, int days) throws Exception;

    String getCoinDetails(String coinId) throws Exception;

    Coin findById(String coinId) throws Exception;

    String searchCoin(String keyword) throws Exception;

    String getTop50coinsbyMarketcapRank() throws Exception;

    String getTrendingCoin() throws Exception;
}
