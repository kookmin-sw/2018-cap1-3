package kr.seon.alarm;

/**
 * Created by sara on 18. 3. 17.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class SplashActivity extends Activity{
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        try{
            Thread.sleep(4000);
            startActivity(new Intent(this,MainActivity.class));
            finish();
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
        /*startActivity(new Intent(this,MainActivity.class));
        finish();*/
    }
}
