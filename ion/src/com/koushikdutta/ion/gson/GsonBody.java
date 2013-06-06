package com.koushikdutta.ion.gson;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.koushikdutta.async.DataEmitter;
import com.koushikdutta.async.DataSink;
import com.koushikdutta.async.Util;
import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.async.http.AsyncHttpRequest;
import com.koushikdutta.async.http.AsyncHttpRequestBody;
import com.koushikdutta.ion.Ion;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;

public class GsonBody<T extends JsonElement> implements AsyncHttpRequestBody<T> {
    byte[] mBodyBytes;
    T json;
    Gson gson;
    public GsonBody(Gson gson, T json) {
        this.json = json;
        this.gson = gson;
    }

    @Override
    public void parse(DataEmitter emitter, final CompletedCallback completed) {
        new GsonParser<T>().parse(emitter).setCallback(new FutureCallback<T>() {
            @Override
            public void onCompleted(Exception e, T result) {
                json = result;
                completed.onCompleted(e);
            }
        });
    }

    @Override
    public void write(AsyncHttpRequest request, DataSink sink) {
        if (mBodyBytes == null) {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            OutputStreamWriter out = new OutputStreamWriter(bout);
            gson.toJson(json, out);
            mBodyBytes = bout.toByteArray();
        }
        Util.writeAll(sink, mBodyBytes, null);
    }

    @Override
    public String getContentType() {
        return "application/json";
    }

    @Override
    public boolean readFullyOnRequest() {
        return true;
    }

    @Override
    public int length() {
        if (mBodyBytes == null)
            mBodyBytes = json.toString().getBytes();
        return mBodyBytes.length;
    }

    public static final String CONTENT_TYPE = "application/json";

    @Override
    public T get() {
        return json;
    }
}

