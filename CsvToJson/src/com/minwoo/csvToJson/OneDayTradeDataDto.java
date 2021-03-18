package com.minwoo.csvToJson;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class OneDayTradeDataDto {
	int start;
	int end;
	String open;
	String close;
	String high;
	String low;
	String average;
	@JsonProperty("weighted_average")
	String weightedAverage;
	String volume;
}
