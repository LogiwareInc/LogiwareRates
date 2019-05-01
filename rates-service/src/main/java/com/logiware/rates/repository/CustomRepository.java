package com.logiware.rates.repository;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.logiware.rates.dto.ResultModel;
import com.logiware.rates.entity.Company;
import com.logiware.rates.entity.File;
import com.logiware.rates.entity.Site;
import com.logiware.rates.entity.SiteFile;
import com.logiware.rates.util.DateUtils;
import com.logiware.rates.util.StringUtils;

@Service
public class CustomRepository {

	@Autowired
	private EntityManager entityManager;

	public List<ResultModel> findFilesByCarrierOrLoadedDate(Company company, String carrier, String fromDate, String toDate) throws ParseException {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<File> query = builder.createQuery(File.class);
		Root<File> root = query.from(File.class);
		Predicate predicateForCompany = builder.equal(root.get("company").get("id"), company.getId());
		Predicate predicateForCarrier = null;
		Predicate predicateForLoadedDate = null;
		Predicate finalPredicate = null;
		if (StringUtils.isNotEmpty(carrier)) {
			predicateForCarrier = builder.equal(root.get("carrier"), carrier);
		}
		if (StringUtils.isAllNotEmpty(fromDate, toDate)) {
			Date from = DateUtils.parseToDate(fromDate + " 00:00:00", "MM/dd/yyyy kk:mm:ss");
			Date to = DateUtils.parseToDate(toDate + " 23:59:59", "MM/dd/yyyy kk:mm:ss");
			predicateForLoadedDate = builder.between(root.get("loadedDate"), from, to);
		} else if (StringUtils.isNotEmpty(fromDate)) {
			Date from = DateUtils.parseToDate(fromDate + " 00:00:00", "MM/dd/yyyy kk:mm:ss");
			predicateForLoadedDate = builder.greaterThanOrEqualTo(root.get("loadedDate"), from);
		} else if (StringUtils.isNotEmpty(toDate)) {
			Date to = DateUtils.parseToDate(toDate + " 23:59:59", "MM/dd/yyyy kk:mm:ss");
			predicateForLoadedDate = builder.lessThanOrEqualTo(root.get("loadedDate"), to);
		}
		if (predicateForCarrier != null && predicateForLoadedDate != null) {
			finalPredicate = builder.and(predicateForCompany, predicateForCarrier, predicateForLoadedDate);
		} else if (predicateForCarrier != null) {
			finalPredicate = builder.and(predicateForCompany, predicateForCarrier);
		} else if (predicateForLoadedDate != null) {
			finalPredicate = builder.and(predicateForCompany, predicateForLoadedDate);
		}
		if (finalPredicate != null) {
			query.where(finalPredicate);
		}
		List<File> files = entityManager.createQuery(query).getResultList();
		List<ResultModel> results = new ArrayList<>();
		files.forEach(file -> {
			ResultModel result = new ResultModel();
			result.setCarrier(file.getCarrier());
			result.setScac(file.getScac());
			result.setEffectiveDate(DateUtils.formatToString(file.getEffectiveDate(), "MM/dd/yyyy"));
			result.setExpirationDate(DateUtils.formatToString(file.getExpirationDate(), "MM/dd/yyyy"));
			result.setFilename(file.getName());
			result.setLoadedDate(DateUtils.formatToString(file.getLoadedDate(), "MM/dd/yyyy hh:mm a"));
			result.setSites(file.getSiteFiles().stream().map(SiteFile::getSite).map(Site::getName).collect(Collectors.joining(",")));
			result.setId(file.getId());
			results.add(result);
		});
		return results;
	}

}
