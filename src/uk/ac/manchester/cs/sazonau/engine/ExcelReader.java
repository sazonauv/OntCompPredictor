package uk.ac.manchester.cs.sazonau.engine;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class ExcelReader {
	
	/**
	 * @param fileName e.g. "/results/results.xls"
	 * @return
	 */
	public static double[][] read(String fileName) {		
		String workingDir = System.getProperty("user.dir");
		File file = new File(workingDir + fileName);
		double[][] times = null;
		try {			
			FileInputStream fileIn = new FileInputStream(file);			
			HSSFWorkbook workbook = new HSSFWorkbook(fileIn);
			
			HSSFSheet sheet = workbook.getSheetAt(0);			
			int last = sheet.getLastRowNum();
			times = new double[last][3];
			
			for (int i=1; i<=last; i++) {
				HSSFRow row = sheet.getRow(i);
				HSSFCell jcell = row.getCell(6);
				HSSFCell hcell = row.getCell(7);
				HSSFCell pcell = row.getCell(8);
				times[i-1][0] = jcell.getNumericCellValue();
				times[i-1][1] = hcell.getNumericCellValue();
				times[i-1][2] = pcell.getNumericCellValue();
			}
			
			fileIn.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return times;
	}

}
