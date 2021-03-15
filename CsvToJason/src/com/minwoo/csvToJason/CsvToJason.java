package com.minwoo.csvToJason;

import java.io.FileNotFoundException; 
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

public class CsvToJason {

	public static void main(String[] args) {
		try {
			CSVReader csvDatas = new CSVReader(new FileReader("korbitKRW.csv"));
			List<OneDayTradeDataDto> dtoList = separateOneDayData(csvDatas);

			// Output : JSON Array
			String[] jsonArray = convertJson(dtoList);

			for (String json : jsonArray) {
				System.out.println(json);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static List<OneDayTradeDataDto> separateOneDayData(CSVReader csvDatas) {
		List<OneDayTradeDataDto> dtoList = new ArrayList<OneDayTradeDataDto>();
		List<String[]> oneDayDatas = new ArrayList<String[]>();
		final int timeStampGap = 86400; // 1일(86400초)
		int startTimeStamp = 0;
		int nextTimeStamp = 0;

		try {
			// firstLine의 startTimeStamp 초기화 및 dto 저장
			String[] nextLine = csvDatas.readNext();
			startTimeStamp = Integer.parseInt(nextLine[0]);
			oneDayDatas.add(nextLine);

			while ((nextLine = csvDatas.readNext()) != null) {
				nextTimeStamp = Integer.parseInt(nextLine[0]);

				// json 객체 구분 기준 = 1일(86400초)
				if (nextTimeStamp - startTimeStamp <= timeStampGap) {
					oneDayDatas.add(nextLine);
				} else {
					// 1일 data가 모이면 data를 계산(평균, 가중평균 등) 메소드로 넘기고, 계산 결과 dto를 return할 List에 추가
					dtoList.add(calculateOneDayTradeDatas(oneDayDatas));

					// 초기화
					oneDayDatas = new ArrayList<String[]>();
					oneDayDatas.add(nextLine);
					startTimeStamp = nextTimeStamp + 1;
				}
			}

		} catch (CsvValidationException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return dtoList;
	}

	public static OneDayTradeDataDto calculateOneDayTradeDatas(List<String[]> oneDayDatas) {
		int oneDayDatasSize = oneDayDatas.size();
		int thisIndexPrice = 0;
		int highestPrice = 0;
		int lowestPrice = (int) Float.parseFloat(oneDayDatas.get(0)[1]);
		int totalTradePrice = 0;
		int totalTradeCount = 0;
		BigDecimal totalWeightTradePrice = new BigDecimal(0.0);
		BigDecimal volume = new BigDecimal(0.0);

		for (int i = 0; i < oneDayDatasSize; i++) {
			thisIndexPrice = (int) Float.parseFloat(oneDayDatas.get(i)[1]);

			if (highestPrice < thisIndexPrice) {
				highestPrice = thisIndexPrice;
			}

			if (lowestPrice > thisIndexPrice) {
				lowestPrice = thisIndexPrice;
			}

			totalTradePrice += thisIndexPrice;
			totalTradeCount++;
			totalWeightTradePrice = totalWeightTradePrice
					.add(new BigDecimal(oneDayDatas.get(i)[2]).multiply(new BigDecimal(thisIndexPrice)));

			volume = volume.add(new BigDecimal(oneDayDatas.get(i)[2]));
		}

		OneDayTradeDataDto oneDayTradeDataDto = new OneDayTradeDataDto();

		oneDayTradeDataDto.setStart(Integer.parseInt(oneDayDatas.get(0)[0]));
		oneDayTradeDataDto.setEnd(Integer.parseInt(oneDayDatas.get(oneDayDatasSize - 1)[0]));
		oneDayTradeDataDto.setOpen(oneDayDatas.get(0)[1].substring(0, oneDayDatas.get(0)[1].indexOf(".")));
		oneDayTradeDataDto.setClose(oneDayDatas.get(oneDayDatasSize - 1)[1].substring(0, oneDayDatas.get(oneDayDatasSize - 1)[1].indexOf(".")));
		oneDayTradeDataDto.setHigh(Integer.toString(highestPrice));
		oneDayTradeDataDto.setLow(Integer.toString(lowestPrice));
		oneDayTradeDataDto.setAverage(Integer.toString(totalTradePrice / totalTradeCount));
		oneDayTradeDataDto.setWeightedAverage(totalWeightTradePrice.divide(volume, 0, RoundingMode.HALF_EVEN).toString());
		oneDayTradeDataDto.setVolume(volume.setScale(8, RoundingMode.HALF_EVEN).toString());

		return oneDayTradeDataDto;
	}

	public static String[] convertJson(List<OneDayTradeDataDto> dtoList) {
		ObjectMapper mapper = new ObjectMapper();
		int dtoListSize = dtoList.size();
		String[] jsonArray = new String[dtoListSize];

		for (int i = 0; i < dtoListSize; i++) {
			try {
				jsonArray[i] = mapper.writeValueAsString(dtoList.get(i));
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		}

		return jsonArray;
	}

}
