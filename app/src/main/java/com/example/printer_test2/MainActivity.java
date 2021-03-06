package com.example.printer_test2;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.mazenrashed.printooth.Printooth;
import com.mazenrashed.printooth.data.DefaultPrintingImagesHelper;
import com.mazenrashed.printooth.data.printable.ImagePrintable;
import com.mazenrashed.printooth.data.printable.Printable;
import com.mazenrashed.printooth.data.printable.RawPrintable;
import com.mazenrashed.printooth.data.printable.TextPrintable;
import com.mazenrashed.printooth.data.printer.DefaultPrinter;
import com.mazenrashed.printooth.ui.ScanningActivity;
import com.mazenrashed.printooth.utilities.Printing;
import com.mazenrashed.printooth.utilities.PrintingCallback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements PrintingCallback {
    Printing printing;
    Button conncet , print;
    EditText textText ;
    LinearLayout imagelinear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initview();
    }

    private void initview() {
        conncet = findViewById(R.id.btnConnect);
        print = findViewById(R.id.btnPrint);
        textText = findViewById(R.id.txtText);
        imagelinear = findViewById(R.id.imagelinear);

        if (printing !=null){
            printing.setPrintingCallback(this);
        }

        conncet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Printooth.INSTANCE.hasPairedPrinter()){
                    Printooth.INSTANCE.removeCurrentPrinter();
                }
                else {
                    startActivityForResult(new Intent(MainActivity.this, ScanningActivity.class),ScanningActivity.SCANNING_FOR_PRINTER);
                    ChangePairAndUnpair();
                }
            }
        });

        print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textText.getText().toString().length()>0){
                    if (!Printooth.INSTANCE.hasPairedPrinter()){
                        startActivityForResult(new Intent(MainActivity.this, ScanningActivity.class),ScanningActivity.SCANNING_FOR_PRINTER);
                    }
                    else {
                        PrintText();
                    }
                    //PrintText();
                }

            }
        });
        ChangePairAndUnpair();
    }

    private void PrintText() {
        imagelinear.setDrawingCacheEnabled(true);
        imagelinear.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        imagelinear.layout(0, 0, imagelinear.getMeasuredWidth(), imagelinear.getMeasuredHeight());
        imagelinear.buildDrawingCache(true);
        Bitmap b = Bitmap.createBitmap(imagelinear.getDrawingCache());
        imagelinear.setDrawingCacheEnabled(false);
        ArrayList<Printable> printables = new ArrayList<>();
        printables.add(new ImagePrintable.Builder(toGrayscale(b)).setAlignment(DefaultPrinter.Companion.getALIGNMENT_CENTER()).build());
        System.out.println(printables);
        System.out.println(printing);
        printing.print(printables);
//        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        toGrayscale(b).compress(Bitmap.CompressFormat.PNG, 30, stream);
//        byte[] byteArray = stream.toByteArray();
//        Intent intent = new Intent(MainActivity.this,View_Image.class);
//        intent.putExtra("image",byteArray);
//        startActivity(intent);
    }

    private void ChangePairAndUnpair() {
        if (Printooth.INSTANCE.hasPairedPrinter()) {
            conncet.setText(new StringBuilder("UnPair ").append(Printooth.INSTANCE.getPairedPrinter().getName()).toString());
        }
        else {
            conncet.setText("Paird With Printer");
        }
    }

    @Override
    public void connectingWithPrinter() {
        Toast.makeText(MainActivity.this,"connecting with printer",Toast.LENGTH_LONG).show();

    }

    @Override
    public void connectionFailed(String s) {
        Toast.makeText(MainActivity.this,s,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onError(String s) {
        Toast.makeText(MainActivity.this,s,Toast.LENGTH_LONG).show();

    }

    @Override
    public void onMessage(String s) {
        Toast.makeText(MainActivity.this,s,Toast.LENGTH_LONG).show();
    }

    @Override
    public void printingOrderSentSuccessfully() {
        Toast.makeText(MainActivity.this,"order sent to printer",Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ScanningActivity.SCANNING_FOR_PRINTER){
            initprinter();
        }
        ChangePairAndUnpair();
    }

    private void initprinter() {
        if (Printooth.INSTANCE.hasPairedPrinter()){
            printing = Printooth.INSTANCE.printer();
        }
        if(printing !=null){
            printing.setPrintingCallback(this);
        }
    }
    public static Bitmap toGrayscale(Bitmap srcImage) {

        Bitmap bmpGrayscale = Bitmap.createBitmap(srcImage.getWidth(),
                srcImage.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bmpGrayscale);
        Paint paint = new Paint();

        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(srcImage, 0, 0, paint);
        return bmpGrayscale;
    }

}
