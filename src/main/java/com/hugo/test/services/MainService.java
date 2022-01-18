package com.hugo.test.services;

import com.hugo.test.payload.request.Player;
import com.hugo.test.payload.request.PlayerRequest;
import com.hugo.test.payload.response.PlayerResponse;
import com.hugo.test.repository.MainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service


public class MainService implements  MainServiceInterface {

    @Autowired
    MainRepository mainRepository;

    public PlayerResponse processPlay(PlayerRequest players){
        PlayerResponse playerResponse=new PlayerResponse();
        for (Player player:players.getPlayers()
             ) {
            if(player.getType().equals("expert")){
                com.hugo.test.models.Player playerDb=new com.hugo.test.models.Player();
                playerDb.setName(player.getName());
                playerDb.setType(player.getType());

                this.mainRepository.save(playerDb);
                playerResponse.addResponse("player "+player.getName()+" stored in DB");

                List<com.hugo.test.models.Player> playersResult=this.mainRepository.findAll();

                for (com.hugo.test.models.Player playerresult:playersResult
                     ) {
                    System.out.println(playerresult.getName());
                }
            }
            else if(player.getType().equals("novice")){
                this.sendMessage("Player="+player.getName());
                playerResponse.addResponse("player "+player.getName()+"  sent to Kafka topic");

            }
            else{
                playerResponse.addResponse("player "+player.getName()+"  sent to Kafka topic");

            }


        }
        return  playerResponse;

    }


    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessage(String message)
    {
        System.out.println(String.format("Message sent -> %s", message));
        this.kafkaTemplate.send("novice-players", message);
    }
}
