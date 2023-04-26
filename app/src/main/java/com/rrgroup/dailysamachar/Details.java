package com.rrgroup.dailysamachar;

import static com.rrgroup.dailysamachar.MainActivity.DESCRIPTION;
import static com.rrgroup.dailysamachar.MainActivity.HEADLINE;
import static com.rrgroup.dailysamachar.MainActivity.IMAGE;
import static com.rrgroup.dailysamachar.MainActivity.SOURCE_LINK;
import static com.rrgroup.dailysamachar.MainActivity.SOURCE_NAME;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.nl.languageid.LanguageIdentification;
import com.google.mlkit.nl.languageid.LanguageIdentifier;
import com.squareup.picasso.Picasso;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class Details extends AppCompatActivity {
    TextToSpeech textToSpeech1;
    TextToSpeech textToSpeech2;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Intent intent = getIntent();

        String headline = intent.getStringExtra(HEADLINE);
        String image = intent.getStringExtra(IMAGE);
        String src_link=intent.getStringExtra(SOURCE_LINK);
        String src_name=intent.getStringExtra(SOURCE_NAME);
        String desc_uncleaned=intent.getStringExtra(DESCRIPTION);

        ImageView imageView=findViewById(R.id.imageView);
        Picasso.get()
                .load(image)
                .fit()
                //.transform(transformation)
                .into(imageView);

        TextView description = findViewById(R.id.textView);
        String desc= desc_uncleaned.replaceAll("xa0", " ");

        //description.setText(desc);

        //setting Read More
        String full = desc+"\n\nRead More";
        SpannableString spannableString = new SpannableString(full);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                // Create an Intent to open a WebView activity
                Intent intent = new Intent(Details.this, WebViewActivity.class);
                intent.putExtra("url", src_link);
                startActivity(intent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.GRAY);
                ds.setUnderlineText(true);
            }
        };
        spannableString.setSpan(clickableSpan, full.indexOf("Read More"), full.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        description.setText(spannableString);
        description.setMovementMethod(LinkMovementMethod.getInstance());

        //setting source name
        TextView src = findViewById(R.id.src);
        src.setText("Source : "+src_name);

        //progress dialog
        progressDialog=new ProgressDialog(this);

        CardView text_to_speech_btn = findViewById(R.id.TextToSpeech);


        textToSpeech1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {

                // if No error is found then only it will run
                if(i!=TextToSpeech.ERROR){
                    // To Choose language of speech
                    textToSpeech1.setLanguage(Locale.US);
                }
            }
        });
        textToSpeech2 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                // if No error is found then only it will run
                if(i!=TextToSpeech.ERROR){
                    // To Choose language of speech
                    textToSpeech2.setLanguage(new Locale ("hi","IN"));
                }
            }
        });

        text_to_speech_btn.setOnClickListener(view->{
            languageIdentify(desc);
        });



    }

    private void speakInEnglish(String desc) {
        textToSpeech1.setSpeechRate(0.8f);
        //textToSpeech1.setPitch(1f);
        Set<String> a=new HashSet<>();
        a.add("male");
        Voice v = new Voice("en-in-x-end-network", new Locale("en","IN"), 400, 200, false, a);
        textToSpeech1.setVoice(v);
        textToSpeech1.speak(desc,TextToSpeech.QUEUE_FLUSH,null);
        Toast.makeText(this, "Speaking Mode is Starting...", Toast.LENGTH_SHORT).show();
    }
    private void speakInHindi(String desc) {
        textToSpeech2.setSpeechRate(0.8f);
        //textToSpeech2.setPitch(1.2f);
        Set<String> a=new HashSet<>();
        a.add("male");
        Voice v = new Voice("hi-in-x-hie-local", new Locale("hi","IN"), 400, 200, false, a);
        textToSpeech2.setVoice(v);
        textToSpeech2.speak(desc,TextToSpeech.QUEUE_FLUSH,null);
        Toast.makeText(this, "Speaking Mode is Starting...", Toast.LENGTH_SHORT).show();
    }
    public void languageIdentify(String desc) {
        //language identify hindi or english
        LanguageIdentifier languageIdentifier =
                LanguageIdentification.getClient();
        languageIdentifier.identifyLanguage(desc)
                .addOnSuccessListener(
                        new OnSuccessListener<String>() {
                            @Override
                            public void onSuccess(@Nullable String languageCode) {
                                if (languageCode.equals("und")) {
                                    speakInHindi(desc);
                                } else if(languageCode.equals("en")) {
                                    System.out.println(languageCode);
                                    speakInEnglish(desc);
                                }else {
                                    System.out.println(languageCode);
                                    speakInHindi(desc);
                                }
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Model couldn’t be loaded or other internal error.
                                // ...
                            }
                        });

    }
    Boolean isBackPressed = false;
    @Override
    public void onBackPressed() {
        isBackPressed = true;
        textToSpeech1.stop();
        textToSpeech2.stop();
        finish();
    }
    @Override
    protected void onPause() {
        super.onPause();
        if(!isBackPressed) {
            textToSpeech1.stop();
            textToSpeech2.stop();
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        textToSpeech1.stop();
        textToSpeech2.stop();
    }
}
