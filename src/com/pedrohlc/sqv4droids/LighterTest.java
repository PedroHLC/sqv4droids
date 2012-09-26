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

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class LighterTest implements Test {
	private SQLiteDatabase sqlConn;
	
	public LighterTest(String db) throws SQLException{
		sqlConn = Main.createSQLConn(Main.sqlTestsDBFolder + db);
	}
	
	public Question getQuestion(int id){
		Question returnv = null;
		try{
			Cursor result = sqlConn.query(Main.sqlTestsQuestionsTable, null, "id=?", new String[]{new Integer(id).toString()}, null, null, null); 
			while(result.moveToNext()){
				returnv = new Question(result);
			}
			result.close();
		} catch(Exception e) {}
		return returnv;
	}
	
	public void disposeSQLConn(){
		try {
			sqlConn.close();
		} catch (SQLException e) {}
	}

	public Integer getQuestionsNum() {
		Integer returnv = null;
		try{
			Cursor result = sqlConn.rawQuery("SELECT COUNT(*) AS `num` FROM `"+Main.sqlTestsQuestionsTable+"`;", null);
			while(result.moveToNext()){
				returnv = result.getInt(0);
			}
			result.close();
			return returnv;
		} catch(SQLException e) {return 0;}
	}
}
