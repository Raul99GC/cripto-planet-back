package com.rcgraul.cripto_planet.services.order;

import com.rcgraul.cripto_planet.enums.OrderType;
import com.rcgraul.cripto_planet.models.Coin;
import com.rcgraul.cripto_planet.models.Order;
import com.rcgraul.cripto_planet.models.OrderItem;
import com.rcgraul.cripto_planet.models.User;

import java.util.List;
import java.util.UUID;

public interface IOrderService {

    Order createOrder(User user, OrderItem orderItem, OrderType orderType);

    Order getOrderById(UUID id);

    List<Order> getOrdersOfUser(UUID userId);

    Order processOrder(Coin coin, double quantity, OrderType orderType, User user) throws Exception;
}
