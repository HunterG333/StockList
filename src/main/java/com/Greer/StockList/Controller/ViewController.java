package com.Greer.StockList.Controller;

import com.Greer.StockList.Services.APIService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.net.URISyntaxException;


/**
 *
 */
@ResponseBody
@Controller
public class ViewController {

    APIService apiService;

    public ViewController(APIService apiService){
        this.apiService = apiService;
    }

    @GetMapping("/")
    @ResponseBody
    public String sayHello(){
        return "Hello";
    }

    @GetMapping("/NVDA")
    @ResponseBody
    public String getNVDA() throws URISyntaxException, IOException, InterruptedException {
        return apiService.getStock("NVDA");
    }
}
