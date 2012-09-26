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

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Vector;

import android.app.Activity;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TableLayout;
import android.widget.TableRow;

public class ChooseTest extends Activity{

	private SQLiteDatabase sqlConn;
	private Vector<String> testsSNames, choosedones;
	private Vector<CheckBox> testsChecks;
	private boolean tmpSucess;
	private int tmpTestIndex = 0,
			tmpTestsNum = 0;
	private static ChooseTest instance;
	private Button btnGo, btnBack;
	//private ProgressBar progressBar1, progressBar2;
	private TableLayout table;

	@Override
    public void onCreate(Bundle savedInstanceState) {
		instance = this;
		
		super.onCreate(savedInstanceState);
        setContentView(R.layout.choosetest);
		
        btnGo = ((Button) findViewById(R.id.button2)); 
        btnGo.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				startDownload();
			}
		});
        
        btnBack = ((Button) findViewById(R.id.button1));
        btnBack.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Main.goToAct(MainActivity.class, instance); 
			}
		});
        
        /*progressBar1 = ((ProgressBar) findViewById(R.id.progressBar1));
        progressBar2 = ((ProgressBar) findViewById(R.id.progressBar2));*/
        
        table = ((TableLayout) findViewById(R.id.TableLayout1));
        
		new Thread(){
			public void run(){
				prepareList();
				System.out.println("...");
			}
		}.start();
	}
	
	private TableRow tmpRow = null;	
	protected synchronized void prepareList(){
		byte createError = 0;
		try {
			FileDownloader.instantDownloadFile(Main.webSQVAddr + Main.sqlTestsListDBWeb, Main.getDBFilePath(Main.sqlTestsListDB)); //*WEB
		} catch (Exception e) {
			createError = 1;
			e.printStackTrace();
		}
		
		testsSNames = new Vector<String>();
		testsChecks = new Vector<CheckBox>();
		
		sqlConn = null;
		sqlConn = Main.createSQLConn(Main.sqlTestsListDB);
		if(sqlConn != null)
			try {
				Cursor result = sqlConn.query(Main.sqlTestsTable, new String[]{"name", "namespace"}, null, null, null, null, null);
				if (result.getCount() <= 0){
					Main.showMessage("Lista de provas indisponível, por favor, conecte o computador a internet ao menos uma vez.", Main.titleWarning, this);
					return;
				}
				while(result.moveToNext()){
					tmpRow = new TableRow(table.getContext());
					CheckBox cb = new CheckBox(tmpRow.getContext());
					cb.setText(result.getString(0));
					testsChecks.add(cb);
					tmpRow.addView(cb);
					runOnUiThread(new Runnable(){
						public void run() {
							table.addView(tmpRow);
							tmpRow = null;
						}
					});
					testsSNames.add(result.getString(1));
					while(tmpRow != null){}
				}
				result.close();
			} catch (SQLException e) {
				createError = 2;
				e.printStackTrace();
			}
		
		if(sqlConn != null)
			sqlConn.close();
		
		if(createError == 1)
			runOnUiThread(new Runnable(){
				public void run() {
					Main.showMessage("Não foi possível se conectar a internet ou nosso website não está disponível.", Main.titleError, instance);
				}
			});
		else if(createError == 2)
			runOnUiThread(new Runnable(){
				public void run() {
					Main.showMessage("Lista de provas indisponível, por favor, conecte o computador a internet ao menos uma vez.", Main.titleWarning, instance);
				}
			});
	}
	
	private void startDownload(){
		this.choosedones = new Vector<String>();
		for(int i = 0; i < testsSNames.size(); i++)
			if(testsChecks.get(i).isChecked())
				choosedones.add(testsSNames.get(i));
		tmpTestsNum = choosedones.size();
		if(tmpTestsNum < 1){
			Main.showMessage("Selecione pelo menos uma prova.", Main.title, this);
			return;
		}
		btnGo.setEnabled(false);
		table.setEnabled(false);
		btnGo.setText("Baixando...");
		/*progressBar1.setProgress(0);
		progressBar1.setMax(100);
		progressBar2.setProgress(0);
		progressBar2.setMax(tmpTestsNum * 100);*/
		tmpSucess = true;
		new Thread(){
			public void run(){
				boolean breaked;
				for(tmpTestIndex = 0; tmpTestIndex < tmpTestsNum; tmpTestIndex++){
					String testSName = choosedones.get(tmpTestIndex) + Main.sqlSufix;
					String foutpath = Main.dataFolder + Main.sqlTestsDBFolder + testSName;
					String inpath = Main.webSQVAddr + Main.sqlTestsDBFolderWeb + testSName; //WEB*
					if(!new File(foutpath).exists()){
						String errorMessage = "Erro ao tentar baixar uma prova.";
						FileDownloader downloader = new FileDownloader(inpath, foutpath); //WEB*
						try {
							downloader.start();
							breaked = false;
							while(!breaked){
								breaked = downloader.tick();
								/*Integer percent = new Double(downloader.getPosition().doubleValue() / downloader.getSize().doubleValue() * 100).intValue();
								progressBar1.setProgress(percent);
								progressBar2.setProgress((tmpTestIndex * 100) + percent);*/	
							}
							tmpSucess = true;
						} catch (MalformedURLException e) {
							Main.showMessage(errorMessage, Main.titleError, instance);
							e.printStackTrace();
						} catch (IOException e) {
							Main.showMessage(errorMessage, Main.titleError, instance);
							e.printStackTrace();
						}
					}
				}
				//progressBar1.setValue((tmpTestIndex * 100) + 100);  TODO Re-make this code
				finishDownload();
			}
		}.start();
	}
	
	private void finishDownload(){
		if(tmpSucess){
			QuestionsRunner.doTests(choosedones, this);
		}else{
			btnGo.setEnabled(true);
			table.setEnabled(true);
			btnGo.setText("Ir...");
		}
	}
}