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

import java.util.Collections;
import java.util.Vector;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class QuestionsRunner extends Activity {
	
	protected QuestionsRunner instance; 
	
	private int originalTextColor;
	private RadioGroup radioGroup;
	private TextView txtQuestao;
	private Byte correctop = 0;
	private Vector<RadioButton> radiobtns;
	private Vector<Byte> choosedsAnswers, correctsAnswers;
	private static Vector<Test> tests = null;
	private int questindex;
	private Vector<QuestionID> questions;
	private Button btnGo, btnBack, btnCheck, btnFinish;
	public static final String[] optionsLabel = {"A) ", "B) ", "C) ", "D) ", "E) ", "F) "};
	
	public static Vector<QuestionID> shuffleTests(Vector<Test> ltests){
		Vector<QuestionID> qs = new Vector<QuestionID>();
		for(int t=0; t<ltests.size(); t++){
			Test test = ltests.get(t);
			for(int i=1; i<test.getQuestionsNum(); i++){
				qs.add(new QuestionID(t, i));
			}
		}
		Collections.shuffle(qs);
		return qs;
	}
	
	private void intializeWTests(){
		this.questions = shuffleTests(QuestionsRunner.tests);
		this.choosedsAnswers = new Vector<Byte>();
		this.correctsAnswers = new Vector<Byte>();
		for(int i = 0; i <=  this.questions.size(); i++){
			this.choosedsAnswers.add((byte) 255);
			this.correctsAnswers.add((byte) 255);
		}
		initialize();
		this.questindex = 0;
		QuestionID actquest = this.questions.get(this.questindex);
		this.setQuestionData(tests.get(actquest.test).getQuestion(actquest.id));
	}
	
	private void intializeWOTests(){
		this.choosedsAnswers = null;
		this.correctsAnswers = null;
		this.questions = null;
		this.questindex = 0;
		initialize();
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		instance = this;
		
		super.onCreate(savedInstanceState);
        setContentView(R.layout.questionsrunner);
        
        if(tests == null)
			intializeWOTests();
		else
			intializeWTests();
	}
	
	/**
	 * Create the frame.
	 */
	public void initialize() {
		
		txtQuestao = (TextView) findViewById(R.id.textView1);
		btnBack = (Button) findViewById(R.id.button1);
		btnCheck = (Button) findViewById(R.id.button2);
		btnFinish = (Button) findViewById(R.id.button3);
		btnGo = (Button) findViewById(R.id.button4);
		radioGroup = (RadioGroup) findViewById(R.id.radioGroup1);
		radiobtns = new Vector<RadioButton>();
		RadioButton rbtn0 = (RadioButton) findViewById(R.id.radioButton1);
		radiobtns.add(rbtn0);
		radiobtns.add((RadioButton) findViewById(R.id.radioButton2));
		radiobtns.add((RadioButton) findViewById(R.id.radioButton3));
		radiobtns.add((RadioButton) findViewById(R.id.radioButton4));
		radiobtns.add((RadioButton) findViewById(R.id.radioButton5));
		radiobtns.add((RadioButton) findViewById(R.id.radioButton6));
		originalTextColor = rbtn0.getCurrentTextColor();
		
		btnBack.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				checkOptionStatus();
				questindex -= 1;
				QuestionID actquest = questions.get(questindex);
				setQuestionData(tests.get(actquest.test).getQuestion(actquest.id));
			}
		});
		
		btnGo.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				checkOptionStatus();
				questindex += 1;
				QuestionID actquest = questions.get(questindex);
				setQuestionData(tests.get(actquest.test).getQuestion(actquest.id));
			}
		});
			
		btnCheck.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				checkOptionStatus();
				RadioButton rightbtn = radiobtns.get(correctop-1);
				rightbtn.setTextColor(Color.GREEN);
			}
		});
		
		btnFinish.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				checkOptionStatus();
				Statistics.insert(correctsAnswers, choosedsAnswers, instance);
				Main.goToAct(Statistics.class, instance);
			}
		});
			
		onChangeQuestion();
	}
	
	private void checkOptionStatus(){
		if(this.choosedsAnswers == null)
			return;
		byte choosed = (byte)255;
		for(byte r=0; r<6; r++)
			if(this.radiobtns.get(r).isChecked()){
				choosed = r;
				break;
			}
		this.choosedsAnswers.set(questindex, choosed);
		this.correctsAnswers.set(questindex, correctop);
	}
	
	public void beforeChangeQuestion(){
		if(correctop <= 0) return;
		RadioButton rightbtn = radiobtns.get(correctop-1);
		rightbtn.setTextColor(originalTextColor);
		radioGroup.check(-1); //old radioGroup.clearSelection();
	}
	
	public void onChangeQuestion(){
		if(QuestionsRunner.tests == null){
			this.btnBack.setEnabled(false);
			this.btnGo.setEnabled(false);
		}else{
			this.btnBack.setEnabled((this.questindex > 0));
			this.btnGo.setEnabled((this.questindex < (this.questions.size() - 1)));
		}
		if(txtQuestao != null){
			int choosedop = this.choosedsAnswers.get(questindex);
			if(choosedop < 6 & choosedop >= 0)
				this.radiobtns.get(choosedop).setChecked(true);
		}
	}
	
	public void setQuestionData(String source, String question, Vector<String> options, byte lcorrectop){
		beforeChangeQuestion();
		txtQuestao.setText("("+source+") "+question);
		int optsnum = options.size();
		for(byte i=0; i<radiobtns.size(); i++){
			if(i<optsnum){
				radiobtns.get(i).setText(optionsLabel[i] + options.elementAt(i));
				radiobtns.get(i).setVisibility(View.GONE);
			}else{
				radiobtns.get(i).setText("");
				radiobtns.get(i).setVisibility(View.GONE);
			}
		}
		this.correctop = lcorrectop;
		onChangeQuestion();
	}
	
	public void setQuestionData(Question q){
		if(q == null){
			Vector<String> opts = new Vector<String>();
			opts.add("Ok!");
			try{
				QuestionID actquest = questions.get(questindex);
				setQuestionData("Erro", "Questão corrompida! ID" + actquest.id + "TEST" + actquest.test, opts, (byte) 0);
			}catch(Exception e){
				setQuestionData("Erro", "Questão corrompida! Possível erro na data do aplicativo. Por favor, limpe-a.", opts, (byte) 0);
			}
		}else{
			beforeChangeQuestion();
			txtQuestao.setText(q.getProblemText());
			Vector<String> as = q.getAnswers();
			int optsnum = as.size();
			for(byte i=0; i<radiobtns.size(); i++){
				if(i<optsnum){
					String puretext = optionsLabel[i] + as.elementAt(i);
					radiobtns.get(i).setText(puretext);
					radiobtns.get(i).setVisibility(View.VISIBLE);
				}else{
					radiobtns.get(i).setText("");
					radiobtns.get(i).setVisibility(View.GONE);
					
				}
			}
			this.correctop = q.getRightAnswerId();
			onChangeQuestion();
		}
	}
	
	public static void doTests(Vector<String> testsSNames, Activity father){
		Vector<Test> ltests = new Vector<Test>();
		for(String testSName :testsSNames){
			try {
				Test test = new LighterTest(testSName);
				ltests.add(test);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		tests = ltests;
		Main.goToAct(QuestionsRunner.class, father);
	}
	
	@Override 
	public void onDestroy(){
		if(tests != null)
			for(Test test : tests)
				test.disposeSQLConn();
		System.out.println("Conexões com as provas fechadas.");
		super.onDestroy();
	}
}
