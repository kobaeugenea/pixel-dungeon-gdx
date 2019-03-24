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

import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.utils.reflect.ClassReflection;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class Bundle {

	private static final String CLASS_NAME = "__className";
	
	private static HashMap<String,String> aliases = new HashMap<String, String>();

	private static JsonReader jsonReader = new JsonReader();
	
	private JsonValue data;
	
	public Bundle() {
		this(new JsonValue(JsonValue.ValueType.object));
	}
	
	private Bundle( JsonValue data ) {
		this.data = data;
	}
	
	public boolean isNull() {
		return data == null;
	}
	
	public boolean contains( String key ) {
		return data.has(key);
	}
	
	public boolean getBoolean( String key ) {
		try {
			return data.getBoolean(key);
		} catch (Exception e){
			return false;
		}
	}
	
	public int getInt( String key ) {
		try {
			return data.getInt(key);
		} catch (Exception e){
			return 0;
		}
	}
	
	public float getFloat( String key ) {
		try {
			return data.getFloat(key);
		} catch (Exception e){
			return Float.NaN;
		}
	}
	
	public String getString( String key ) {
		try{
			return data.getString(key);
		} catch (Exception e){
			return "";
		}
	}

	public Bundle getBundle( String key ) {
		return new Bundle(data.get(key));
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
			return data.get(key).asIntArray();
		} catch (Exception e) {
			return null;
		}
	}
	
	public boolean[] getBooleanArray( String key ) {
		try {
			return data.get(key).asBooleanArray();
		} catch (Exception e) {
			return null;
		}
	}
	
	public String[] getStringArray( String key ) {
		try {
			return data.get(key).asStringArray();
		} catch (Exception e) {
			return null;
		}
	}
	
	public Collection<Bundlable> getCollection( String key ) {
		
		ArrayList<Bundlable> list = new ArrayList<Bundlable>();
		
		try {
			JsonValue array = data.get(key);
			for (int i=0; i < array.size; i++) {
				list.add(new Bundle(array.get(i)).get());
			}
		} catch (Exception e) { }
		
		return list;
	}
	
	public void put( String key, boolean value ) {
		data.addChild(key, new JsonValue(value));
	}
	
	public void put( String key, int value ) {
		data.addChild(key, new JsonValue(value));
	}
	
	public void put( String key, float value ) {
		data.addChild(key, new JsonValue(value));
	}
	
	public void put( String key, String value ) {
		data.addChild(key, new JsonValue(value));
	}
	
	public void put( String key, Bundle bundle ) {
		data.addChild(key, bundle.data);
	}
	
	public void put( String key, Bundlable object ) {
		if (object != null) {
			try {
				Bundle bundle = new Bundle();
				bundle.put( CLASS_NAME, object.getClass().getName() );
				object.storeInBundle( bundle );
				data.addChild(key, bundle.data);
			} catch (Exception e) { }
		}
	}
	
	public void put( String key, int[] array ) {
		try {
			JsonValue jsonArray = new JsonValue(JsonValue.ValueType.array);
			for (int i=0; i < array.length; i++) {
				jsonArray.addChild(new JsonValue(array[i]));
			}
			data.addChild(key, jsonArray);
		} catch (Exception e) { }
	}
	
	public void put( String key, boolean[] array ) {
		try {
			JsonValue jsonArray = new JsonValue(JsonValue.ValueType.array);
			for (int i=0; i < array.length; i++) {
				jsonArray.addChild(new JsonValue(array[i]));
			}
			data.addChild(key, jsonArray);
		} catch (Exception e) { }
	}
	
	public void put( String key, String[] array ) {
		try {
			JsonValue jsonArray = new JsonValue(JsonValue.ValueType.array);
			for (int i=0; i < array.length; i++) {
				jsonArray.addChild(new JsonValue(array[i]));
			}
			data.addChild(key, jsonArray);
		} catch (Exception e) { }
	}
	
	public void put( String key, Collection<? extends Bundlable> collection ) {
		JsonValue array = new JsonValue(JsonValue.ValueType.array);
		for (Bundlable object : collection) {
			Bundle bundle = new Bundle();
			bundle.put( CLASS_NAME, object.getClass().getName() );
			object.storeInBundle( bundle );
			array.addChild(bundle.data);
		}
		try {
			data.addChild(key, array);
		} catch (Exception e) {
			
		}
	}
	
	public static Bundle read( InputStream stream ) {
		
		try {
			BufferedReader reader = new BufferedReader( new InputStreamReader( stream ) );
			String jsonString = reader.readLine();
			reader.close();
			if(jsonString == null) return new Bundle(new JsonValue(JsonValue.ValueType.object));
			JsonValue json = jsonReader.parse(jsonString);
			return new Bundle( json );
		} catch (Exception e) {
			return null;
		}
	}
	
	public static boolean write( Bundle bundle, OutputStream stream ) {
		try {
			BufferedWriter writer = new BufferedWriter( new OutputStreamWriter( stream ) );	
			writer.write(bundle.data.toJson(JsonWriter.OutputType.json));
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
