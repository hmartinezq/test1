package com.hugo.test.repository;

import com.hugo.test.models.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MainRepository extends JpaRepository<Player, Long> {


}
