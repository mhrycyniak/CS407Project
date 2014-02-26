package com.wisc.cs407project;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import com.wisc.cs407project.R;

public class Scale extends Activity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.main);
        }


        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
                // Inflate the menu; this adds items to the action bar if it is present.
                getMenuInflater().inflate(R.menu.settings, menu);
                return true;
        }
        
        public void Button2Clicked(View view) {
                Intent intent = new Intent(this, DirChooser.class);
                startActivity(intent);
        }
        
        public void Button3Clicked(View view) {
                Intent intent = new Intent(this, RecordPath.class);
                startActivity(intent);
        }

}