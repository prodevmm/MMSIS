package com.jlib.mmsis.async;

import android.os.AsyncTask;

import com.jlib.mmsis.models.NavigationModel;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;


public class NavigationParserAsync extends AsyncTask<String, Void, List<NavigationModel>> {
    private OnParsedListener onParsedListener;
    private String errorMessage = "";
    public NavigationParserAsync(OnParsedListener listener){
        this.onParsedListener = listener;
    }

    @Override
    protected List<NavigationModel> doInBackground(String... strings) {
        try {

            String html = strings[0];
            Document document = Jsoup.parse(html);
            Elements elements = document.select("div#left_menu ul li.sub ul li a");

            List<NavigationModel> navigationList = new ArrayList<>();
            for (Element e : elements){
                String name = e.text().trim();
                String code_code = e.attr("href");
                code_code = code_code.substring(code_code.indexOf("code_code=") + 10);

                NavigationModel model = new NavigationModel(name, code_code);
                navigationList.add(model);
            }

            return navigationList;
        } catch (Exception e){
            errorMessage = e.getMessage(); return null;
        }

    }

    @Override
    protected void onPostExecute(List<NavigationModel> mmsisNavigationModels) {
        if (mmsisNavigationModels == null) onParsedListener.onError(errorMessage);
        else onParsedListener.onParsed(mmsisNavigationModels);
    }

    public interface OnParsedListener{
        void onParsed (List<NavigationModel> navigationList);
        void onError (String errorMessage);
    }
}
