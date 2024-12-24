package com.rcgraul.cripto_planet.services.asset;

import com.rcgraul.cripto_planet.models.Asset;
import com.rcgraul.cripto_planet.models.Coin;
import com.rcgraul.cripto_planet.models.User;

import java.util.List;
import java.util.UUID;

public interface IAssetService {

    Asset createAsset(User user, Coin coin, double quantity);

    Asset getAssetById(UUID id);

    Asset getAssetByUserIdAndId(UUID userId, UUID id);

    List<Asset> getUsersAssets(UUID userId);

    Asset updateAsset(UUID id, double quantity);

    Asset findAssetByUserIdAndCoinId(UUID userId, String coinId);

    void deleteAsset(UUID id);
}
