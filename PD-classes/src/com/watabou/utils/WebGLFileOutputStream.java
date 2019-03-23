package com.watabou.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class WebGLFileOutputStream extends OutputStream {

    private String fileName;
    private OutputStream stream;

    public WebGLFileOutputStream(String fileName) {
        this.fileName = fileName;
        this.stream = new ByteArrayOutputStream();
    }

    @Override
    public void write(int oneByte) throws IOException {
        stream.write(oneByte);
    }

    @Override
    public void close() throws IOException {
        stream.close();
        Preferences preferences = Gdx.app.getPreferences("com.watabou.pixeldungeon");
        preferences.putString(fileName, stream.toString());
        preferences.flush();
    }
}
