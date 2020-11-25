package com.wasidnp.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

//
//
//  Created by Parbir Kaur,
//
public class SaveData {

	private Context _context;
	private SharedPreferences shared;
	private String SHARED_NAME = "TA";
	private Editor edit;

	public SaveData(Context c) {
		_context = c;
		shared = _context.getSharedPreferences(SHARED_NAME,
				Context.MODE_PRIVATE);
		edit = shared.edit();
	}

	// ============================================//
	public void save(String key, String value) {
		edit.putString(key, value);
		edit.commit();
	}

	// ============================================//
	public String get(String key) {
		return shared.getString(key, key);

	}

	// ============================================//
	public boolean isExist(String key) {
		return shared.contains(key);

	}

	// ============================================//
	public void remove(String key) {

		edit.remove(key);
		edit.commit();

	}
//================================================================//
	public Object getString(String keyGurudwaraId) {
		// TODO Auto-generated method stub
		return null;
	}
	//================================================================//

}
