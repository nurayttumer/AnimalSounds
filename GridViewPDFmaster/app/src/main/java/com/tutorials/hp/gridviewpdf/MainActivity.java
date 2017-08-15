package com.tutorials.hp.gridviewpdf;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.GridView;

import java.io.File;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final GridView gv= (GridView) findViewById(R.id.gv);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gv.setAdapter(new CustomAdapter(MainActivity.this,getPDFs()));

            }
        });
    }

    private ArrayList<PDFDoc> getPDFs()

    {
        ArrayList<PDFDoc> pdfDocs=new ArrayList<>();
        //TARGET FOLDER
        File downloadsFolder= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        PDFDoc pdfDoc;

        if(downloadsFolder.exists())
        {
            //GET ALL FILES IN DOWNLOAD FOLDER
            File[] files=downloadsFolder.listFiles();

            //LOOP THRU THOSE FILES GETTING NAME AND URI
            for (int i=0;i<files.length;i++)
            {
                File file=files[i];

                if(file.getPath().endsWith("pdf"))
                {
                    pdfDoc=new PDFDoc();
                    pdfDoc.setName(file.getName());
                    pdfDoc.setPath(file.getAbsolutePath());

                    pdfDocs.add(pdfDoc);
                }

            }
        }

        return pdfDocs;
    }


    // internetten indirilecek olan dosyanın adresi
    String indirilecekDosyaAdresi = "http://androidevreni.com/api/android/fotograf.jpg";
    // dosya indirildikten sonra hangi yola kaydedilecek.
    String dosyaKayitYeri = "/sdcard/fotograf.jpg";
    // dosya sunucuya gönderilirken (upload) hangi adres kullanılacak
    String uploadAdresi = "http://androidevreni.com/api/android/upload.php";

    // upload download işlemlerinin % olarak göstermek için kullanılacak progress dialog
    ProgressDialog pDialog;

    // indirdiğimiz dosyayı byte şeklinde saklayacağımız değişken
    byte[] inenDosya;
    // get ve post işlemleri yapacağımız AsyncHttpClient nesnesi
    final AsyncHttpClient client = new AsyncHttpClient();
    // dosya gönderirken dosyayı iliştireceğimiz nesne.
    final RequestParams params = new RequestParams();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pDialog = new ProgressDialog(this);
        pDialog.setIndeterminate(false);
        pDialog.setMax(100);
        pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pDialog.setCancelable(true);
    }

    public void mesajGoster(String mesaj) {
        Toast.makeText(getApplicationContext(), mesaj, Toast.LENGTH_LONG)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    public void onClickDosyaIndir(View v) {
        pDialog.setMessage("Dosya indiriliyor. Lütfen bekleyin...");
        pDialog.show();

        client.get(indirilecekDosyaAdresi, new BinaryHttpResponseHandler() {
            @Override
            public void onSuccess(byte[] fileData) {

                try {
                    OutputStream output = new FileOutputStream(dosyaKayitYeri);
                    output.write(fileData);
                    output.flush();
                    mesajGoster("Dosya SD karta kaydedildi!");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onProgress(int bytesWritten, int totalSize) {
                super.onProgress(bytesWritten, totalSize);
                int progress = (bytesWritten * 100) / totalSize;
                pDialog.setProgress(progress);
                if (progress == 100)
                    pDialog.dismiss();
            }
        });
    }

    public void onClickDosyaGonder(View v) {
        File file = new File(dosyaKayitYeri);
        try {
            params.put("dosya", file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        pDialog.setMessage("Dosya gönderiliyor. Lütfen bekleyin...");
        pDialog.show();

        client.post(uploadAdresi, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                mesajGoster("Dosya sunucuya gönderildi!");
            }

            @Override
            public void onProgress(int bytesWritten, int totalSize) {
                super.onProgress(bytesWritten, totalSize);
                int progress = (bytesWritten * 100) / totalSize;
                pDialog.setProgress(progress);

                if (progress == 100)
                    pDialog.dismiss();
            }
        });
    }
}
