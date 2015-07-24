package kz.flabs.cashtracker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.IOException;
import java.util.Locale;


public class choose_lang_page extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_lang_page);
        //makeView();
    }

    public void showNotif(String message) {
        Toast toast = Toast.makeText(getApplicationContext(),
                message,
                Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0, 0);
        toast.show();
    }

    public void showAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(choose_lang_page.this);
        builder.setMessage(message)
                .setCancelable(false)
                .setNegativeButton("ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void switchLang(View view) {
        TextView chooselangtitle = (TextView)findViewById (R.id.choose_lang_title);
        Integer id_lang = view.getId();
        android.util.Log.d("id_lang", id_lang.toString());
        Configuration conf = getResources().getConfiguration();
        String newlang = "rus";
        switch(view.getId()) {
            case R.id.eng:
                conf.locale = new Locale("en");
                newlang = "eng";
                break;
            case R.id.kaz:
                conf.locale = new Locale("kk");
                newlang = "kaz";
                break;
            case R.id.rus:
                conf.locale = new Locale("ru");
                newlang = "rus";
                break;
            case R.id.bg:
                conf.locale = new Locale("bg");
                newlang = "bg";
                break;
        }
        new SendNewLangRequest().execute(newlang);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        Resources resources = new Resources(getAssets(), metrics, conf);
        String str = resources.getString(R.string.ChooseLang);
        chooselangtitle.setText(str);
        String current_lang = conf.locale.getDisplayLanguage();
        String x = "1x";
    }


    public class SendNewLangRequest extends AsyncTask<String, Integer, HttpResponse> {
        Integer status;
        HttpResponse response;
        String response_string;
        @Override
        protected HttpResponse doInBackground(String... params) {
            try {
                HttpGet httpget = new HttpGet("http://172.16.250.9:38555/CashTracker/rest/page/welcome?lang="+params[0]);
                HttpClient httpclient = new DefaultHttpClient();
               /* httppost.setHeader("Accept", "application/json");
                httppost.setHeader("Content-type", "application/json");
                httppost.setHeader("X-Request-With", "XMLHttpRequest");
                JSONObject auth = new JSONObject();
                auth.put("login", params[0]);
                auth.put("pwd", params[1]);
                JSONObject authUser = new JSONObject();
                authUser.put("authUser",auth);
                StringEntity se = new StringEntity(authUser.toString());
                se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                httppost.setEntity(se);*/
                response = httpclient.execute(httpget);
                HttpEntity resEntityGet = response.getEntity();

                if (resEntityGet != null) {

                    //String response_string = null;
                    try {
                        response_string = EntityUtils.toString(resEntityGet);
                        android.util.Log.d("GET RESPONSE", response_string);
                        //intent.putExtra("response", response_string);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return response;
        }

        protected void onPostExecute(HttpResponse response) {
            status = response.getStatusLine().getStatusCode();

            if (status == 500) {
                showAlert("Ошибка");
            }
            if (status == 200) {
                showAlert("ok");
            }
        }
    }

    public void makeView() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://172.16.250.9:38555/CashTracker/rest/page/welcome";
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                // TODO Auto-generated method stub
                String testresp = "Response => "+response.toString();
                android.util.Log.d("response", testresp);
                // findViewById(R.id.progressBar1).setVisibility(View.GONE);

                try {
                    //JSONArray jsonMainNode = response.optJSONArray("_Page");
                    JSONObject _Page = response.getJSONObject("_Page");
                    JSONObject includedPages = _Page.getJSONArray("includedPages").getJSONObject(0);
                    JSONObject captions = includedPages.getJSONObject("captions");
                    // JSONArray jsonMainNode = jobject.optJSONArray("elements");
                    String login_button_caption = captions.getJSONArray("login_button").getString(0);
                    String login_caption = captions.getJSONArray("login_login").getString(0);
                    String pwd_caption = captions.getJSONArray("login_pwd").getString(0);
                    String promo_caption = captions.getJSONArray("promo_line1").getString(0);


                    setContentView(R.layout.activity_main);
                    Button login_button = (Button)findViewById (R.id.loginbtn);
                    login_button.setText(login_button_caption);

                    EditText login_edittext = (EditText)findViewById (R.id.login);
                    login_edittext.setHint(login_caption);

                    EditText pwd_edittext = (EditText)findViewById (R.id.pwd);
                    pwd_edittext.setHint(pwd_caption);

                    TextView promo_textview = (TextView)findViewById (R.id.textView);
                    promo_textview.setText(promo_caption);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
            }
        });
        queue.add(jsObjRequest);


    }
}