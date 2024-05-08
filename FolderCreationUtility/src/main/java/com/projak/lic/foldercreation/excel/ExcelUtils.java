package com.projak.lic.foldercreation.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.IntStream;

import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFTable;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.filenet.api.collection.ContentElementList;
import com.filenet.api.constants.AutoClassify;
import com.filenet.api.constants.CheckinType;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.core.ContentTransfer;
import com.filenet.api.core.Document;
import com.filenet.api.core.Factory;
import com.filenet.api.core.ObjectStore;
import com.projak.lic.foldercreation.filenet.FilenetUtils;

public class ExcelUtils {
	
	static void getTableList(XSSFSheet sheet){
		
		List<XSSFTable> tables = sheet.getTables();
		
		for (XSSFTable xssfTable : tables) {
			
			int startRow = xssfTable.getStartRowIndex() + 1;
			
			int lastRow = xssfTable.getEndRowIndex();
			
			int startCol = xssfTable.getStartColIndex();
			
			int lastCol = xssfTable.getEndColIndex();
			
			
			IntStream.range(startRow, lastRow+1).forEach(i -> {
				
				XSSFRow row = sheet.getRow(i);
				
				IntStream.range(startCol, lastCol+1).forEach(j -> {
					
					XSSFCell cell = row.getCell(j);
					
					System.out.println(cell.getStringCellValue());
					
					if(xssfTable.getName().equalsIgnoreCase("Location")) {
						
					}
					
				});
				
				
				
			});
			
		}
		
	}
	
	
	public static void main(String[] args) throws IOException {
		
		ObjectStore os = FilenetUtils.fetchObjectStore();
		
		Document doc = Factory.Document.createInstance(os, "Document");
		
		doc.getProperties().putValue("DocumentTitle", "Plugin6.jar");
		
		doc.set_MimeType("application/java-archive");
		
		FileInputStream fis = new FileInputStream(new File("D:\\IBM\\LIC\\FolderStructurePlugin\\FolderStructurePlugin.jar"));
		
		ContentElementList cel = Factory.ContentElement.createList();
		
		ContentTransfer ct = Factory.ContentTransfer.createInstance();
		
		ct.setCaptureSource(fis);
		
		ct.set_RetrievalName("Plugin6.jar");
		
		ct.set_ContentType("application/java-archive");
		
		cel.add(ct);
		
		doc.set_ContentElements(cel);
		
		doc.checkin(AutoClassify.DO_NOT_AUTO_CLASSIFY, CheckinType.MAJOR_VERSION);
		
		doc.save(RefreshMode.REFRESH);
		
		System.out.println("Document Id - " + doc.get_Id().toString());
		
	}

}
