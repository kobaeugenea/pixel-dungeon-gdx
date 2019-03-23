package com.watabou.utils;

import com.badlogic.gdx.Gdx;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class WebGLFileInputStream extends InputStream {

    private InputStream stream;

    public WebGLFileInputStream(String fileName) {
        stream = new ByteArrayInputStream(Gdx.app.getPreferences("com.watabou.pixeldungeon").getString(fileName).getBytes());
    }

    @Override
    public int read() throws IOException {
        return stream.read();
    }

    @Override
    public int available() throws IOException {
        return stream.available();
    }

    @Override
    public void close() throws IOException {
        stream.close();
    }
}
