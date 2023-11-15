package com.eda.security.repository;

import java.util.List;
import java.util.Optional;

import com.eda.security.entity.TokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TokenRepository extends JpaRepository<TokenEntity, Integer> {

  @Query(value = " select t from TokenEntity t inner join UserEntity u " +
          "on t.userEntity.id = u.id where u.id = :id and (t.expired = false " +
          "or t.revoked = false) ")
  List<TokenEntity> findAllValidTokenByUser(Integer id);

  Optional<TokenEntity> findByToken(String token);
}
