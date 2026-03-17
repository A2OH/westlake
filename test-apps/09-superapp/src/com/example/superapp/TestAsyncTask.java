package com.example.superapp;

import android.os.AsyncTask;
import java.util.ArrayList;
import java.util.List;

/**
 * AsyncTask subclass for testing execution lifecycle.
 */
public class TestAsyncTask extends AsyncTask<String, Integer, String> {

    public static boolean sPreExecuteCalled = false;
    public static boolean sDoInBackgroundCalled = false;
    public static boolean sPostExecuteCalled = false;
    public static String sResult = null;
    public static String sInput = null;
    public static final List<Integer> sProgressValues = new ArrayList<Integer>();
    public static final List<String> sExecutionOrder = new ArrayList<String>();

    public static void reset() {
        sPreExecuteCalled = false;
        sDoInBackgroundCalled = false;
        sPostExecuteCalled = false;
        sResult = null;
        sInput = null;
        sProgressValues.clear();
        sExecutionOrder.clear();
    }

    @Override
    protected void onPreExecute() {
        sPreExecuteCalled = true;
        sExecutionOrder.add("onPreExecute");
    }

    @Override
    protected String doInBackground(String... params) {
        sDoInBackgroundCalled = true;
        sExecutionOrder.add("doInBackground");
        sInput = (params != null && params.length > 0) ? params[0] : null;
        publishProgress(Integer.valueOf(50));
        publishProgress(Integer.valueOf(100));
        return "result-" + sInput;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        sExecutionOrder.add("onProgressUpdate");
        if (values != null && values.length > 0) {
            sProgressValues.add(values[0]);
        }
    }

    @Override
    protected void onPostExecute(String result) {
        sPostExecuteCalled = true;
        sResult = result;
        sExecutionOrder.add("onPostExecute");
    }
}
