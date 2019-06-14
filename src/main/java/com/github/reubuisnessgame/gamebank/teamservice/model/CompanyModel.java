package com.github.reubuisnessgame.gamebank.teamservice.model;

import javax.persistence.*;

@Entity
@Table(name = "company")
public class CompanyModel {

    @Id
    @GeneratedValue
    @Column(name = "company_id", unique = true)
    private Long id;

    @Column(name = "share_price")
    private Double sharePrice;

    @Column(name = "company_name", unique = true, nullable = false)
    private String companyName;

    public CompanyModel() {
    }

    public CompanyModel(String companyName, Double sharePrice) {
        this.sharePrice = sharePrice;
        this.companyName = companyName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getSharePrice() {
        return sharePrice;
    }

    public void setSharePrice(Double sharePrice) {
        this.sharePrice = sharePrice;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
}
