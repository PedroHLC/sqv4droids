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

import java.util.Vector;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class Statistics extends Activity {

	/**
	 * 
	 */
	private static Activity instance;
	private static SQLiteDatabase conn;
	public static final String tableCreateQuery = "CREATE TABLE IF NOT EXISTS "+Main.sqlHistoryTable+"(date DATE NOT NULL,corrects INT NOT NULL,wrongs INT NOT NULL,emptys INT NOT NULL);";
	private TextView lblAcertos, lblErros, lblNoRespondidas;
	private Button btnClean, btnToTitle, btnQuit;
	private TableLayout eachRList;
	
	private static void connectSQL(){
		if(conn == null) 
			conn = Main.createSQLConn(Main.sqlHistoryDB);
		else if(!conn.isOpen())
			conn = Main.createSQLConn(Main.sqlHistoryDB);
		
		try {
			conn.execSQL(tableCreateQuery);
		} catch (SQLException e) {
			Main.showMessage(e.toString(), Main.titleError, instance);
			instance.finish();
		}
	}

	private void loadHistory(){
		try {
			Cursor result = conn.query(Main.sqlHistoryTable, null, null, null, null, null, null);
			showHistory(result);
			result.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Create the frame.
	 */
	private void inti(){
		btnClean = (Button) findViewById(R.id.button1);
		btnToTitle = (Button) findViewById(R.id.button2);
		btnQuit = (Button) findViewById(R.id.button3);
		eachRList = (TableLayout) findViewById(R.id.TableLayout1);
		lblAcertos = (TextView) findViewById(R.id.textView2);
		lblErros = (TextView) findViewById(R.id.textView3);
		lblNoRespondidas = (TextView) findViewById(R.id.textView4);
		
		btnClean.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				try {
					if(conn == null){
						connectSQL();
						conn.delete(Main.sqlHistoryTable, null, null);
						conn.close();
						conn = null;
					}else
						conn.delete(Main.sqlHistoryTable, null, null);
					Main.goToAct(Statistics.class, instance);
				} catch (SQLException e1) {
					e1.printStackTrace();
					runOnUiThread(new Runnable(){
						public void run() {
							Main.showMessage("Não foi possível limpar seu histórico.", Main.titleError, instance);
						}
					});
				}
			}
		});
		
		btnToTitle.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Main.goToAct(MainActivity.class, instance);
			}
		});
		
		btnQuit.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		instance = this;
		
		super.onCreate(savedInstanceState);
        setContentView(R.layout.statistics);
        
        inti();
		connectSQL();
		loadHistory();
		conn.close();
		conn = null;
	}
	
	public static void insert(Vector<Byte> correctsls, Vector<Byte> chooseds, Activity father) {	
		if(instance == null);
			instance = father;
		
		int len = correctsls.size(),
				corrects = 0,
				wrongs = 0,
				emptys = 0;
		
		byte value;
		for(int i=0; i<len; i++){
			value = chooseds.get(i);
			if(value < 6 & value >= 0){
				if(value == (correctsls.get(i) - 1))
					corrects++;
				else
					wrongs++;
			}else
				emptys++;
		}
		
		String date = Main.getTodayDate();
		
		connectSQL();
		
		try {
			ContentValues cv = new ContentValues();
			cv.put("date", date);
			cv.put("corrects", corrects);
			cv.put("wrongs", wrongs);
			cv.put("emptys", emptys);
			conn.insert(Main.sqlHistoryTable, null, cv);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void showHistory(Cursor rs){
		int corrects = 0, wrongs = 0, emptys = 0, total, realtotal;
		
		String indvDate;
		int indvCorrects, indvWrongs, indvEmptys;
		
		Context viewsFather = eachRList.getContext();
		
		TableRow leg_row = new TableRow(viewsFather);
		
		TextView leg_lbl = new TextView(viewsFather);
		leg_lbl.setText("Data\t\tCorretas\tErradas");
		leg_row.addView(leg_lbl);
		eachRList.addView(leg_row);
		
		if(rs != null)
			try {
				while(rs.moveToNext()){
					indvDate = rs.getString(0);
					indvCorrects = rs.getInt(1);
					indvWrongs = rs.getInt(2);
					indvEmptys = rs.getInt(3);
					
					TableRow row = new TableRow(viewsFather);
					
					TextView lbl = new TextView(viewsFather);
					lbl.setText(indvDate + "\t\t" + indvCorrects + "\t\t" + indvWrongs);
					row.addView(lbl);
					
					eachRList.addView(row);
					
					corrects += indvCorrects;
					wrongs += indvWrongs; 
					emptys += indvEmptys; 
				}
			} catch (SQLException e) {
				e.printStackTrace();
				Main.showMessage("Não foi possível ler o histórico de resultados", "SQV - ERRO", this);
			}
		
		total =  corrects + wrongs;
		realtotal = total + emptys;
		
		lblAcertos.setText("Acertou: "+corrects + "/" + total);
		
		lblErros.setText("Errou: "+wrongs + "/" + total);
		
		lblNoRespondidas.setText("Pulou: "+emptys + "/" + realtotal);
	}
}
