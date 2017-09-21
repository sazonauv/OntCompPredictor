package uk.ac.manchester.cs.sazonau.engine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import uk.ac.manchester.cs.sazonau.ontology.features.Solution;

public class ExcelWriter {
	
	public static final String fileName = "/reports/common-times.xls";
	public static final String sheetName = "Performance measurements";
	
	public static final String comparisonName = "/results/comparison.xls";
	
	
	public static void write(double[][] ctimes, String name) {
		String workingDir = System.getProperty("user.dir");
		File file = new File(workingDir + "/reports/"+name+".xls");
		try {
			// If the file doesn't exist then create it
			if (!file.exists()) {
					file.createNewFile();
			}
			
			FileOutputStream fileOut = new FileOutputStream(file);
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet(sheetName);
			
			for (int i=0; i<ctimes.length; i++) {
				HSSFRow row = sheet.createRow(i);
				for (int j=0; j<ctimes[0].length; j++) {
					HSSFCell cell = row.createCell(j);
					cell.setCellValue(ctimes[i][j]);
				}
			}
			
			workbook.write(fileOut);
			fileOut.flush();
			fileOut.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public static void write(double[] arr, String name) {
		String workingDir = System.getProperty("user.dir");
		File file = new File(workingDir + "/reports/"+name+".xls");
		try {
			// If the file doesn't exist then create it
			if (!file.exists()) {
					file.createNewFile();
			}
			
			FileOutputStream fileOut = new FileOutputStream(file);
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet(sheetName);
			
			for (int i=0; i<arr.length; i++) {
				HSSFRow row = sheet.createRow(i);				
				HSSFCell cell = row.createCell(0);
				cell.setCellValue(arr[i]);
			}
			
			workbook.write(fileOut);
			fileOut.flush();
			fileOut.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	
	public static void write(Map<String, Integer> map, String name) {
		String workingDir = System.getProperty("user.dir");
		File file = new File(workingDir + "/reports/"+name+".xls");
		try {
			// If the file doesn't exist then create it
			if (!file.exists()) {
					file.createNewFile();
			}
			
			FileOutputStream fileOut = new FileOutputStream(file);
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet(sheetName);
			
			Set<String> keys = map.keySet();
			int i=0;
			for (String key : keys) {
				HSSFRow row = sheet.createRow(i++);
				HSSFCell cell1 = row.createCell(0);
				cell1.setCellValue(key);
				HSSFCell cell2 = row.createCell(1);
				cell2.setCellValue(map.get(key).doubleValue());
			}			
			
			workbook.write(fileOut);
			fileOut.flush();
			fileOut.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
		
	public static void writeComparison(double[][] ctimes1, double[][] ctimes2) {
		String workingDir = System.getProperty("user.dir");
		File file = new File(workingDir + comparisonName);
		try {
			// If the file doesn't exist then create it
			if (!file.exists()) {
					file.createNewFile();
			}
			
			FileOutputStream fileOut = new FileOutputStream(file);
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet(sheetName);
			
			for (int i=0; i<ctimes1.length; i++) {
				HSSFRow row = sheet.createRow(i);
				for (int j=0; j<ctimes1[0].length; j++) {
					HSSFCell cell = row.createCell(j);
					cell.setCellValue(ctimes1[i][j]);
				}
				for (int j=0; j<ctimes2[0].length; j++) {
					HSSFCell cell = row.createCell(ctimes1[0].length + j);
					cell.setCellValue(ctimes2[i][j]);
				}
			}
			
			workbook.write(fileOut);
			fileOut.flush();
			fileOut.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void writeOntTests(File[] ontFiles, double[][] ctimes) {
		String workingDir = System.getProperty("user.dir");
		File file = new File(workingDir + fileName);
		try {
			// If the file doesn't exist then create it
			if (!file.exists()) {
					file.createNewFile();
			}
			
			FileOutputStream fileOut = new FileOutputStream(file);
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet(sheetName);
			
			for (int i=0; i<ctimes.length; i++) {
				HSSFRow row = sheet.createRow(i);
				HSSFCell cell = row.createCell(0);
				cell.setCellValue(ontFiles[i].getName());
				for (int j=0; j<ctimes[0].length; j++) {
					cell = row.createCell(j+1);
					cell.setCellValue(ctimes[i][j]);
				}
			}
			
			workbook.write(fileOut);
			fileOut.flush();
			fileOut.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public static void writeFront(Solution[] sols, ArrayList<Solution> front, String reasoner) {
		String workingDir = System.getProperty("user.dir");
		String frontName = "/results/"+reasoner+"-PF.xls";
		File file = new File(workingDir + frontName);
		try {
			// If the file doesn't exist then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			FileOutputStream fileOut = new FileOutputStream(file);
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet(sheetName);

			// all points
			for (int i=0; i<sols.length; i++) {
				HSSFRow row = sheet.createRow(i);
				Solution s = sols[i];
				HSSFCell cell0 = row.createCell(0);
				cell0.setCellValue(s.fitness/Solution.maxerr);
				HSSFCell cell1 = row.createCell(1);
				cell1.setCellValue(s.vfalse);
			}

			// pareto front
			int k=0;
			for (int i=0; i<front.size(); i++) {
				Solution s = front.get(i);				
				k++;
				HSSFRow row = sheet.createRow(sols.length+5+k);					
				HSSFCell cell0 = row.createCell(0);
				cell0.setCellValue(s.fitness/Solution.maxerr);
				HSSFCell cell1 = row.createCell(1);
				cell1.setCellValue(s.vfalse);
				for (int xi=0; xi<s.x.length; xi++) {
					HSSFCell cell = row.createCell(2+xi);
					cell.setCellValue(s.x[xi]);
				}
				
			}

			workbook.write(fileOut);
			fileOut.flush();
			fileOut.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}	

}
