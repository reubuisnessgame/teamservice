package com.github.reubuisnessgame.gamebank.teamservice.dao;

import com.github.reubuisnessgame.gamebank.teamservice.form.ChangingUserDataForm;
import com.github.reubuisnessgame.gamebank.teamservice.form.FullTeamForm;
import com.github.reubuisnessgame.gamebank.teamservice.model.TeamModel;
import com.github.reubuisnessgame.gamebank.teamservice.repository.TeamsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TeamsDAO {
    private static final long STOCK_PRICE_CHANGE = 300_000; //5 minutes

    private final double creditRate = 1.12;
    private final double depositRate = 1.07;

    //public static final long PAY_TIME = 1_800_000; // 30 minutes
    private static final long PAY_TIME = 30_000; // 30 minutes
    private static final long SLEEP_CHECK_TIME = 30_000; // 30 seconds

    private final double sharePercent = 0.25;

    private static long lastPaySharesTime;


    private final Logger LOGGER = LoggerFactory.getLogger(TeamsDAO.class.getSimpleName());

    private final
    TeamsRepository teamsRepository;

    private final RepositoryComponent repositoryComponent;

    private boolean isGameStarted;

    public TeamsDAO(TeamsRepository teamsRepository, RepositoryComponent repositoryComponent) {
        this.teamsRepository = teamsRepository;
        lastPaySharesTime = 0;
        CheckingThread checkingThread = new CheckingThread();
        checkingThread.start();

        this.repositoryComponent = repositoryComponent;
    }

    private TeamModel getTeam(String token) {
        return repositoryComponent.getTeamByToken(token);
    }

    public FullTeamForm getTeamFullInfo(String token) {
        TeamModel teamModel = getTeam(token);
        return repositoryComponent.getTeamFullInfo(teamModel);
    }


    public ChangingUserDataForm changeUsername(String token, String newUsername) {
        return repositoryComponent.changeTeamUsername(token, newUsername);
    }

    // take a credit
    public TeamModel takeCredit(String token, Double credit) throws IllegalAccessException {
        if (isGameStarted) {
            TeamModel teamModel = repositoryComponent.getTeamByToken(token);
            teamModel.setScore(teamModel.getScore() + credit);
            teamModel.setCredit(teamModel.getCredit() + credit);
            teamModel.setCreditTime(System.currentTimeMillis() + PAY_TIME);
            LOGGER.warn("Taking credit " + teamModel.getUsername() + " " + teamModel.getCredit() + " " + teamModel.getCreditTime());
            return teamsRepository.save(teamModel);
        }
        throw new IllegalAccessException("The game has not started yet");
    }


    // open a contribution
    public TeamModel takeDeposit(String token, Double deposit) throws IllegalAccessException {
        if (isGameStarted) {
            TeamModel teamModel = repositoryComponent.getTeamByToken(token);
            Double tmpDeposit = teamModel.getDeposit();
            if (deposit > tmpDeposit) {
                deposit = tmpDeposit;
            }
            teamModel.setScore(teamModel.getScore() - deposit);
            teamModel.setDeposit(tmpDeposit + deposit);
            teamModel.setCreditTime(System.currentTimeMillis() + PAY_TIME);
            return teamsRepository.save(teamModel);
        }
        throw new IllegalAccessException("The game has not started yet");
    }


    // reply a Loan
    public TeamModel replyLoan(String token, Double credit) throws IllegalAccessException {
        if (isGameStarted) {
            credit = Math.round(credit * 100) / 100D;
            TeamModel teamModel = repositoryComponent.getTeamByToken(token);
            Double tmpCredit = teamModel.getCredit();
            if (credit > tmpCredit) {
                credit = tmpCredit;
            }
            tmpCredit -= credit;
            if (tmpCredit < 0.01) {
                tmpCredit = 0D;
                teamModel.setCreditTime(0L);
            }
            teamModel.setCredit(tmpCredit);
            teamModel.setScore(teamModel.getScore() - credit);
            return teamsRepository.save(teamModel);
        }
        throw new IllegalAccessException("The game has not started yet");
    }

    // withdraw money from a deposit
    public TeamModel returnDeposit(String token, Double deposit) throws IllegalAccessException {
        if (isGameStarted) {
            deposit = Math.round(deposit * 100) / 100D;
            TeamModel teamModel = repositoryComponent.getTeamByToken(token);
            Double tmpDeposit = teamModel.getDeposit();
            if (deposit > tmpDeposit) {
                deposit = tmpDeposit;
            }
            tmpDeposit -= deposit;
            teamModel.setScore(teamModel.getScore() + deposit);
            if (tmpDeposit < 0.01) {
                tmpDeposit = 0D;
                teamModel.setDepositTime(0L);
            }
            teamModel.setDeposit(tmpDeposit);
            return teamsRepository.save(teamModel);
        }
        throw new IllegalAccessException("The game has not started yet");
    }

    public void stopStartGame(boolean isGameStarted, long startTime) {
        this.isGameStarted = isGameStarted;
        lastPaySharesTime = startTime + STOCK_PRICE_CHANGE;
    }

    // !!ALARM!! Database updated 30 seconds

    class CheckingThread extends Thread {

        public void run() {
            try {
                //noinspection InfiniteLoopStatement
                while (true) {
                    LOGGER.warn("Task ok " + System.currentTimeMillis());
                    Thread.sleep(SLEEP_CHECK_TIME);
                    if (isGameStarted || lastPaySharesTime + 100 > System.currentTimeMillis()) {
                        Iterable<TeamModel> teams = teamsRepository.findAll();
                        teams.forEach((team) -> {
                            LOGGER.info("Check time for user " + team.getUsername());
                            Long tmpCreditTime = team.getCreditTime();
                            Long tmpDepositTime = team.getDepositTime();
                            LOGGER.info(tmpCreditTime + " " + System.currentTimeMillis());
                            if (tmpCreditTime != 0 && tmpCreditTime <= System.currentTimeMillis()) {

                                Double tmpCredit = team.getCredit();
                                tmpCredit = Math.round((tmpCredit * creditRate) * 1000.0) / 1000.0;
                                tmpCreditTime = System.currentTimeMillis() + PAY_TIME;
                                team.setCredit(tmpCredit);
                                team.setCreditTime(tmpCreditTime);
                                LOGGER.warn("Credit time for user " + team.getUsername() + " credit " + team.getCredit());
                            }
                            if (tmpDepositTime != 0 && tmpDepositTime <= System.currentTimeMillis()) {
                                Double tmpDeposit = team.getDeposit();
                                tmpDeposit = Math.round((tmpDeposit * depositRate) * 1000.0) / 1000.0;
                                tmpDepositTime = System.currentTimeMillis() + PAY_TIME;
                                team.setDeposit(tmpDeposit);
                                team.setDepositTime(tmpDepositTime);
                                LOGGER.warn("Deposit time for user " + team.getUsername() + " deposit " + team.getDeposit());

                            }
                            double sharesPrice = repositoryComponent.calculateFullScore(team.getUserId());
                            double score = team.getScore();
                            if (lastPaySharesTime <= System.currentTimeMillis()) {
                                score += sharesPrice * sharePercent;
                                team.setScore(score);
                            }
                            double fullScore = score - team.getCredit() + team.getDeposit() + sharesPrice;
                            team.setFullScore(fullScore);
                            LOGGER.warn("Full score for user " + team.getUsername() + " full score " + team.getFullScore());
                            teamsRepository.save(team);

                        });
                    }

                }
            } catch (InterruptedException e) {
                LOGGER.warn(e.getMessage(), e);
            }
        }
    }
}
