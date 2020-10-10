package com.example.hp.picturestudio;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button Encode_Button =(Button)findViewById(R.id.button);
        Button Decode_Button = (Button)findViewById(R.id.button2);
        Encode_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,Encode.class);
                startActivity(intent);
            }
        });
        Decode_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(MainActivity.this,Decode.class);
                startActivity(intent);
            }
        });

    }
}
