package com.rcgraul.cripto_planet.services.watchlist;

import com.rcgraul.cripto_planet.models.Coin;
import com.rcgraul.cripto_planet.models.User;
import com.rcgraul.cripto_planet.models.Watchlist;

import java.util.UUID;

public interface IWatchlistService {

    Watchlist findUserWatchlist(UUID userId);
    Watchlist createWatchlist(User user);
    Watchlist findById(UUID id);

    Coin addItemToWatchlist(Coin coin, User user);
}
