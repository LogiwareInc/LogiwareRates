package com.logiware.rates.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.logiware.rates.entity.File;

public interface FileRepository extends JpaRepository<File, Long> {
	

}
