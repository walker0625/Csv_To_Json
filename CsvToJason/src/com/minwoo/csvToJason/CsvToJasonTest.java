package com.minwoo.csvToJason;

import static org.junit.jupiter.api.Assertions.assertEquals; 

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

public class CsvToJasonTest {
        // Test를 위하여 1378189897(startTimeStamp) + 86400 내의 Data를 출력하고
	// 직접 계산한 후 해당 '계산값'이 CsvToJason의 결과 'dto(json 객체)'와 같은지 Test
	
	// 1378189897 160000.000000000000 0.100000000000
	// 1378224414 150000.000000000000 0.200000000000
	// 1378254765 180000.000000000000 2.940000000000 
	// 1378271222 180000.000000000000 0.500000000000
	
	// 1378343798 150000.000000000000 0.100000000000
	
	@Test
	void testFirstResult() throws CsvValidationException, IOException {
		CSVReader csvDatas = new CSVReader(new FileReader("korbitKRW.csv"));
		List<OneDayTradeDataDto> dtoList = CsvToJason.separateOneDayData(csvDatas);
		
		assertEquals(1378189897, dtoList.get(0).getStart());
		assertEquals(1378271222, dtoList.get(0).getEnd());
		assertEquals("160000", dtoList.get(0).getOpen());
		assertEquals("180000", dtoList.get(0).getClose());
		assertEquals("180000", dtoList.get(0).getHigh());
		assertEquals("150000", dtoList.get(0).getLow());
		assertEquals("167500", dtoList.get(0).getAverage());
		assertEquals("177861", dtoList.get(0).getWeightedAverage());
		assertEquals("3.74000000", dtoList.get(0).getVolume());
	}
}
