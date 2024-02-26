package com.example.mystr;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TTSUrlExtractor extends AsyncTask<Void, Void, String> {
    private TTSUrlExtractListener listener;
    private static final String TAG = "TTSUrlExtractorTask";


    public interface TTSUrlExtractListener {
        void onTTSUrlExtracted(String ttsUrl);
        void onError(Exception e);
    }

    public TTSUrlExtractor(TTSUrlExtractListener listener) {
        this.listener = listener;
    }
    @Override
    protected void onPostExecute(String ttsUrl) {
        if (ttsUrl != null) {
            listener.onTTSUrlExtracted(ttsUrl);
        } else {
            listener.onError(new Exception("Failed to extract TTS URL"));
        }
    }
    private String getNextLine(String[] lines, String currentLine) {
        int currentIndex = -1;
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].equals(currentLine)) {
                currentIndex = i;
                break;
            }
        }
        if (currentIndex != -1 && currentIndex + 1 < lines.length) {
            return lines[currentIndex + 1];
        }
        return null;
    }

    @Override
    protected String doInBackground(Void... voids) {
        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\"text\":\"Hello from a realistic voice.\",\"voice\":\"s3://voice-cloning-zero-shot/d9ff78ba-d016-47f6-b0ef-dd630f59414e/female-cs/manifest.json\",\"output_format\":\"mp3\",\"voice_engine\":\"PlayHT2.0\"}");
        Request request = new Request.Builder()
                .url("https://api.play.ht/api/v2/tts")
                .post(body)
                .addHeader("accept", "text/event-stream")
                .addHeader("content-type", "application/json")
                .addHeader("AUTHORIZATION", "3ade013c7864438f9a8bbeb35da8699e")
                .addHeader("X-USER-ID", "tHxfwF6JhiWxTIAGAWhB0G8HRft1")
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful())
                throw new IOException("Unexpected response code: " + response);
            String responseBody = response.body().string();
            Log.d(TAG, "Response: " + responseBody);
            String[] lines = responseBody.split("\\r?\\n");
            String ttsUrl = null;
            for (String line : lines) {
                if (line.startsWith("event: completed")) {
                    String nextLine = getNextLine(lines, line);
                    if (nextLine != null && nextLine.startsWith("data:")) {
                        String data = nextLine.substring("data:".length()).trim();
                        String[] parts = data.split(",");
                        for (String part : parts) {
                            if (part.trim().startsWith("\"url\":")) {
                                ttsUrl = part.substring("\"url\":\"".length(), part.lastIndexOf('"'));
                                break;
                            }
                        }
                    }
                    break;
                }
            }
            return ttsUrl;
        } catch (Exception e) {
            Log.e(TAG, "Error: " + e.getMessage(), e);
            return null;
        }
    }
}
