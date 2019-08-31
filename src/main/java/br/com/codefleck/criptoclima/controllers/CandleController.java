package br.com.codefleck.criptoclima.controllers;

import br.com.codefleck.criptoclima.enitities.Candle;
import br.com.codefleck.criptoclima.repositories.CandleRepository;
import br.com.codefleck.criptoclima.services.CandleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
public class CandleController {

    @Autowired
    private CandleService candleService;

    @PostMapping("/candles")
    @ResponseStatus(HttpStatus.CREATED)
    Candle saveCandle(@RequestBody Candle candle) {
        return candleService.saveCandle(candle);
    }
}
