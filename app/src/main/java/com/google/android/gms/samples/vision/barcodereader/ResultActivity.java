package com.google.android.gms.samples.vision.barcodereader;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;


import org.json.JSONObject;


import java.util.*;


/**
 * Activity that send the QRCodeReceived(an ID) to the server and receive a JsonObj as response
 * via http.
 * FloatingActionButton activates text to speech
 */
public class ResultActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
    private String QRCodeReceived = "";
    private ListView listView ;
    private Context mContext;
    private String app;
    private Evidence evidence = new Evidence();
    private String link = "http://192.168.85.1:8080/user/evidence/";
    TextToSpeech tts;
    FloatingActionButton btnSpeak;
    boolean speech_in_progress = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        listView = (ListView) findViewById(R.id.list);
        //getting the result of BarcodeCaptureActivity by intent
        Intent intent = getIntent();
        QRCodeReceived = intent.getExtras().getString("qrResult");

        mContext = this;


        //String url = link + QRCodeReceived;
        //String url = "http://192.168.85.1:8080/user/evidence/1";
        String url = "http://headers.jsontest.com/";
        RequestQueue queue = Volley.newRequestQueue(this);
        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        Log.d("Response", response.toString());
                        Gson gson = new Gson();
                        String x = ("{\"id\":1,\"creationDate\":15130955,\"museum\":{\"id\":1,\"name\":\"Museo contemporaneo di sta minchia\",\"location\":\"Lecce\",\"address\":\"Via Giuditta\",\"postalNo\":73100,\"description\":\"Un museo davvero figo\",\"email\":\"poroporo@gmail.com\"},\"owner\":null,\"number\":0,\"domain\":null,\"type\":null,\"title\":\"Un'opera fantastica\",\"material\":null,\"technique\":null,\"culture\":null,\"period\":null,\"author\":\"Mia madre\",\"dimensions\":null,\"weight\":0,\"value\":null,\"otherData\":null,\"scaleOfDamage\":null,\"originPlace\":null,\"storageLocation\":null,\"historicInformation\":null,\"description\":\"Che opera!\",\"restauration\":null,\"operator\":{\"id\":1,\"name\":\"Simone\",\"surname\":\"Colaci\",\"username\":\"spronghi\",\"password\":\"popo\",\"email\":\"sim.colaci@gmail.com\",\"museum\":{\"id\":1,\"name\":\"Museo contemporaneo di sta minchia\",\"location\":\"Lecce\",\"address\":\"Via Giuditta\",\"postalNo\":73100,\"description\":\"Un museo davvero figo\",\"email\":\"poroporo@gmail.com\"},\"admin\":true},\"public\":false,\"original\":false}");
                        evidence = gson.fromJson(x, Evidence.class);

                        // ArrayList for array adapter
                        final ArrayList <String> listp = new ArrayList<String>();

                            listp.add(new String("" + evidence.getOwner()));
                            listp.add(new String("" + evidence.getNumber()));
                            listp.add(new String("" + evidence.getDomain()));
                            listp.add(new String("" + evidence.getType()));
                            listp.add(new String("" + evidence.getTitle()));
                            listp.add(new String("" + evidence.getMaterial()));
                            listp.add(new String("" + evidence.getTechnique()));
                            listp.add(new String("" + evidence.getCulture()));
                            listp.add(new String("" + evidence.getPeriod()));
                            listp.add(new String("" + evidence.getAuthor()));
                            listp.add(new String("" + evidence.getDimensions()));
                            listp.add(new String("" + evidence.getWeight()));
                            listp.add(new String("" + evidence.getValue()));
                            listp.add(new String("" + evidence.getOtherData()));
                            listp.add(new String("" + evidence.getScaleOfDamage()));
                            listp.add(new String("" + evidence.getOriginPlace()));
                            listp.add(new String("" + evidence.getStorageLocation()));
                            listp.add(new String("" + evidence.getHistoricInformation()));
                            listp.add(new String("" + evidence.getDescription()));
                            listp.add(new String("" + evidence.getRestauration()));
                            listp.add(new String("" + evidence.getOriginPlace()));
                            listp.add(new String("" + evidence.isOriginal()));

                        StringBuilder sb = new StringBuilder();
                        for(String s : listp){
                            sb.append(s);
                        }
                        app = sb.toString();

                        // get list from layout
                        ListView mylist = (ListView) findViewById(R.id.list);
                        // create adapter
                        ArrayAdapter <String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, listp);
                        // inject data
                        mylist.setAdapter(adapter);

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", error.getMessage());
                    }
                }
        );
        // add it to the RequestQueue
        queue.add(getRequest);



    btnSpeak = (FloatingActionButton) findViewById(R.id.fab);
        tts = new TextToSpeech(this, this);
        speech_in_progress = false;
        btnSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                if (speech_in_progress == false) {
                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ResultActivity.this);
                    alertDialogBuilder.setMessage("Turn on audio text to speech?");
                    alertDialogBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            speakOut();
                            speech_in_progress = true;
                        }
                    })
                            .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    speech_in_progress = false;
                                }
                            });
                    alertDialogBuilder.create();
                    alertDialogBuilder.show();
                } else {
                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ResultActivity.this);
                    alertDialogBuilder.setMessage("Turn off audio text to speech?");
                    alertDialogBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            tts.stop();
                            speech_in_progress = false;
                        }
                    })

                            .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    speech_in_progress = true;
                                }
                            });

                    alertDialogBuilder.create();
                    alertDialogBuilder.show();
                }

            }
        });

    }

    @Override
    public void onDestroy() {
        // Don't forget to shutdown tts!
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    /**
     * @param status
     */
    @Override
    public void onInit(int status) {

        if (status == TextToSpeech.SUCCESS) {

            //int result = tts.setLanguage(Locale.US);
            int result = tts.setLanguage(Locale.ITALIAN);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
                btnSpeak.setEnabled(true);
            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }

    }

    /**
     * Method that specify the string to text
     */
    private void speakOut() {
        tts.speak(app, TextToSpeech.QUEUE_FLUSH, null);
    }

}

