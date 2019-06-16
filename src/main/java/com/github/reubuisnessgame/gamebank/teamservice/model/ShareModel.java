package com.github.reubuisnessgame.gamebank.teamservice.model;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
@Table(name = "share")
public class ShareModel {

    @Id
    @GeneratedValue
    @Column(name = "shares_id")
    private Long sharesId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "company_id")
    private Long companyId;

    @Column(name = "number")
    private Long sharesNumbers;

    @ManyToOne
    @JoinColumn(name = "company_model", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private CompanyModel companyModel;

    public ShareModel() {
    }

    public ShareModel(Long userId, Long companyId, Long sharesNumbers, CompanyModel companyModel) {
        this.userId = userId;
        this.companyId = companyId;
        this.sharesNumbers = sharesNumbers;
        this.companyModel = companyModel;
    }

    public Long getSharesId() {
        return sharesId;
    }

    public void setSharesId(Long sharesId) {
        this.sharesId = sharesId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Long getSharesNumbers() {
        return sharesNumbers;
    }

    public void setSharesNumbers(Long sharesNumbers) {
        this.sharesNumbers = sharesNumbers;
    }

    public CompanyModel getCompanyModel() {
        return companyModel;
    }

    public void setCompanyModel(CompanyModel companyModel) {
        this.companyModel = companyModel;
    }
}
