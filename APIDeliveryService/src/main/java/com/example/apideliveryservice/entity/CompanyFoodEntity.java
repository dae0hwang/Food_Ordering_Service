package com.example.apideliveryservice.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "company_food")
public class CompanyFoodEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "company_member_id", nullable = false)
    private CompanyMemberEntity companyMemberEntity;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private BigDecimal price;
    @Column(name = "registration_date", nullable = false)
    private Timestamp registrationDate;

    public CompanyFoodEntity(CompanyMemberEntity companyMemberEntity, String name, BigDecimal price, Timestamp registrationDate) {
        this.companyMemberEntity = companyMemberEntity;
        this.name = name;
        this.price = price;
        this.registrationDate = registrationDate;
    }
}
