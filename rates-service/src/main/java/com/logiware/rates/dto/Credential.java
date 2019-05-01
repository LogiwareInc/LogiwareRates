package com.logiware.rates.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class Credential {

	@ApiModelProperty(position = 0, required = true, example = "xyz@abc.com")
	private String username;
	@ApiModelProperty(position = 1, required = true, example = "password")
	private String password;

}
