package com.github.reubuisnessgame.gamebank.teamservice.repository;

import com.github.reubuisnessgame.gamebank.teamservice.model.TeamModel;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface TeamsRepository extends CrudRepository<TeamModel, Long> {

    Optional<TeamModel> findByTeamNumber(Long number);
}
