package com.jlib.mmsis;

import androidx.annotation.NonNull;

import com.jlib.mmsis.async.FileWriterAsync;
import com.jlib.mmsis.async.NavigationParserAsync;
import com.jlib.mmsis.async.PdfListParserAsync;
import com.jlib.mmsis.models.NavigationModel;
import com.jlib.mmsis.models.PaginationModel;
import com.jlib.mmsis.models.PaginationPostModel;
import com.jlib.mmsis.models.PdfDownloadModel;
import com.jlib.mmsis.models.PdfModel;

import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MmsisApi {
    private NavigationCallBack navigationCallBack;
    private PdfListCallBack pdfListCallBack;
    private DownloadListener downloadListener;

    public void enqueueNavigation(NavigationCallBack navigationCallBack) {
        this.navigationCallBack = navigationCallBack;
    }

    public void enqueuePdfList(PdfListCallBack pdfListCallBack) {
        this.pdfListCallBack = pdfListCallBack;
    }

    public void start(){
        start("001");
    }

    public void start(String code_code) {
        final ApiRequest request = ApiUtil.getRequest("https://www.mmsis.gov.mm/sub_menu/statistics/");
        request.getResponse(code_code).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                ResponseBody body = response.body();
                if (body == null) {
                    if (navigationCallBack != null)
                        MmsisApi.this.navigationCallBack.onError("request body is null");
                } else {
                    String bodyString = getString(body);
                    if (navigationCallBack != null) handleNavigationResponse(bodyString);
                    if (pdfListCallBack != null) handlePdfResponse(bodyString);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                MmsisApi.this.navigationCallBack.onError(t.getMessage());
            }
        });
    }

    private String getString(ResponseBody body) {
        try {
            return body.string();
        } catch (IOException e) {
            return e.getMessage();
        }
    }

    private void handleNavigationResponse(String body) {
        new NavigationParserAsync(new NavigationParserAsync.OnParsedListener() {
            @Override
            public void onParsed(List<NavigationModel> navigationList) {
                navigationCallBack.onSuccess(navigationList);
            }

            @Override
            public void onError(String errorMessage) {
                navigationCallBack.onError(errorMessage);
            }
        }).execute(body);
    }

    private void handlePdfResponse(String body) {
        new PdfListParserAsync(new PdfListParserAsync.OnParsedListener() {
            @Override
            public void onParsed(List<PdfModel> pdfList, List<PaginationModel> paginationList) {
                pdfListCallBack.onSuccess(pdfList, paginationList);
            }

            @Override
            public void onError(String errorMessage) {
                pdfListCallBack.onError(errorMessage);
            }
        }).execute(body);
    }

    public interface NavigationCallBack {
        void onSuccess(List<NavigationModel> navigationList);

        void onError(String errorMessage);
    }

    public interface PdfListCallBack {
        void onSuccess(List<PdfModel> pdfList, List<PaginationModel> paginationList);

        void onError(String errorMessage);
    }

    public void downloadPdf(final String name, PdfDownloadModel downloadModel, final String download_folder, DownloadListener listener) {
        this.downloadListener = listener;
        ApiRequest request = ApiUtil.getRequest("https://www.mmsis.gov.mm/");
        request.getFile("publication", downloadModel.getFile_mask(), downloadModel.getFile_name()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                ResponseBody responseBody = response.body();
                if (responseBody == null) downloadListener.onDownloadError("response is null");
                else startFileWriter(name, download_folder, responseBody);
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                downloadListener.onDownloadError(t.getMessage());
            }
        });
    }

    private void startFileWriter(String name, final String download_folder, ResponseBody responseBody) {
        new FileWriterAsync(name, download_folder, new FileWriterAsync.OnTaskDoneListener() {
            @Override
            public void onSuccess(String file_path) {
                downloadListener.onDownloadComplete(file_path);
            }

            @Override
            public void onError(String errorMessage) {
                downloadListener.onDownloadError(errorMessage);
            }
        }).execute(responseBody);

    }


    public interface DownloadListener {
        void onDownloadComplete(String file_path);

        void onDownloadError(String error);
    }

    public void loadPage(PaginationPostModel postModel, PdfListCallBack pdfListCallBack) {
        this.pdfListCallBack = pdfListCallBack;
        final ApiRequest request = ApiUtil.getRequest("https://www.mmsis.gov.mm/contents/");
        request.loadPage(postModel.getCode_code(), postModel.getPage_list_scale(), postModel.getPg()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                ResponseBody body = response.body();
                if (body == null) MmsisApi.this.pdfListCallBack.onError("request body is null");
                else {
                    String bodyString = getString(body);
                    handlePdfResponse(bodyString);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                MmsisApi.this.pdfListCallBack.onError(t.getMessage());
            }
        });
    }
}
