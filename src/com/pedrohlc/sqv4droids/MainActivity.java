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

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {
	protected static MainActivity instance;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {   	
    	instance = this;
    	
    	String sdcard = Environment.getExternalStorageDirectory().getPath() + "/";
    	
    	if(new File(sdcard).exists())
    		Main.dataFolder = sdcard+".sqv/";
    	else
    		Main.dataFolder = this.getFilesDir().getPath() + "/";
    	System.out.println("Data Folder: " + Main.dataFolder);
    	Main.main();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        ((Button) findViewById(R.id.button1)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Main.goToAct(ChooseTest.class, instance);
			}
		});
        ((Button) findViewById(R.id.button2)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Main.goToAct(Statistics.class, instance);
			}
		});
        ((Button) findViewById(R.id.button3)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
    }
}