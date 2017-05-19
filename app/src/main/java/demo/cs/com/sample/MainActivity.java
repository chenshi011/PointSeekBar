package demo.cs.com.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.cs.seekbar.pointseekbar.PointSeekBar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        PointSeekBar seekBar = (PointSeekBar) findViewById(R.id.psb);
        seekBar.setOnSeekBarChangedListener(new PointSeekBar.OnPointSeekBarChangedListener(){
            @Override
            public void onProgressChanged(PointSeekBar pointSeekBar, int progress) {
                Toast.makeText(MainActivity.this,"pointSeekBar on progress:" + progress,Toast.LENGTH_SHORT).show();
            }
        });

        PointSeekBar seekBar2 = (PointSeekBar) findViewById(R.id.psb2);
        seekBar2.setOnSeekBarChangedListener(new PointSeekBar.OnPointSeekBarChangedListener(){
            @Override
            public void onProgressChanged(PointSeekBar pointSeekBar, int progress) {
                Toast.makeText(MainActivity.this,"pointSeekBar on progress:" + progress,Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
