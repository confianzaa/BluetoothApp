package project.mybluetooth;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

public class thankyou extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thankyou);

        final TextView t,y;
        t= (TextView) findViewById(R.id.textView2);
        y= (TextView) findViewById(R.id.textView3);

        final Animation anm= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.anm);
        final Animation anm1= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.anm1);
        final Animation shake=AnimationUtils.loadAnimation(getApplicationContext(),R.anim.shake);

        t.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                t.setAnimation(anm);
//                t.setTextSize(70);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                t.setAnimation(anm1);
//                t.setTextSize(35);
            }
        });

        y.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                y.startAnimation(shake);
            }
        });

    }
}
