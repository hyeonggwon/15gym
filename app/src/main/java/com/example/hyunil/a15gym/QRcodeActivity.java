package com.example.hyunil.a15gym;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;


public class QRcodeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);
    }

    public void onScanButtonClicked(View v) {
        final Activity activity = this;

        IntentIntegrator integrator = new IntentIntegrator(activity);

        integrator.setCaptureActivity(AnyOritationCaptureActivity.class);

        integrator.setOrientationLocked(false);
        integrator.setBarcodeImageEnabled(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.DATA_MATRIX_TYPES);
        integrator.setCameraId(0);
        integrator.setBeepEnabled(false);
        integrator.setPrompt("Scan중...");
        integrator.initiateScan();
    }

    /*protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        //  com.google.zxing.integration.android.IntentIntegrator.REQUEST_CODE
        //  = 0x0000c0de; // Only use bottom 16 bits
        if (requestCode == IntentIntegrator.REQUEST_CODE) {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (result == null) {
                // 취소됨
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                // 스캔된 QRCode --> result.getContents()
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        TextView qrtext2 = (TextView) findViewById(R.id.qrtext2);
        TextView qrtext4 = (TextView) findViewById(R.id.qrtext4);
        TextView qrtext5 = (TextView) findViewById(R.id.qrtext5);
        if (requestCode == IntentIntegrator.REQUEST_CODE) {
                IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (result != null && resultCode==RESULT_OK) {
                try {
                    //data를 json으로 변환
                    JSONObject obj = new JSONObject(result.getContents());
                    qrtext2.setText(obj.getString("name"));
                    qrtext4.setText(obj.getString("address"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    //Toast.makeText(MainActivity.this, result.getContents(), Toast.LENGTH_LONG).show();
                    qrtext5.setText(result.getContents());
                }
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(result.getContents()));
                startActivity(intent);

            } else {
                // 취소됨
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}

