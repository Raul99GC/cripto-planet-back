package com.rcgraul.cripto_planet.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

  @GetMapping
  public String home() {
    return "Cripto Planet";
  }
}
