package com.xfsi.swipe_demo1.contents;

import android.content.res.AssetManager;
import android.renderscript.ScriptGroup;
import android.text.TextUtils;

import com.xfsi.swipe_demo1.common.logger.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

/**
 * Created by local-kieu on 3/26/16.
 */
public class SimpleWebServer implements Runnable {
    public static final String TAG = "SimpleWebServer";
    private int mPort;
    private AssetManager mAssets;
    private boolean mIsRunning = false;

    private ServerSocket mServerSocket;

    public SimpleWebServer(int port, AssetManager assets) {
        int mPort = port;
        mAssets = assets;
    }

    @Override
    public void run() {
        try {

           mServerSocket = new ServerSocket(mPort);
            Log.i(TAG, "Kieu SimpleWebServer: run, before mIsRunning ");
            while (mIsRunning) {
                Log.i(TAG, "Kieu SimpleWebServer: run, inside while ");
                Socket socket = mServerSocket.accept();
                // never get here, I got SocketException
                Log.i(TAG, "Kieu SimpleWebServer: run, before handle ");
                handle(socket);
                socket.close();
            }

        } catch(SocketException e){
        // The Server was stopped; ignore.
            Log.e(TAG, "Kieu SocketException", e);
        } catch (IOException e) {
            Log.e(TAG, "Web server error.", e);
        }
    }

    public void start() {
        mIsRunning = true;
        new Thread(this).start();
    }

    public void stop() {
        try {
            mIsRunning = false;
            if (null != mServerSocket) {
                mServerSocket.close();
                mServerSocket = null;
            }
        } catch (IOException e) {
            Log.e(TAG, "Error closing Server socket.", e);
        }
    }

    private void handle(Socket socket) {
        BufferedReader reader = null;
        PrintStream output = null;

        try {
            String route = null;
            Log.i(TAG, "Kieu SimpleWebServer: handle");
            // Read HTTP headers and parse out the route.
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line;
            while ( !TextUtils.isEmpty(line = reader.readLine())) {
                if (line.startsWith("GET /")) {
                    int start = line.indexOf('/') + 1;
                    int end = line.indexOf(' ', start);
                    route = line.substring(start, end);
                    break;
                }
            }

            // Output stream that we send the response to
            output = new PrintStream(socket.getOutputStream());
            if ( null == route) {
                writeServerError(output);
                return;
            }
            // load the content from file
            byte[] content = loadContent(route);
            if (null == content) {
               writeServerError(output);
                return;
            }

            // send out the response with content
            output.println("HTML/1.0 200 OK");
            output.println("Content-Type: " + detectMimeType(route));
            output.println("Content-Length: " + content.length);
            output.println();
            output.write(content);
            output.flush();
            output.close();

        } catch (IOException e) {
            Log.e(TAG, "Fail to write to Server.", e);
        } finally {
            if (null != output) {
                output.close();
            }
            try {
                if (null != reader) {
                    reader.close();
                }
            } catch (IOException e) {
                Log.e(TAG, "Fail to close BufferedReader.", e);
            }
        }
    }

    private void writeServerError(PrintStream output) {
        output.println("HTTP/1.0 500 Server Error");
        output.flush();
    }

    private byte[] loadContent(String filename) throws IOException{
        InputStream input = null;

        try {
            input = mAssets.open(filename);
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            int size;
            byte[] buf = new byte[1024];
            while ( -1 != (size = input.read(buf))) {
                output.write(buf, 0, size);
            }
            output.flush();
            return output.toByteArray();
        } catch (FileNotFoundException e) {
            return null;
        } finally {
            if (null != input) {
                input.close();
            }
        }
    }

    private String detectMimeType(String filename) {
        Log.i(TAG, "kieu in detectMimeType filename= " + filename +".");
        if (TextUtils.isEmpty(filename)) {
            return null;
        } else if (filename.endsWith(".html")) {
            return "text/html";
        } else if ( filename.endsWith(".css")) {
            return "text/css";
        } else if (filename.endsWith(".js")) {
            return "application/javascript";
        } else {
            return "application/octet-stream";
        }
    }
}
