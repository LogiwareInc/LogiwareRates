package com.logiware.rates.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "file")
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class File implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "id")
	@EqualsAndHashCode.Include
	@ToString.Include
	private Long id;
	@Basic(optional = false)
	@Column(name = "name")
	private String name;
	@Basic(optional = false)
    @Lob
	@Column(name = "path")
	private String path;
	@Basic(optional = false)
	@Column(name = "shipment_type")
	private String shipmentType;
	@Basic(optional = false)
	@Column(name = "rates_type")
	private String ratesType;
	@Basic(optional = false)
	@Column(name = "loaded_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date loadedDate;
	@JoinColumn(name = "company_id", referencedColumnName = "id")
	@ManyToOne(optional = false)
	private Company company;
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "file")
	private List<History> histories;

}
