package com.logiware.rates.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.logiware.rates.entity.File;
import com.logiware.rates.entity.History;

public interface HistoryRepository extends JpaRepository<History, Long> {
	

}
