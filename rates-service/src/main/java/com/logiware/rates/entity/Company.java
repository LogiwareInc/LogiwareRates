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
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
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
@Table(name = "company")
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Company implements Serializable {

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
	@Column(name = "logo")
	private byte[] logo;
	@Basic(optional = false)
	@Column(name = "db_url")
	private String dbUrl;
	@Basic(optional = false)
	@Column(name = "db_user")
	private String dbUser;
	@Basic(optional = false)
	@Column(name = "db_password")
	private String dbPassword;
	@Basic(optional = false)
	@Column(name = "loading_query")
	private String loadingQuery;
	@Basic(optional = false)
	@Column(name = "created_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "company")
	private List<User> users;
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "company")
	private List<Site> sites;
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "company")
	private List<File> files;

}
