package com.github.reubuisnessgame.gamebank.teamservice.form;


import com.github.reubuisnessgame.gamebank.teamservice.model.ShareModel;
import com.github.reubuisnessgame.gamebank.teamservice.model.TeamModel;

public class FullTeamForm {

    private TeamModel team;

    private Iterable<ShareModel> shares;

    public FullTeamForm() {
    }

    public TeamModel getTeam() {
        return team;
    }

    public void setTeam(TeamModel team) {
        this.team = team;
    }

    public Iterable<ShareModel> getShares() {
        return shares;
    }

    public void setShares(Iterable<ShareModel> shares) {
        this.shares = shares;
    }
}
