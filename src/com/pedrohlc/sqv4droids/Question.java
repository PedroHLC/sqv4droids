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

import android.database.Cursor;
import android.database.SQLException;

public class Question {
	private String problemtext,
		answer1text,answer2text,answer3text,answer4text,answer5text,answer6text;
	private int id;
	private byte rightanswer, course;
	
	public String getProblemText(){ return problemtext;	}
	public Vector<String> getAnswers(){
		Vector<String> result = new Vector<String>();
		for(int i=1; i<7; i++){
			String ta = searchAnswerText(i);
			if(ta == null)
				break;
			else
				result.add(ta);
		}
		return result;
	}
	public String getAnswerText(int id){
		return searchAnswerText(id);
	}
	private String searchAnswerText(int id){
		switch(id){
			case 1: return answer1text;
			case 2: return answer2text;
			case 3: return answer3text;
			case 4: return answer4text;
			case 5: return answer5text;
			case 6: return answer6text;
			default: return null; 
		}
	}
	public Byte getRightAnswerId(){ return rightanswer;	}
	public Byte getCourseId(){ return course;	}
	public Integer getID(){ return id;	}
	
	public Question(Cursor rs) throws SQLException{
		id = rs.getInt(0);
		problemtext = Main.unescapeString(rs.getString(1));
		answer1text = Main.unescapeString(rs.getString(2));
		answer2text = Main.unescapeString(rs.getString(3));
		answer3text = Main.unescapeString(rs.getString(4));
		answer4text = Main.unescapeString(rs.getString(5));
		answer5text = Main.unescapeString(rs.getString(6));
		answer6text = Main.unescapeString(rs.getString(7));
		//if(answer1text.compareToIgnoreCase("null") == 0) answer1text = null;
		//if(answer2text.compareToIgnoreCase("null") == 0) answer2text = null;
		if(answer3text.compareToIgnoreCase("null") == 0) answer3text = null;
		if(answer4text.compareToIgnoreCase("null") == 0) answer4text = null;
		if(answer5text.compareToIgnoreCase("null") == 0) answer5text = null;
		if(answer6text.compareToIgnoreCase("null") == 0) answer6text = null;
		rightanswer = Byte.parseByte(rs.getString(8));
		course = Byte.parseByte(rs.getString(9));
	}
}
