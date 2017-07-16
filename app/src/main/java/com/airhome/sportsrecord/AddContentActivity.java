package com.airhome.sportsrecord;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class AddContentActivity extends AppCompatActivity {
    private TextView mContent;
    private ImageView mImage;
    private Button mButton;
    private String picPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_content);
        setTitle("添加记录");
        mContent = (TextView) findViewById(R.id.content);
        mImage = (ImageView) findViewById(R.id.image);
        mButton = (Button) findViewById(R.id.button_ok);
        picPath = Utils.getFile().getAbsolutePath();
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, 200, 0, "拍照");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 200) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            Uri uri = Uri.parse(picPath);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(intent, 100);
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100 && resultCode == RESULT_OK) {
            FileInputStream fis;
            try {
                fis = new FileInputStream(picPath);
                Bitmap bitmap = BitmapFactory.decodeFile(picPath);
                mImage.setImageBitmap(bitmap);
                Toast.makeText(this, (float) bitmap.getByteCount() / 1024 / 1024 + "", Toast.LENGTH_SHORT).show();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
