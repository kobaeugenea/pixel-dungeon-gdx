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
package com.watabou.noosa;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

import com.watabou.glscripts.Script;
import com.watabou.glwrap.Attribute;
import com.watabou.glwrap.Quad;
import com.watabou.glwrap.Uniform;

public class NoosaScript extends Script {
	
	public Uniform uCamera;
	public Uniform uModel;
	public Uniform uTex;
	public Uniform uColorM;
	public Uniform uColorA;
	public Attribute aXY;
	public Attribute aUV;

	private int verticesBuffer;
	private int indicesBuffer;

	private Camera lastCamera;
	
	public NoosaScript() {
		
		super();
		compile( shader() );
		
		uCamera	= uniform( "uCamera" );
		uModel	= uniform( "uModel" );
		uTex	= uniform( "uTex" );
		uColorM	= uniform( "uColorM" );
		uColorA	= uniform( "uColorA" );
		aXY		= attribute( "aXYZW" );
		aUV		= attribute( "aUV" );

        verticesBuffer = Gdx.gl.glGenBuffer();
        indicesBuffer = Gdx.gl.glGenBuffer();
	}
	
	@Override
	public void use() {
		
		super.use();
		
		aXY.enable();
		aUV.enable();
		
	}

	public void drawElements( FloatBuffer vertices, ShortBuffer indices, int size ) {
		vertices.position(0);
		Gdx.gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, verticesBuffer);
		Gdx.gl.glBufferData(GL20.GL_ARRAY_BUFFER, vertices.capacity() * Float.SIZE / 8, vertices, GL20.GL_STATIC_DRAW);

		aXY.vertexPointer( 2, 4, 0 );
		aUV.vertexPointer( 2, 4, 8 );


		Gdx.gl.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer);
		Gdx.gl.glBufferData(GL20.GL_ELEMENT_ARRAY_BUFFER, indices.capacity() * Short.SIZE / 8, indices, GL20.GL_STATIC_DRAW);
		Gdx.gl.glDrawElements( GL20.GL_TRIANGLES, size, GL20.GL_UNSIGNED_SHORT,  0);
	}
	
	public void drawQuad( FloatBuffer vertices ) {
        drawElements(vertices, Quad.INDICES_1, Quad.SIZE);
	}
	
	public void drawQuadSet( FloatBuffer vertices, int size ) {
		if (size == 0) {
			return;
		}
		drawElements(vertices, Quad.getIndices(size), Quad.SIZE * size);
	}
	
	public void lighting( float rm, float gm, float bm, float am, float ra, float ga, float ba, float aa ) {
		uColorM.value4f( rm, gm, bm, am );
		uColorA.value4f( ra, ga, ba, aa );
	}
	
	public void resetCamera() {
		lastCamera = null;
	}
	
	public void camera( Camera camera ) {
		if (camera == null) {
			camera = Camera.main;
		}
		if (camera != lastCamera) {
			lastCamera = camera;
			uCamera.valueM4( camera.matrix );
			
			Gdx.gl.glScissor(
				camera.x, 
				Game.height - camera.screenHeight - camera.y, 
				camera.screenWidth, 
				camera.screenHeight );
		}
	}
	
	public static NoosaScript get() {
		return Script.use( NoosaScript.class );
	}
	
	
	protected String shader() {
		return SHADER;
	}
	
	private static final String SHADER =
		
		"uniform mat4 uCamera;" +
		"uniform mat4 uModel;" +
		"attribute vec4 aXYZW;" +
		"attribute vec2 aUV;" +
		"varying vec2 vUV;" +
		"void main() {" +
		"  gl_Position = uCamera * uModel * aXYZW;" +
		"  vUV = aUV;" +
		"}" +
		
		"//\n" +
		"#ifdef GL_ES\n" +
		"precision mediump float;\n" +
		"#endif\n" +
		"varying vec2 vUV;" +
		"uniform sampler2D uTex;" +
		"uniform vec4 uColorM;" +
		"uniform vec4 uColorA;" +
		"void main() {" +
		"  gl_FragColor = texture2D( uTex, vUV ) * uColorM + uColorA;" +
		"}";
}
