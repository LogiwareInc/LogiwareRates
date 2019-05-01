package com.logiware.rates.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.logiware.rates.entity.SiteFile;

public interface SiteFileRepository extends JpaRepository<SiteFile, Long> {
	

}
