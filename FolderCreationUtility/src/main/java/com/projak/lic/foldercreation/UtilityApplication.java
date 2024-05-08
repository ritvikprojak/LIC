package com.projak.lic.foldercreation;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.IntStream;

import org.apache.http.HttpRequest;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.util.EntityUtils;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFTable;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;

import com.filenet.api.core.Factory;
import com.filenet.api.core.Folder;
import com.filenet.api.core.ObjectStore;
import com.projak.lic.foldercreation.filenet.FilenetUtils;
import com.projak.lic.foldercreation.utils.PropertyReader;

public class UtilityApplication {

	public static void main(String[] args) throws IOException {

		String path = args[0];

		System.out.println("UtilityApplication.main.entry()");

		XSSFWorkbook wk = new XSSFWorkbook(path);

		ObjectStore os = FilenetUtils.fetchObjectStore();

		Folder folder = Factory.Folder.fetchInstance(os, PropertyReader.getProperty("root.folder"), null);

		String tableName = PropertyReader.getProperty("root.folder.table");

		System.out.println(tableName);

		String accessMaskTable = PropertyReader.getProperty("root.access.table");

		XSSFTable accessTable = wk.getTable(accessMaskTable);

		HashMap<String, List<Object>> accessMaskMap = FilenetUtils.accessMaskTable(accessTable);

		XSSFTable table = wk.getTable(tableName);

		System.out.println("Starting Folder Creation");

		FilenetUtils.createFolders(folder, table, accessMaskMap);

		System.out.println("UtilityApplication.main.exit()");

	}

	public static void main1(String[] args) throws ClientProtocolException, IOException {

//		CloseableHttpClient client = HttpClients.createDefault();

//		HttpPost httpPost = new HttpPost("https://reqres.in/api/login");
//
//		JSONObject jobj = new JSONObject();
//
//		jobj.put("userId", userId);
//
//		StringEntity entity = new StringEntity(jobj.toString());
//
//		httpPost.setEntity(entity);
//
//		CloseableHttpResponse response = client.execute(httpPost);
//
//		String bodyAsString = EntityUtils.toString(response.getEntity());
//
//		System.out.println(bodyAsString);
//
//		client.close();

		try (CloseableHttpClient client = HttpClients.createDefault()) {

			HttpPost httpPost = new HttpPost("https://reqres.in/api/login");

			JSONObject jobj = new JSONObject();

			jobj.put("userId", "ABC");

			StringEntity entity = new StringEntity(jobj.toString());

			httpPost.setEntity(entity);

			try (CloseableHttpResponse response = client.execute(httpPost)) {

				String bodyAsString = EntityUtils.toString(response.getEntity());

				System.out.println(bodyAsString);

//				JSOresponseJson = JSONObject.parse(bodyAsString);

			}

		}

//		String t = "{\r\n"
//				+ "	\"Location\": [\r\n"
//				+ "		\"Navi Mumbai\",\r\n"
//				+ "		\"Mumbai\",\r\n"
//				+ "		\"Nashik\",\r\n"
//				+ "		\"Pune\"\r\n"
//				+ "	],\r\n"
//				+ "	\"Department\": [\r\n"
//				+ "		\"Credit Appraisal\",\r\n"
//				+ "		\"Marketing\"\r\n"
//				+ "	],\r\n"
//				+ "	\"DocumentType\": [\r\n"
//				+ "		{\r\n"
//				+ "			\"name\": \"Correspondence\",\r\n"
//				+ "			\"type\": [\r\n"
//				+ "				\"Regional Office\",\r\n"
//				+ "				\"Inter Department\",\r\n"
//				+ "				\"Instruction\"\r\n"
//				+ "			]\r\n"
//				+ "		}\r\n"
//				+ "	]\r\n"
//				+ "}";
//		
//		JSONObject jobj = new JSONObject(t);
//		
////		System.out.println(jobj.toString());
//		
//		JSONArray tt = jobj.getJSONArray("Location");
//		
//		List<String> location = new ArrayList<String>(tt.length());
//		
//		tt.forEach(i -> {
//			location.add((String) i);
//		});
//		
//		System.out.println(location);

	}

}
