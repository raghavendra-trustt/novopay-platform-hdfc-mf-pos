package com.mf.mp63.controler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mf.mp63.services.CardReadingService;

import model.CardReadResponse;
import model.ReadCardModel;

@RestController
public class CardReadingControler {

@Autowired
private CardReadingService cardReadingService;

//@RequestMapping("/read_card")
@RequestMapping(value="/read_card",method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
public CardReadResponse loadMasterKey(@RequestBody ReadCardModel readCardModel) throws IllegalArgumentException, IllegalAccessException
{
	return cardReadingService.readCard(readCardModel);
}

@RequestMapping(value = "/online_auth", params = "authData", method = RequestMethod.GET)
public String performOnlineAuth(@RequestParam("authData") String authData){
	return cardReadingService.performOnlineAuth(authData);
}
}
