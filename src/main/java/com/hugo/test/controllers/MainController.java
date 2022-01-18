package com.hugo.test.controllers;

import com.hugo.test.payload.request.PlayerRequest;
import com.hugo.test.payload.response.PlayerResponse;
import com.hugo.test.services.MainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class MainController extends  DataServiceController{
    @Autowired
    MainService mainService;


    @GetMapping(serviceNameV1+"/play")
    public PlayerResponse play(@RequestBody PlayerRequest players) {




        PlayerResponse response= mainService.processPlay(players);


      return response;
    }



}
