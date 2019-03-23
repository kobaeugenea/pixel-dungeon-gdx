/*
 * Pixel Dungeon
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
package com.watabou.pixeldungeon.ui;

import com.badlogic.gdx.Input;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.watabou.input.PDInputProcessor;
import com.watabou.noosa.*;
import com.watabou.pixeldungeon.Chrome;
import com.watabou.pixeldungeon.scenes.PixelScene;
import com.watabou.utils.Signal;

public class Window extends Group implements Signal.Listener<PDInputProcessor.Key> {

	protected int width;
	protected int height;
	
	protected TouchArea blocker;
	protected NinePatch chrome;
	
	public static final int TITLE_COLOR = 0xFFFF44;
	
	public Window() {
		this( 0, 0, Chrome.get( Chrome.Type.WINDOW ) );
	}
	
	public Window( int width, int height ) {
		this( width, height, Chrome.get( Chrome.Type.WINDOW ) );
	}
			
	public Window( int width, int height, NinePatch chrome ) {
		super();
		
		blocker = new TouchArea( 0, 0, PixelScene.uiCamera.width, PixelScene.uiCamera.height ) {
			@Override
			protected void onClick( PDInputProcessor.Touch touch ) {
				if (!Window.this.chrome.overlapsScreenPoint( 
					(int)touch.current.x, 
					(int)touch.current.y )) {
					
					onBackPressed();
				}
			}
		};
		blocker.camera = PixelScene.uiCamera;
		add( blocker );
		
		this.chrome = chrome;
		
		this.width = width;
		this.height = height;
		
		chrome.x = -chrome.marginLeft();
		chrome.y = -chrome.marginTop();
		chrome.size( 
			width - chrome.x + chrome.marginRight(),
			height - chrome.y + chrome.marginBottom() );
		add( chrome );
		
		camera = new Camera( 0, 0, 
			(int)chrome.width, 
			(int)chrome.height, 
			PixelScene.defaultZoom );
		camera.x = (int)(Game.width - camera.width * camera.zoom) / 2;
		camera.y = (int)(Game.height - camera.height * camera.zoom) / 2;
		camera.scroll.set( chrome.x, chrome.y );
		Camera.add( camera );

		PDInputProcessor.eventKey.add( this );
	}
	
	public void resize( int w, int h ) {
		this.width = w;
		this.height = h;
		
		chrome.size( 
			width + chrome.marginHor(),
			height + chrome.marginVer() );
		
		camera.resize( (int)chrome.width, (int)chrome.height );
		camera.x = (int)(Game.width - camera.screenWidth()) / 2;
		camera.y = (int)(Game.height - camera.screenHeight()) / 2;
	}
	
	public void hide() {
		parent.erase( this );
		destroy();
	}
	
	@Override
	public void destroy() {
		super.destroy();
		
		Camera.remove( camera );
		PDInputProcessor.eventKey.remove( this );
	}

	@Override
	public void onSignal( PDInputProcessor.Key key ) {
		if (key.pressed) {
			switch (key.code) {
			case Input.Keys.BACK:
			case Input.Keys.ESCAPE:
				onBackPressed();
				break;
			case Input.Keys.MENU:
			case Input.Keys.F5:
				onMenuPressed();
				break;
			default:
				onKeyDown(key);
				break;
			}
		}

		PDInputProcessor.eventKey.cancel();
	}

	protected void onKeyDown(PDInputProcessor.Key key) {

	}

	public void onBackPressed() {
		hide();
	}
	
	public void onMenuPressed() {
	}
	
	protected static class Highlighter {

		private static final String HIGHLIGHTER_TEXT = "_(.*?)_";
		private static final RegExp HIGHLIGHTER	= RegExp.compile(HIGHLIGHTER_TEXT, "g");
		private static final String STRIPPER = "[ \n]";
		
		public String text;
		
		public boolean[] mask;
		
		public Highlighter( String text ) {

			String stripped = text.replaceAll(STRIPPER, "");
			mask = new boolean[stripped.length()];
			
			MatchResult m;

			int pos = 0;
			int lastMatch = 0;

			while((m = HIGHLIGHTER.exec(stripped)) != null) {
				pos += (m.getIndex() - lastMatch);
				int groupLen = m.getGroup( 1 ).length();
				for (int i=pos; i < pos + groupLen; i++) {
					mask[i] = true;
				}
				pos += groupLen;
				lastMatch = HIGHLIGHTER.getLastIndex();
			}
			
			this.text = text.replaceAll(HIGHLIGHTER_TEXT, "$1");
		}
		
		public boolean[] inverted() {
			boolean[] result = new boolean[mask.length];
			for (int i=0; i < result.length; i++) {
				result[i] = !mask[i];
			}
			return result;
		}
		
		public boolean isHighlighted() {
			for (int i=0; i < mask.length; i++) {
				if (mask[i]) {
					return true;
				}
			}
			return false;
		}
	}
}