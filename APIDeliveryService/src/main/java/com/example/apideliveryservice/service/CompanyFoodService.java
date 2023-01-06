package com.example.apideliveryservice.service;

import static com.example.apideliveryservice.exception.CompanyFoodExceptionEnum.ADD_ORDER_DUPLICATED_FOOD_NAME;
import static com.example.apideliveryservice.exception.CompanyFoodExceptionEnum.ADD_ORDER_REQUEST_PRICE_BLANK;
import static com.example.apideliveryservice.exception.CompanyFoodExceptionEnum.UPDATE_PRICE_REQUEST_PRICE_BLANK;

import com.example.apideliveryservice.dto.CompanyFoodDto;
import com.example.apideliveryservice.entity.CompanyFoodEntity;
import com.example.apideliveryservice.entity.CompanyFoodHistoryEntity;
import com.example.apideliveryservice.entity.CompanyMemberEntity;
import com.example.apideliveryservice.exception.CompanyFoodException;
import com.example.apideliveryservice.repository.CompanyFoodHistoryRepository;
import com.example.apideliveryservice.repository.CompanyFoodRepository;
import com.example.apideliveryservice.repository.CompanyMemberRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Slf4j
@RequiredArgsConstructor
public class CompanyFoodService {

    private final CompanyFoodRepository companyFoodRepository;
    private final CompanyFoodHistoryRepository companyFoodHistoryRepository;
    private final CompanyMemberRepository companyMemberRepository;

    @Transactional
    public void addFood(Long companyMemberId, String foodName, BigDecimal price)  {
        try {
            validateDuplicateFoodName(companyMemberId, foodName);
            saveCompanyFoodAndHistory(companyMemberId, foodName, price);
        } catch (DataIntegrityViolationException e) {
            throw new CompanyFoodException(ADD_ORDER_REQUEST_PRICE_BLANK.getErrormessage());
        }
    }

    @Transactional(readOnly = true)
    public List<CompanyFoodDto> findAllFoodByCompanyMemberId(Long companyMemberId) {
            CompanyMemberEntity findCompanyMember = companyMemberRepository.findById(companyMemberId)
                .orElseThrow();
            List<CompanyFoodEntity> findCompanyFoodList =
                companyFoodRepository.findAllByCompanyMemberEntity(findCompanyMember);
            List<CompanyFoodDto> companyFoodDtoList = changeFoodEntityListToDto(findCompanyFoodList);
            return companyFoodDtoList;
    }

    @Transactional(readOnly = true)
    public CompanyFoodDto findFoodById(Long companyFoodId) {
        CompanyFoodEntity findCompanyFoodEntity = companyFoodRepository.findById(companyFoodId)
            .orElseThrow();
        CompanyFoodDto findCompanyFoodDto = changeFindFoodEntityToDto(findCompanyFoodEntity);
        return findCompanyFoodDto;
    }

    @Transactional
    public void updateFoodPrice(Long companyFoodId, BigDecimal updatePrice) {
        try {
            CompanyFoodEntity findCompanyFood = companyFoodPriceUpdate(companyFoodId, updatePrice);
            saveCompanyFoodUpdatePriceHistory(updatePrice, findCompanyFood);
        } catch (DataIntegrityViolationException e) {
            throw new CompanyFoodException(UPDATE_PRICE_REQUEST_PRICE_BLANK.getErrormessage());
        }
    }

    private void saveCompanyFoodUpdatePriceHistory(BigDecimal updatePrice,
        CompanyFoodEntity findCompanyFood) {
        CompanyFoodHistoryEntity saveCompanyFoodHistory = new CompanyFoodHistoryEntity(
            findCompanyFood, updatePrice);
        companyFoodHistoryRepository.save(saveCompanyFoodHistory);
    }

    private CompanyFoodEntity companyFoodPriceUpdate(Long companyFoodId, BigDecimal updatePrice) {
        CompanyFoodEntity findCompanyFood = companyFoodRepository.findById(companyFoodId)
            .orElseThrow();
        findCompanyFood.setPrice(updatePrice);
        return findCompanyFood;
    }

    private List<CompanyFoodDto> changeFoodEntityListToDto(List<CompanyFoodEntity> allFoodEntity) {
        List<CompanyFoodDto> companyFoodDtoList = allFoodEntity.stream().map(
            m -> new CompanyFoodDto(m.getId(), m.getCompanyMemberEntity().getId(), m.getName(),
                m.getRegistrationDate(), m.getPrice())).collect(Collectors.toList());
        return companyFoodDtoList;
    }

    private CompanyFoodDto changeFindFoodEntityToDto(CompanyFoodEntity findFoodEntity) {
        CompanyFoodDto findFoodDto = new CompanyFoodDto(findFoodEntity.getId(),
            findFoodEntity.getCompanyMemberEntity().getId(), findFoodEntity.getName(),
            findFoodEntity.getRegistrationDate(), findFoodEntity.getPrice());
        return findFoodDto;
    }

    private void saveCompanyFoodAndHistory(Long companyMemberId, String foodName, BigDecimal price) {
        CompanyMemberEntity findCompanyMember = companyMemberRepository.findById(companyMemberId)
            .orElseThrow();
        CompanyFoodEntity saveCompanyFood = new CompanyFoodEntity(findCompanyMember, foodName,
            price);
        companyFoodRepository.save(saveCompanyFood);
        CompanyFoodHistoryEntity saveCompanyFoodHistory = new CompanyFoodHistoryEntity(
            saveCompanyFood, price);
        companyFoodHistoryRepository.save(saveCompanyFoodHistory);
    }

    private void validateDuplicateFoodName(Long companyMemberId, String foodName) {
        CompanyMemberEntity findCompanyMember = companyMemberRepository.findById(companyMemberId)
            .orElseThrow();
        companyFoodRepository.findByNameAndCompanyMemberEntity(foodName, findCompanyMember)
            .ifPresent(companyFood->{
                throw new CompanyFoodException(ADD_ORDER_DUPLICATED_FOOD_NAME.getErrormessage());
            });
    }
}
