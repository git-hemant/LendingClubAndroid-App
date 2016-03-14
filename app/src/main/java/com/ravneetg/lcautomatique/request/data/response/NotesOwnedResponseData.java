package com.ravneetg.lcautomatique.request.data.response;

import com.google.gson.stream.JsonReader;
import android.util.Log;

import com.ravneetg.lcautomatique.MainApplication;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;
import com.google.gson.Gson;
import com.ravneetg.lcautomatique.data.gson.NoteGsonBean;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by HemantSingh on 1/18/2015.
 */
public class NotesOwnedResponseData extends ResponseData {

    public static final String MY_NOTES = "myNotes";
    public static final String LOAN_LIST_DATE = "listD";
    private List<NoteOwnedResponseData> notes;
    private JsonReader reader;
    private boolean readingInitialized;
    private Gson gson = new Gson();

    public NotesOwnedResponseData(InputStream inputStream, Exception exception, int responseCode) {
        super(inputStream, exception, responseCode);
        try {
            reader = new JsonReader(new InputStreamReader(inputStream, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            // This should never happen.
            Log.e(MainApplication.TAG, e.getMessage(), e);
        }
    }

    public boolean hasNextNote() throws IOException {
        initReading();
        return reader.hasNext();
    }

    public NoteGsonBean nextNote() throws IOException {
        NoteGsonBean note = null;
        try {
            note = gson.fromJson(this.reader, NoteGsonBean.class);
        } catch (RuntimeException e) {
            MainApplication.reportCaughtException(e);
        }
        return note;
    }

    public void closeInputStream() {
        try {
            reader.close();
        } catch (Exception e) {}
    }

    private void initReading() throws IOException {
        if (!readingInitialized) {
            // Start the top level object.
            reader.beginObject();
            // Read the name myNotes
            reader.nextName();
            // Now start reading the notes array
            reader.beginArray();
            readingInitialized = true;
        }
    }

    public JSONObject getResponseData() {
        throw new RuntimeException("Unsupported operation.");
    }

    public JSONObject noteToJson(NoteGsonBean noteGsonBean) throws JSONException {
        return new JSONObject(gson.toJson(noteGsonBean, NoteGsonBean.class));
    }

    public List<NoteOwnedResponseData> getNotes() {
        return notes;
    }
}
