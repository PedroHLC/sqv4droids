/**
    Copyright (C) 2012 Pedro Henrique Lara Campos, Felipe Rodrigues Varjão

    This file is part of SQV.

    SQV is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    any later version.

    SQV is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with SQV.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.pedrohlc.sqv4droids;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

public class Main {

	protected static final String webSQVNormalAddr = "http://dl.dropbox.com/u/44637513/sqv/";
	protected static final String webSQVBackupAddr = "http://www.pedrohlc.com/sqv/";
	protected static String webSQVAddr = webSQVNormalAddr;
	protected static final String webSQVOfflineAddr =  "http://0.0.0.0/";
	protected static final String webSQVMinVersion = "minv.txt";
	protected static final String localSQVMinVersion = "minv.txt";
	protected static final String sqlConnector = "org.sqlite.JDBC";
	protected static final String sqlSufix = ".dat";
	protected static final String sqlMainDB = "maindb";
	protected static final String sqlConfigTable = "config";
	protected static final String sqlTestsListDBWeb = "testslistdb.dat";
	protected static final String sqlTestsListDB = "testslistdb";
	protected static final String sqlHistoryDB = "historydb";
	protected static final String sqlTestsDBFolderWeb = "tests/";
	protected static final String sqlTestsDBFolder = "tests_";
	protected static final String sqlTestsQuestionsTable = "questoes";
	protected static final String sqlTestsTable = "list";
	protected static final String sqlHistoryTable = "history";
	protected static final String dwlSuffix = ".part";
	protected static final String title = "SQV";
	protected static final String titleError = "SQV - Erro";
	protected static final String titleWarning = "SQV - Aviso";
	protected static String dataFolder = Environment.getDataDirectory().getPath() + "/";
	protected static final int version = 1;
	
	
	public static Runnable checkVersionAndConnection = new Runnable(){
		public void run(){
			try {
				System.out.println("Tentando link: " + (webSQVNormalAddr + webSQVMinVersion));
				FileDownloader.instantDownloadFile(webSQVNormalAddr + webSQVMinVersion, dataFolder + localSQVMinVersion); //*WEB
				Main.webSQVAddr = webSQVNormalAddr;
			} catch (Exception e) {
				//e.printStackTrace();
				try{
					System.out.println("Tentando link reserva: " + (webSQVBackupAddr + webSQVMinVersion));
					FileDownloader.instantDownloadFile(webSQVBackupAddr + webSQVMinVersion, dataFolder + localSQVMinVersion); //*WEB
					Main.webSQVAddr = webSQVBackupAddr;
				} catch (Exception e2) {
					System.out.println("Vai ser offline mesmo :P\n -Log complementar: ");
					e2.printStackTrace();
					Main.webSQVAddr = webSQVOfflineAddr;
				}
			}
			String minversion = fileToString(dataFolder + localSQVMinVersion);
			if(minversion != null)
				if(new Integer(minversion) > version){
					showMessage("É necessário atualizar o aplicativo para novas provas.", "Aviso", new Activity());
					Main.webSQVAddr = webSQVOfflineAddr;
			}
		}
	};
	
	public static String fileToString(String file) {
        String result = null;
        DataInputStream in = null;

        try {
            File f = new File(file);
            byte[] buffer = new byte[(int) f.length()];
            in = new DataInputStream(new FileInputStream(f));
            in.readFully(buffer);
            result = new String(buffer);
        } catch (IOException e) {
        } finally {
            try {
            	if(in != null)
            		in.close();
            } catch (IOException e) {}
        }
        
        return result;
    }
	
	public static void showMessage(String content, String title, Context father){
		System.out.println(title + ": " + content);
		//Looper.prepare();
		new AlertDialog.Builder(father)
        	.setTitle(title)
        	.setPositiveButton(android.R.string.ok, null)
        	.setMessage(content)
        .create().show();
	}
	
	@Deprecated
	public static String getDBSQLPath(String db){	return getDBFilePath(db); }
	public static String getDBFilePath(String db){	return dataFolder + db + sqlSufix; }
	
	
	protected static SQLiteDatabase createSQLConn(String db){
		try {
			return SQLiteDatabase.openDatabase(getDBFilePath(db), null, SQLiteDatabase.OPEN_READWRITE + SQLiteDatabase.CREATE_IF_NECESSARY);
		} catch (SQLException e) {
			System.out.println("Erro: Database \"" + db + "\" corrompida.");
			e.printStackTrace();
		}
		return null;
	}
	
	private static void createDataFolder() {
		File dataFolderFile = new File(dataFolder);
		if(!dataFolderFile.exists())
			dataFolderFile.mkdir();
	}
	
	private static boolean copyFile(String sourcePath, String newPath, boolean sourceDelete){
		File fromF = new File(sourcePath);
		File toF = new File(newPath);
		FileInputStream from = null;
	    FileOutputStream to = null;
	    try {
	    	from = new FileInputStream(fromF);
	    	to = new FileOutputStream(toF);
	    	byte[] buffer = new byte[4096];
	    	int bytesRead;
	    	while ((bytesRead = from.read(buffer)) != -1)
		    	to.write(buffer, 0, bytesRead); // write
	    } catch (IOException e) {
			return false;
		} finally {
	    	if (from != null){
	    		try {
			    	from.close();
			    } catch (IOException e) {
			    	return false;
			    } finally{
			    	if(sourceDelete)
			    		fromF.delete();
			    }
	    	}
	    	if (to != null)
	    		try {
	    			to.close();
	    		} catch (IOException e) {
	    			return false;
	    		}
	    }
	    return true;
	}
	
	public static boolean copyFile(String sourcePath, String newPath){
		return copyFile(sourcePath, newPath, false);
	}
	
	public static boolean moveFileForced(String sourcePath, String newPath){
		return copyFile(sourcePath, newPath, true);
	}
	
	public static String getHomePath(){
		String hp = System.getProperty("user.home");
		return ((hp == null | hp.length() < 1) ? "." : hp);
	}
	
	public static String unescapeString(String string) {
		return string.replace("\\n", "\n").replace("\\34", "\"").replace("\\t", "\t");
	}
	
	public static String getNowTime(String format) {
		Calendar currentDate = Calendar.getInstance();
		SimpleDateFormat formatter= 
				new SimpleDateFormat(format);
		String dateNow = formatter.format(currentDate.getTime());
		return dateNow;
	}
	
	public static String getTodayDate() {
		return getNowTime("dd-MM-yy");
	}
	
	
	/**
	 * @param args
	 */
	public static void main() {
		createDataFolder();
		new Thread(checkVersionAndConnection).start();
	}
	
	public static void goToAct(Class<?> to, Activity from){
    	Intent intent = new Intent(from, to).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    	from.finish();
    	from.startActivity(intent);
    }
}
