package com.jlib.mmsis.async;

import android.os.AsyncTask;

import com.jlib.mmsis.models.PaginationModel;
import com.jlib.mmsis.models.PaginationPostModel;
import com.jlib.mmsis.models.PdfDownloadModel;
import com.jlib.mmsis.models.PdfModel;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class PdfListParserAsync extends AsyncTask<String, Void, List<PdfModel>> {

    private static final String regex = "'(.*?)'";
    private OnParsedListener onParsedListener;
    private String errorMessage = "";
    private List<PaginationModel> paginationList = new ArrayList<>();

    public PdfListParserAsync(OnParsedListener listener) {
        this.onParsedListener = listener;
    }

    @Override
    protected List<PdfModel> doInBackground(String... strings) {
        try {
            String html = strings[0];

            Document document = Jsoup.parse(html);
            Elements elements = document.select("table tbody tr");


            List<PdfModel> pdfList = new ArrayList<>();
            for (Element e : elements) {
                Elements tdList = e.select("td");
                if (tdList.size() != 6) {
                    errorMessage = "No PDF list found";
                    break;
                }

                String sortNumber = tdList.get(0).text();
                String name = tdList.get(1).text();
                String size = tdList.get(2).text();
                String date = tdList.get(3).text();
                String download_count = tdList.get(4).text();
                String downloadQuery = tdList.get(5).select("a").attr("onclick");
                PdfDownloadModel downloadModel = getDownloadModel(downloadQuery);
                if (downloadModel == null) {
                    errorMessage = "cannot retrieve download query of PDF";
                    break;
                }

                PdfModel model = new PdfModel(sortNumber, name, size, date, download_count, downloadModel);
                pdfList.add(model);
            }

            Elements pElements = document.select("div.pagination-nav.pagination-nav-centered ul li.active a");
            // if (pElements.size() == 0) errorMessage = "pagination list not found";

            for (Element e : pElements) {
                String pageNumber = e.text();
                String goPageQuery = e.attr("onclick");
                PaginationPostModel postModel = getPaginationPostModel(goPageQuery);
                if (postModel == null) {
                    errorMessage = "cannot get pagination list";
                    break;
                }
                PaginationModel model = new PaginationModel(pageNumber, postModel);
                paginationList.add(model);
            }

            return pdfList;
        } catch (Exception e) {
            errorMessage = e.getMessage();
            return null;
        }

    }

    private PdfDownloadModel getDownloadModel(String downloadQuery) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(downloadQuery);
        String file_mask = null, filename = null;
        if (matcher.find()) {
            file_mask = matcher.group(1);
        }
        if (matcher.find()) {
            filename = matcher.group(1);
        }

        if (file_mask != null && filename != null) return new PdfDownloadModel(file_mask, filename);
        else return null;
    }

    private PaginationPostModel getPaginationPostModel(String goPageQuery) throws JSONException {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(goPageQuery);
        String q = null;
        if (matcher.find() && matcher.find() && matcher.find()) {
            q = matcher.group(1);
        }

        if (q == null) return null;
        else {
            q = q.replace("&", "\"");
            JSONObject jsonObject = new JSONObject(q);
            String code_code = jsonObject.getString("code_code");
            String page_list_scale = jsonObject.getString("pagelistscale");
            String pg = jsonObject.getString("pg");
            return new PaginationPostModel(code_code, page_list_scale, pg);
        }
    }

    @Override
    protected void onPostExecute(List<PdfModel> pdfList) {
        if (!errorMessage.isEmpty()) onParsedListener.onError(errorMessage);
        else onParsedListener.onParsed(pdfList, paginationList);
    }

    public interface OnParsedListener {
        void onParsed(List<PdfModel> pdfList, List<PaginationModel> paginationList);

        void onError(String errorMessage);
    }
}
