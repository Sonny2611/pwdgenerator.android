/*
 * Copyright (c) 2010-2012, Petr Panteleyev
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *    2. Redistributions in binary form must reproduce the above copyright notice,
 *       this list of conditions and the following disclaimer in the documentation
 *       and/or other materials provided with the distribution.
 *    3. The name of the author may not be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
 * AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE AUTHOR
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN
 * IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.panteleyev.passwdgen;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.ClipboardManager;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;


public class PasswdGen extends Activity {
	
	private final Generator generator = new Generator();
	private Button pinButton;
	private Button unixButton;
	private Button genButton;
	private Button copyButton;
	private CheckBox upperCaseCheck;
	private CheckBox lowerCaseCheck;
	private CheckBox digitsCheck;
	private CheckBox symbolsCheck;
	private EditText passwdText;
	private Spinner lenSpinner;
	private ArrayAdapter<Integer> adapter;
		
	private static final int[] lenArray = {4, 6, 8, 10, 12, 14, 16, 20, 24, 32};
	private static final int PIN_POSITION = 0;
	private static final int UNIX_POSITION = 2;
	private static final int INIT_POSITION = UNIX_POSITION;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        pinButton = (Button)findViewById(R.id.PinButton);
        unixButton = (Button)findViewById(R.id.UnixButton);
        genButton = (Button)findViewById(R.id.GenButton);
        copyButton = (Button)findViewById(R.id.CopyButton);
        passwdText = (EditText)findViewById(R.id.passwdText);
        upperCaseCheck = (CheckBox)findViewById(R.id.UpperCaseCheck);
        lowerCaseCheck = (CheckBox)findViewById(R.id.LowerCaseCheck);
        digitsCheck = (CheckBox)findViewById(R.id.DigitsCheck);
        symbolsCheck = (CheckBox)findViewById(R.id.SymbolsCheck);
        lenSpinner = (Spinner)findViewById(R.id.LenSpinner);
                
        adapter = new ArrayAdapter<Integer>(this.getApplicationContext(), android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);                
        for (int l : lenArray) {
        	adapter.add(l);
        }
        lenSpinner.setAdapter(adapter);
		lenSpinner.setSelection(INIT_POSITION);
        
        pinButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				digitsCheck.setChecked(true);
				lowerCaseCheck.setChecked(false);
				upperCaseCheck.setChecked(false);
				symbolsCheck.setChecked(false);
				lenSpinner.setSelection(PIN_POSITION);
				generate();
			}        	
        });
        
        unixButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				digitsCheck.setChecked(true);
				lowerCaseCheck.setChecked(true);
				upperCaseCheck.setChecked(true);
				symbolsCheck.setChecked(false);
				lenSpinner.setSelection(UNIX_POSITION);
				generate();
			}        	
        });

        genButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				generate();
			}        	
        });
        
        copyButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
			    onCopy();
			}        	
        });
        
        registerForContextMenu(passwdText);
    }
    
    private void generate() {
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    	boolean avoid = prefs.getBoolean("options_avoid_ambiguous_letters", false);
    	
		generator.setDigits(digitsCheck.isChecked());
		generator.setLowerCase(lowerCaseCheck.isChecked());
		generator.setUpperCase(upperCaseCheck.isChecked());
		generator.setSymbols(symbolsCheck.isChecked());

		int index = lenSpinner.getSelectedItemPosition();
		int len = lenArray[index];
			
		String p = generator.generate(len, avoid);
		passwdText.setText(p);
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
		return true;
	}

	
	@Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {	    
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }

    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		    case R.id.main_menu_about:
				onAbout();
				return true;

		    case R.id.main_menu_copy:
		        onCopy();
		        return true;
		        
		    case R.id.main_menu_clear:
			    onClear();
				return true;
				
		    case R.id.main_menu_options:
		    	startActivity(new Intent(this, EditPreferences.class));
		    	return true;
				
			default:
				return true;
		}
	}
    
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.context_menu_copy:
                onCopy();
                return true;
              
            case R.id.contect_menu_clear:
                onClear();
                return true;
                
            default:
                return super.onContextItemSelected(item);
        }
    }
    
    private void onCopy() {
        String text = passwdText.getText().toString();
        ClipboardManager cm = (ClipboardManager)getApplication().getSystemService(CLIPBOARD_SERVICE);
        cm.setText(text);        
    }
    
    private void onClear() {
        passwdText.setText("");
    }
    
    private void onAbout() {
    	Context mContext = this.getApplicationContext();
    	String version = mContext.getString(R.string.version);
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("About");
        dialog.setMessage("(c) 2010-2012 Petr Panteleyev\n\nBuild: " + version);
        dialog.setNeutralButton("Close", null);
        dialog.create().show();    	
    }
    
}