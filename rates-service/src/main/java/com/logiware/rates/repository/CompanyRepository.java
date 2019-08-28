package com.logiware.rates.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.logiware.rates.entity.Company;


public interface CompanyRepository extends JpaRepository<Company, Long> {
	
	Company findByName(String name);
	
}
