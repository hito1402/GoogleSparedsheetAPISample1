package com.gclue.main;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.SpreadsheetFeed;
import com.google.gdata.util.ServiceException;

public class GoogleSpreadSheetAPISample {

	public static void main(String[] args) {
		// 認証情報の取得.
		GoogleCredential credential = getCredential();
		// スプレッドシートサービスを認証情報を使用して取得.
		SpreadsheetService service = getSpreadsheetService(credential);
		
		List<SpreadsheetEntry> sheetEntries = null;
		try {
			sheetEntries = getSpreadsheets(service);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ServiceException e) {
			e.printStackTrace();
		}

		if(sheetEntries != null) {
			System.out.println("取得したシート数: " + sheetEntries.size());
			
			System.out.println("---");
			System.out.println("取得したシート一覧");
			for (SpreadsheetEntry sheetEntry : sheetEntries) {
				System.out.println(sheetEntry.getTitle().getPlainText());
			}
			System.out.println("---");
		}

	}
	
	static List<SpreadsheetEntry> getSpreadsheets(SpreadsheetService service) throws IOException, ServiceException {
		URL spreadSheetFeedURL = new URL(
				"https://spreadsheets.google.com/feeds/spreadsheets/private/full");
		SpreadsheetFeed feed = service.getFeed(spreadSheetFeedURL, SpreadsheetFeed.class);
		List<SpreadsheetEntry> spreadsheets = feed.getEntries();
		
		return spreadsheets;
	}
	
	private static final String ServiceAccountMailAddress = "";
	
	static GoogleCredential getCredential() {
		File p12 = new File("./key.p12");

		HttpTransport httpTransport = new NetHttpTransport();
		JacksonFactory jsonFactory = new JacksonFactory();
		String[] SCOPESArray = { "https://spreadsheets.google.com/feeds" };
		final List<String> SCOPES = Arrays.asList(SCOPESArray);
		GoogleCredential credential = null;
		try {
			credential = new GoogleCredential.Builder()
					.setTransport(httpTransport).setJsonFactory(jsonFactory)
					.setServiceAccountScopes(SCOPES)
					.setServiceAccountPrivateKeyFromP12File(p12)
					.setServiceAccountId(ServiceAccountMailAddress).build();
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return credential;
	}
	
	static String ApplicationName = "";
	static SpreadsheetService spreadsheetService = null;
	
	static synchronized SpreadsheetService getSpreadsheetService(GoogleCredential credential) {
		if(spreadsheetService == null) {
			spreadsheetService = new SpreadsheetService(ApplicationName);
		}
		spreadsheetService.setOAuth2Credentials(credential);
		
		return  spreadsheetService;
	}
}
