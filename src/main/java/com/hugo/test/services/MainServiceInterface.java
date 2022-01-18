package com.hugo.test.services;

import com.hugo.test.payload.request.PlayerRequest;
import com.hugo.test.payload.response.PlayerResponse;

public interface MainServiceInterface {

    public PlayerResponse processPlay(PlayerRequest players);

}
