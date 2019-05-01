package com.logiware.rates.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.logiware.rates.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

	User findByUsername(String loginName);

}
