package com.projak.lic.foldercreation.filenet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;

import javax.accessibility.AccessibleHyperlink;
import javax.security.auth.Subject;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFTable;

import com.filenet.api.collection.AccessPermissionList;
import com.filenet.api.constants.AccessType;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.core.Connection;
import com.filenet.api.core.Domain;
import com.filenet.api.core.Factory;
import com.filenet.api.core.Folder;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.exception.EngineRuntimeException;
import com.filenet.api.security.AccessPermission;
import com.filenet.api.util.Id;
import com.filenet.api.util.UserContext;
import com.projak.lic.foldercreation.utils.PropertyReader;

public class FilenetUtils {

	public static ObjectStore fetchObjectStore() {

		String userName = PropertyReader.getProperty("USER");
		String password = PropertyReader.getProperty("PASSWORD");
		String stanza = PropertyReader.getProperty("STANZA");
		String url = PropertyReader.getProperty("URL");
		String dom = PropertyReader.getProperty("filenet.domain");
		String ObjectStore = PropertyReader.getProperty("filenet.os");

		Connection con = Factory.Connection.getConnection(url);
		Subject sub = UserContext.createSubject(con, userName, password, stanza);

		UserContext.get().pushSubject(sub);

		Domain domain = Factory.Domain.fetchInstance(con, dom, null);

		ObjectStore os = Factory.ObjectStore.fetchInstance(domain, ObjectStore, null);

		return os;

	}

	public static HashMap<String, List<Object>> accessMaskTable(XSSFTable xssfTable) {

		System.out.println("FilenetUtils.accessMaskTable.entry() ");

		int startRow = xssfTable.getStartRowIndex() + 1;

		int lastRow = xssfTable.getEndRowIndex();

		int startCol = xssfTable.getStartColIndex();

		HashMap<String, List<Object>> maps = new HashMap<String, List<Object>>();

		IntStream.range(startRow, lastRow + 1).forEach(i -> {

			XSSFRow row = xssfTable.getXSSFSheet().getRow(i);

			XSSFCell cell = row.getCell(startCol);

			if (cell != null) {

				String tableName = cell.getStringCellValue();

				cell = row.getCell(startCol + 1);

				if (cell != null) {

					Integer accessMask = (int) cell.getNumericCellValue();

					cell = row.getCell(startCol + 2);

					if (cell != null) {

						String documentClass = cell.getStringCellValue();

						List<Object> obl = new ArrayList<Object>();

						obl.add(accessMask);

						obl.add(documentClass);

						maps.put(tableName, obl);

					}

				}

			}

		});

		return maps;
	}

	public static void createFolders(Folder folder, XSSFTable xssfTable, HashMap<String, List<Object>> accessMaskMap) {

		System.out.println("FilenetUtils.createFolders.entry() ");

		String tableName = xssfTable.getName();

		int startRow = xssfTable.getStartRowIndex() + 1;

		int lastRow = xssfTable.getEndRowIndex();

		int startCol = xssfTable.getStartColIndex();

		System.out.println("Starting iteration of the table - " + xssfTable.getName());

		List<Object> maps = accessMaskMap.get(tableName);

		Integer _accessMask = (Integer) maps.get(0);

		if (_accessMask == 0) {
			_accessMask = 131073;
		}
		
		
		Integer accessMask = _accessMask;

		String documentClass = (String) maps.get(1);

		IntStream.range(startRow, lastRow + 1).forEach(i -> {
			
			try{
			
				XSSFRow row = xssfTable.getXSSFSheet().getRow(i);

				XSSFCell cell = row.getCell(startCol);

				if (cell != null) {

					String folderName = cell.getStringCellValue();
					
					System.out.println("Folder Name in the cell - " + folderName);

					Folder subFolder = null;

					Folder fda[] = new Folder[1];

					folder.get_SubFolders().iterator().forEachRemaining(fd -> {
						Folder fold = (Folder) fd;

						if (fold.get_FolderName().equals(folderName)) {
							fda[0] = fold;
						}

					});

					subFolder = fda[0];
					
					System.out.println("Folder Found - "+subFolder);

					if (subFolder == null && folderName != null && !folderName.isEmpty()) {
						
						subFolder = folder.createSubFolder(folderName);
						
						subFolder.changeClass(documentClass);
						
						subFolder = Factory.Folder.createInstance(folder.getObjectStore(), documentClass);
						
						subFolder.set_FolderName(folderName);
						
						subFolder.set_Parent(folder);
						
						System.out.println("Creating Folder - "+folderName);
						
						subFolder.save(RefreshMode.REFRESH);
						
						System.out.println("Folder Created");
						
					}else {
//						System.out.println("Folder Found - "+folderName);
						
						subFolder.refresh();
						
					}

					AccessPermission permission = Factory.AccessPermission.createInstance();

					permission.set_GranteeName("#AUTHENTICATED-USERS");

					permission.set_AccessMask(accessMask);

					permission.set_AccessType(AccessType.ALLOW);

					subFolder.get_Permissions().add(permission);

					subFolder.save(RefreshMode.REFRESH);

					XSSFCell _cell = row.getCell(startCol + 1);

					String childTableName = _cell != null ? _cell.getStringCellValue() : null;

					if (childTableName != null && !childTableName.isEmpty()) {

						XSSFTable childTable = xssfTable.getXSSFSheet().getWorkbook().getTable(childTableName);

						if (childTable != null) {

							createFolders(subFolder, childTable, accessMaskMap);

						}

					}

				}
				
			}catch(Exception e) {
				
				System.out.println("Exception while iteration - " + e.getLocalizedMessage());
				
			}

		});

		System.out.println("FilenetUtils.createFolders.exit() ");

	}

	public static void main(String[] args) {

//		ObjectStore store = fetchObjectStore();
//
//		Folder fold = Factory.Folder.fetchInstance(store, new Id("{3BC58081-504B-4B99-8CD9-4D8E4F02E967}"), null);
//
//		AccessPermissionList acl = fold.get_Permissions();
//
//		Iterator<AccessPermission> ap = acl.iterator();
//
//		while (ap.hasNext()) {
//
//			AccessPermission perm = ap.next();
//
//			System.out.println(perm.get_GranteeName());
//
//			System.out.println(perm.get_AccessMask());
//
//		}

		// Reader for folder - 131073

		// Author for folder - 131121
		
//		createFolders(null, null, null);

	}

}
