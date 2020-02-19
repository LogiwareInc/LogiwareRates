package com.logiware.rates.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.logiware.rates.entity.File;

public interface FileRepository extends JpaRepository<File, Long> {
	
	public List<File> findByLoadedDateBetween(Date start, Date end);

}
