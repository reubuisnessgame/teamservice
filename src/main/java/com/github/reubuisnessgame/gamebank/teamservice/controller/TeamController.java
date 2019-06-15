package com.github.reubuisnessgame.gamebank.teamservice.controller;

import com.github.reubuisnessgame.gamebank.teamservice.dao.TeamsDAO;
import com.github.reubuisnessgame.gamebank.teamservice.form.StartGameForm;
import com.github.reubuisnessgame.gamebank.teamservice.model.ExceptionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/team")
public class TeamController {

    private final TeamsDAO teamsDAO;

    public TeamController(TeamsDAO teamsDAO) {
        this.teamsDAO = teamsDAO;
    }

    @PreAuthorize("hasAuthority('TEAM')")
    @RequestMapping(value = "/me")
    public @ResponseBody
    ResponseEntity getTeam(@RequestHeader(value = "Authorization") String token) {
        try {
            return ResponseEntity.ok(teamsDAO.getTeamFullInfo(token));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(404).body(new ExceptionModel(404, "Not Found", e.getMessage(), "/team/me"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ExceptionModel(500, "Internal Error", e.getMessage(), "/team/me"));
        }
    }

    @PreAuthorize("hasAuthority('TEAM')")
    @RequestMapping(value = "/change_usr", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity changeUsername(@RequestHeader(value = "Authorization") String token,
                                  @RequestParam(value = "usr") String newUsername) {
        try {
            return ResponseEntity.ok(teamsDAO.changeUsername(token, newUsername));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(403).body(new ExceptionModel(403, "Forbidden", e.getMessage(), "/team/change_usr"));
        } catch (Throwable e) {
            return ResponseEntity.status(500).body(new ExceptionModel(500, "Internal Error", e.getMessage(), "/team/change_usr"));
        }
    }

    @PreAuthorize("hasAuthority('TEAM')")
    @RequestMapping(value = "/take/credit", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity takeCredit(@RequestHeader(value = "Authorization") String token,
                              @RequestParam(value = "sum") Double credit) {
        try {
            return ResponseEntity.ok(teamsDAO.takeCredit(token, credit));
        }catch (IllegalAccessException | UsernameNotFoundException e) {
            return ResponseEntity.status(403).body(new ExceptionModel(403, "Forbidden", e.getMessage(), "/team/take_credit"));
        } catch (Throwable e) {
            return ResponseEntity.status(500).body(new ExceptionModel(500, "Internal Error", e.getMessage(), "/team/take_credit"));
        }
    }

    @PreAuthorize("hasAuthority('TEAM')")
    @RequestMapping(value = "/take/deposit", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity takeDeposit(@RequestHeader(value = "Authorization") String token,
                              @RequestParam(value = "sum") Double deposit) {
        try {
            return ResponseEntity.ok(teamsDAO.takeDeposit(token, deposit));
        } catch (IllegalAccessException | UsernameNotFoundException e) {
            return ResponseEntity.status(403).body(new ExceptionModel(403, "Forbidden", e.getMessage(), "/team/take_deposit"));
        } catch (Throwable e) {
            return ResponseEntity.status(500).body(new ExceptionModel(500, "Internal Error", e.getMessage(), "/team/take_deposit"));
        }
    }

    @PreAuthorize("hasAuthority('TEAM')")
    @RequestMapping(value = "/rtn/credit", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity replyLoan(@RequestHeader(value = "Authorization") String token,
                               @RequestParam(value = "sum") Double credit) {
        try {
            return ResponseEntity.ok(teamsDAO.replyLoan(token, credit));
        } catch (IllegalAccessException | UsernameNotFoundException e) {
            return ResponseEntity.status(403).body(new ExceptionModel(403, "Forbidden", e.getMessage(), "/team/rpl_credit"));
        } catch (Throwable e) {
            return ResponseEntity.status(500).body(new ExceptionModel(500, "Internal Error", e.getMessage(), "/team/rpl_credit"));
        }
    }

    @PreAuthorize("hasAuthority('TEAM')")
    @RequestMapping(value = "/rtn/deposit", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity returnDeposit(@RequestHeader(value = "Authorization") String token,
                               @RequestParam(value = "sum") Double deposit) {
        try {
            return ResponseEntity.ok(teamsDAO.returnDeposit(token, deposit));
        } catch (IllegalAccessException | UsernameNotFoundException e) {
            return ResponseEntity.status(403).body(new ExceptionModel(403, "Forbidden", e.getMessage(), "/team/rtn_deposit"));
        } catch (Throwable e) {
            return ResponseEntity.status(500).body(new ExceptionModel(500, "Internal Error", e.getMessage(), "/team/rtn_deposit"));
        }
    }

    @PreAuthorize("hasAuthority('MODERATOR')")
    @RequestMapping(value = "/game", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity startGame(@RequestBody StartGameForm form) {
        try {
            teamsDAO.stopStartGame(form.isStated(), form.getTime());
            return ResponseEntity.ok().build();
        } catch (Throwable t) {
            return ResponseEntity.status(500).body(new ExceptionModel(500, "Internal Error", t.getMessage(), "/admin/start"));
        }
    }



}
