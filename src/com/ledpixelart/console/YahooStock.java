package com.ledpixelart.console;

import java.math.BigDecimal;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;

public class YahooStock extends PIXELConsole {

	String getStock(String symbol) throws java.io.IOException {
		//Yahoo API here http://financequotes-api.com
		
		Stock stock = YahooFinance.get(symbol);
		 
		BigDecimal price = stock.getQuote().getPrice();
		setStockChange(stock.getQuote().getChangeInPercent());
		//BigDecimal peg = stock.getStats().getPeg();
		//BigDecimal dividend = stock.getDividend().getAnnualYieldPercent();
		
		System.out.println(symbol + " Stock Price: " + price);
		System.out.println(symbol + " Stock Price Change: " + getStockChange().toString() +"%");
		
		if (price.toString() == null) {
			return "Connectivity Problem";
		}
		else {
			return price.toString();
		}
	}
	
}
