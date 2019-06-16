package com.github.reubuisnessgame.gamebank.teamservice.dao;

import com.github.reubuisnessgame.gamebank.teamservice.form.ChangingUserDataForm;
import com.github.reubuisnessgame.gamebank.teamservice.form.FullTeamForm;
import com.github.reubuisnessgame.gamebank.teamservice.model.ShareModel;
import com.github.reubuisnessgame.gamebank.teamservice.model.TeamModel;
import com.github.reubuisnessgame.gamebank.teamservice.model.UserModel;
import com.github.reubuisnessgame.gamebank.teamservice.repository.ShareRepository;
import com.github.reubuisnessgame.gamebank.teamservice.repository.TeamsRepository;
import com.github.reubuisnessgame.gamebank.teamservice.repository.UserRepository;
import com.github.reubuisnessgame.gamebank.teamservice.security.jwt.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicReference;

@Component
public class RepositoryComponent {

    private final UserRepository userRepository;

    private final TeamsRepository teamsRepository;

    private final
    JwtTokenProvider jwtTokenProvider;

    private final ShareRepository shareRepository;


    public RepositoryComponent(UserRepository userRepository, TeamsRepository teamsRepository, JwtTokenProvider jwtTokenProvider, ShareRepository shareRepository) {
        this.userRepository = userRepository;
        this.teamsRepository = teamsRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.shareRepository = shareRepository;
    }


    TeamModel getTeamByToken(String token) {
        Long userId = getUserIdFromToken(token);
        return teamsRepository.findByUserId(userId).orElseThrow(() ->
                new UsernameNotFoundException("Team ID: " + userId + " not found"));
    }


    private Long getUserIdFromToken(String token) {
        Jws<Claims> claims = jwtTokenProvider.getClaims(resolveToken(token));
        return Long.valueOf((Integer) claims.getBody().get("userId"));
    }


    FullTeamForm getTeamFullInfo(TeamModel teamModel) {
        Iterable<ShareModel> shareModels = shareRepository.findAllByUserId(teamModel.getId());
        FullTeamForm fullTeamForm = new FullTeamForm();
        fullTeamForm.setTeam(teamModel);
        fullTeamForm.setShares(shareModels);
        return fullTeamForm;
    }


    ChangingUserDataForm changeTeamUsername(String token, String newUsername) {
        TeamModel teamModel = getTeamByToken(token);
        UserModel userModel = userRepository.findByUsername(teamModel.getUsername()).orElseThrow(() ->
                new UsernameNotFoundException("Team username: " + teamModel.getUsername() + " not found"));
        teamModel.setUsername(newUsername);
        userModel.setUsername(newUsername);
        teamsRepository.save(teamModel);
        userRepository.save(userModel);
        ChangingUserDataForm userDataForm = new ChangingUserDataForm();
        userDataForm.setUser(teamModel);
        userDataForm.setToken(jwtTokenProvider.createToken(userModel.getUsername(), userModel.getRole().name()));
        return userDataForm;
    }

    Double calculateFullScore(Long id) {
        TeamModel teamModel = teamsRepository.findByUserId(id).orElseThrow(() ->
                new UsernameNotFoundException("Team ID: " + id + " not found"));
        Iterable<ShareModel> shareModels = shareRepository.findAllByUserId(teamModel.getId());
        AtomicReference<Double> fullScore = new AtomicReference<>((double) 0);
        shareModels.forEach((s) -> {
            double price = s.getCompanyModel().getSharePrice() * s.getSharesNumbers();
            fullScore.updateAndGet(v -> v + price);
        });
        return (Math.round((fullScore.get() * 1000.0)) / 1000.0);
    }


    private String resolveToken(String bearerToken) {
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        throw new IllegalArgumentException("Incorrect token");
    }


}
