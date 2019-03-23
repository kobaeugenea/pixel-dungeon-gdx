/*
 * Copyright (C) 2012-2014  Oleg Dolya
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.watabou.utils;

import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.json.client.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.lang.Class;

public class Bundle {

	private static final String CLASS_NAME = "__className";

	private static HashMap<String,String> aliases = new HashMap<String, String>();

	private JSONObject data;

	public Bundle() {
		this( new JSONObject() );
	}

	private Bundle( JSONObject data ) {
		this.data = data;
	}

	public boolean isNull() {
		return data == null;
	}

	public boolean contains(String key) {
		return data.containsKey(key);
	}

	public boolean getBoolean(String key) {
		try {
			return data.get(key).isBoolean().booleanValue();
		} catch (Exception e){
			return false;
		}
	}

	public int getInt(String key) {
		try {
			return (int) data.get(key).isNumber().doubleValue();
		} catch (Exception e){
			return 0;
		}
	}

	public float getFloat(String key) {
		try {
			return (float) data.get(key).isNumber().doubleValue();
		} catch (Exception e){
			return (float) Double.NaN;
		}
	}

	public String getString(String key) {
		try {
			return data.get(key).isString().stringValue();
		} catch (Exception e){
			return "";
		}
	}

	public Bundle getBundle(String key) {
		try {
			return new Bundle(data.get(key).isObject());
		} catch (Exception e){
			return new Bundle(null);
		}
	}

	private Bundlable get() {
		try {
			String clName = getString( CLASS_NAME );
			if (aliases.containsKey( clName )) {
				clName = aliases.get( clName );
			}

			Class<?> cl = ClassReflection.forName( clName );
			if (cl != null) {
				Bundlable object = (Bundlable) ClassReflection.newInstance(cl);
				object.restoreFromBundle( this );
				return object;
			} else {
				return null;
			}
		} catch (Exception e) {
			e = null;
			return null;
		}
	}

	public Bundlable get( String key ) {
		return getBundle( key ).get();
	}

	public int[] getIntArray( String key ) {
		try {
			JSONArray array = data.get(key).isArray();
			int length = array.size();
			int[] result = new int[length];
			for (int i=0; i < length; i++) {
				JSONNumber n = array.get(i).isNumber();
				result[i] = (int) (n != null ? n.doubleValue() : Integer.valueOf(array.get(i).isString().stringValue()));
			}
			return result;
		} catch (Exception e) {
			return null;
		}
	}

	public boolean[] getBooleanArray( String key ) {
		try {
			JSONArray array = data.get(key).isArray();
			int length = array.size();
			boolean[] result = new boolean[length];
			for (int i=0; i < length; i++) {
				result[i] = array.get(i).isBoolean().booleanValue();
			}
			return result;
		} catch (Exception e) {
			return null;
		}
	}

	public String[] getStringArray( String key ) {
		try {
			JSONArray array = data.get(key).isArray();
			int length = array.size();
			String[] result = new String[length];
			for (int i=0; i < length; i++) {
				result[i] = array.get(i).isString().stringValue();
			}
			return result;
		} catch (Exception e) {
			return null;
		}
	}

	public Collection<Bundlable> getCollection( String key ) {

		ArrayList<Bundlable> list = new ArrayList<Bundlable>();

		try {
			JSONArray array = data.get(key).isArray();
			for (int i=0; i < array.size(); i++) {
				list.add(new Bundle(array.get(i).isObject().isObject()).get()); //double call to call Exception if it isn't JSONObject
			}
		} catch (Exception e) {
		}

		return list;
	}

	public void put( String key, boolean value ) {
		try {
			data.put(key, JSONBoolean.getInstance(value));
		} catch (Exception e) {
		}
	}

	public void put( String key, int value ) {
		put(key, (float) value);
	}

	public void put( String key, float value ) {
		try {
			data.put(key, new JSONNumber(value));
		} catch (Exception e) {
		}
	}

	public void put( String key, String value ) {
		try {
			data.put(key, new JSONString(value));
		} catch (Exception e) {
		}
	}

	public void put( String key, Bundle bundle ) {
		try {
			data.put( key, bundle.data );
		} catch (Exception e) {
		}
	}

	public void put( String key, Bundlable object ) {
		if (object != null) {
			try {
				Bundle bundle = new Bundle();
				bundle.put( CLASS_NAME, object.getClass().getName() );
				object.storeInBundle( bundle );
				data.put( key, bundle.data );
			} catch (Exception e) {
			}
		}
	}

	public void put( String key, int[] array ) {
		try {
			JSONArray jsonArray = new JSONArray();
			for (int i=0; i < array.length; i++) {
				jsonArray.set(i, new JSONNumber(array[i]));
			}
			data.put( key, jsonArray );
		} catch (Exception e) {

		}
	}

	public void put( String key, boolean[] array ) {
		try {
			JSONArray jsonArray = new JSONArray();
			for (int i=0; i < array.length; i++) {
				jsonArray.set(i, JSONBoolean.getInstance(array[i]));
			}
			data.put( key, jsonArray );
		} catch (Exception e) {

		}
	}

	public void put( String key, String[] array ) {
		try {
			JSONArray jsonArray = new JSONArray();
			for (int i=0; i < array.length; i++) {
				jsonArray.set(i, new JSONString(array[i]));
			}
			data.put( key, jsonArray );
		} catch (Exception e) {

		}
	}

	public void put( String key, Collection<? extends Bundlable> collection ) {
		JSONArray array = new JSONArray();
		int i = 0;
		for (Bundlable object : collection) {
			Bundle bundle = new Bundle();
			bundle.put( CLASS_NAME, object.getClass().getName() );
			object.storeInBundle( bundle );
			array.set(i, bundle.data);
			i++;
		}
		try {
			data.put( key, array );
		} catch (Exception e) {

		}
	}

	public static Bundle read( InputStream stream ) {

		try {
			BufferedReader reader = new BufferedReader( new InputStreamReader( stream ) );
			JSONObject json = new JSONObject(JsonUtils.safeEval(reader.readLine()));
			reader.close();

			return new Bundle( json );
		} catch (Exception e) {
			return null;
		}
	}

	public static boolean write( Bundle bundle, OutputStream stream ) {
		try {
			BufferedWriter writer = new BufferedWriter( new OutputStreamWriter( stream ) );
			writer.write( bundle.data.toString() );
			writer.close();

			return true;
		} catch (IOException e) {
			return false;
		}
	}

	public static void addAlias( Class<?> cl, String alias ) {
		aliases.put( alias, cl.getName() );
	}
}
