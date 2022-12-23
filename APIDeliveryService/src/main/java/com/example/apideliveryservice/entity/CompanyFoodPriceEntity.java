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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "company_food_price")
public class CompanyFoodPriceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "company_food_id", nullable = false)
    private CompanyFoodEntity companyFoodEntity;
    @Column(nullable = false)
    private BigDecimal price;
    @Column(name = "update_date", nullable = false)
    private Timestamp updateDate;
}
