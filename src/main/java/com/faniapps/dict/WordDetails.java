package com.faniapps.dict;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

/**
 * Created by user on 13-03-2018.
 */

public class WordDetails extends AppCompatActivity  implements TextToSpeech.OnInitListener  {

    TextView tv_word1,tv_word2,tv_word3,tv_word4;
    TextView tv_share,tv_copy,text_to_speech;
    private ClipboardManager myClipboard;
    private ClipData myClip;
    private TextToSpeech tts;
    private TextView tv_cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.word_detail);
        myClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.word_details));
        setSupportActionBar(toolbar);
        initViews();
        initListeners();
        getDataFromIntent();
    }

    private void initListeners() {

        tv_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(android.content.Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(android.content.Intent.EXTRA_SUBJECT,"Subject test");
                i.putExtra(android.content.Intent.EXTRA_TEXT, tv_word1.getText().toString()+"\n"+tv_word2.getText().toString());
                startActivity(Intent.createChooser(i,"Share via"));
            }
        });

        tv_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myClip = ClipData.newPlainText("text", tv_word1.getText().toString()+"\n"+tv_word2.getText().toString());
                myClipboard.setPrimaryClip(myClip);
                Toast.makeText(getApplicationContext(),"Copied",Toast.LENGTH_LONG).show();
            }
        });

        text_to_speech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tts = new TextToSpeech(WordDetails.this, WordDetails.this);
                speakOut();
            }
        });
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void speakOut() {

        String text = tv_word2.getText().toString();
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
       // tts.shutdown();
    }

    private void initViews() {
        tv_word1 = (TextView) findViewById(R.id.word1);
        tv_word2 = (TextView) findViewById(R.id.word2);
        tv_word3 = (TextView) findViewById(R.id.word3);
        tv_word4 = (TextView) findViewById(R.id.word4);
        tv_share = (TextView) findViewById(R.id.share);
        tv_copy = (TextView) findViewById(R.id.copy);
        text_to_speech = (TextView) findViewById(R.id.texttospeech);
        tv_cancel = (TextView) findViewById(R.id.cancel);
    }


    private void getDataFromIntent() {

        Bundle b = getIntent().getExtras();
        tv_word1.setText(b.getString("tel"));
        tv_word2.setText(b.getString("eng"));

    }


    @Override
    public void onInit(int status) {
        try {
            if (status == TextToSpeech.SUCCESS) {

                int result = tts.setLanguage(Locale.US);
                //  int result = tts.setLanguage(new Locale("te"));
                //Log.e("local langs" ,""+  tts.getAvailableLanguages().toString());
                tts.setPitch(5f);
                tts.setSpeechRate(0.5f);
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "This Language is not supported");
                } else {
                    // btnSpeak.setEnabled(true);
                    speakOut();
                }

            } else {
                Log.e("TTS", "Initilization Failed!");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
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
}
