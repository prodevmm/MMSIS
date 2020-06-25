package com.jlib.mmsis.async;

import android.os.AsyncTask;

import java.io.File;
import java.io.FileOutputStream;

import okhttp3.ResponseBody;

public class FileWriterAsync extends AsyncTask<ResponseBody, Void, String> {
    private String name, download_folder;
    private OnTaskDoneListener onTaskDoneListener;
    private String file_path = "";

    public FileWriterAsync(String name, String download_folder, OnTaskDoneListener onTaskDoneListener) {
        this.name = name;
        this.download_folder = download_folder;
        this.onTaskDoneListener = onTaskDoneListener;
    }

    @Override
    protected String doInBackground(ResponseBody... responseBodies) {
        try {
            File file = new File(new File(download_folder), name + ".pdf");
            if (!file.exists()) {
                //noinspection ResultOfMethodCallIgnored
                file.createNewFile();
            }
            file_path = file.getAbsolutePath();

            FileOutputStream fos = new FileOutputStream(file);
            fos.write(responseBodies[0].bytes());
            fos.close();
        } catch (Exception e) {
            return e.getMessage();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        if (s == null) onTaskDoneListener.onSuccess(file_path);
        else onTaskDoneListener.onError(s);

    }

    public interface OnTaskDoneListener {
        void onSuccess (String file_path);
        void onError (String errorMessage);
    }
}
