package com.Greer.StockList;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@SpringBootApplication
@Controller
public class StockListApplication {

	public static void main(String[] args) {
		SpringApplication.run(StockListApplication.class, args);
	}

	@GetMapping("/")
	@ResponseBody
	public String sayHello(){
		return "Hello";
	}

	//TODO
	@GetMapping("/NVDA")
	@ResponseBody
	public String getNVDA(){
		return "NVDA PRICE";
	}

}

