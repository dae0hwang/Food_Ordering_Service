package com.example.apideliveryservice.repository;

import com.example.apideliveryservice.dto.CompanyMemberDto;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CompanyMemberRepository {

    private Connection connectJdbc() throws SQLException {
        String server = "localhost:3307";
        String database = "delivery_service";
        String user_name = "root";
        String password = "111111";
        return DriverManager.getConnection(
            "jdbc:mysql://" + server + "/" + database + "?useSSL=false", user_name, password);
    }

    public void save(CompanyMemberDto companyMember) throws SQLException {
        String sql = "INSERT INTO company_members "
            + "(login_name, password, name, phone_verification, registration_date)"
            + "VALUES(?,?,?,?,?)";
        try (Connection connection = connectJdbc();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, companyMember.getLoginName());
            preparedStatement.setString(2, companyMember.getPassword());
            preparedStatement.setString(3, companyMember.getName());
            preparedStatement.setString(4,
                String.valueOf(companyMember.getPhoneVerification()));
            preparedStatement.setString(5,
                String.valueOf(Timestamp.valueOf(companyMember.getCreatedAt())));
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            log.error("SQLException", e);
            throw e;
        }
    }

    public Optional<CompanyMemberDto> findByName(String findName) {
        String sql = "SELECT * FROM company_members WHERE name=?";
        try (Connection connection = connectJdbc();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, findName);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    CompanyMemberDto companyMemberDto = getCompanyMemberDto(resultSet);
                    return Optional.of(companyMemberDto);
                }
            }
        } catch (SQLException e) {
            log.error("SQLException", e);
        }
        return Optional.empty();
    }

    public Optional<CompanyMemberDto> findByLoginName(String loginName) {
        String sql = "SELECT * FROM company_members WHERE login_name=?";
        try (Connection connection = connectJdbc();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, loginName);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    CompanyMemberDto companyMemberDto = getCompanyMemberDto(resultSet);
                    return Optional.of(companyMemberDto);
                }
            }
        } catch (SQLException e) {
            log.error("SQLException", e);
        }
        return Optional.empty();
    }

    private CompanyMemberDto getCompanyMemberDto(ResultSet resultSet) throws SQLException {
        String loginName = resultSet.getString(2);
        String password = resultSet.getString(3);
        String name = resultSet.getString(4);
        int phoneVerification = resultSet.getInt(5);
        LocalDateTime createAt = LocalDate.parse(resultSet.getString(6)
            , DateTimeFormatter.ISO_DATE).atStartOfDay();
        return new CompanyMemberDto(loginName, password, name, phoneVerification
            , createAt);
    }
}
