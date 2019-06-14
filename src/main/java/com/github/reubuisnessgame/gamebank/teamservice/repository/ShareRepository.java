package com.github.reubuisnessgame.gamebank.teamservice.repository;

import com.github.reubuisnessgame.gamebank.teamservice.model.ShareModel;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;
import java.util.Optional;

public interface ShareRepository extends CrudRepository<ShareModel, Long> {

    Iterable<ShareModel> findAllByUserId(Long userId);
    Optional<ShareModel> findByCompanyIdAndUserId(Long companyId, Long userId);
    void deleteAllByCompanyId(Long companyId);
    void deleteByCompanyIdAndUserId(Long companyId, Long userId);
}
