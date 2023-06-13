package com.keerthi77459.facerecognition;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import java.util.List;

public class LCOFaceDetection extends AppCompatActivity {

    Bitmap bitmap1;
    ImageView imageView1;
    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        imageView1  = findViewById(R.id.imageView1);


        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,12);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 12){

//            ugrfui


            Uri uri = data.getData();

            bitmap1 = (Bitmap) data.getExtras().get("data");
            imageView1.setImageBitmap(bitmap1);
//            imageView1.setImageURI(uri);
            detectface(bitmap1);
//            imageView1.setImageResource(R.drawable.camera);
        }
    }

    private void detectface(Bitmap bitmap1) {
        FaceDetectorOptions highAccuracyOpts =
                new FaceDetectorOptions.Builder()
                        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                        .build();
        InputImage image = InputImage.fromBitmap(bitmap1,0);

        FaceDetector detector = FaceDetection.getClient(highAccuracyOpts);

        Task<List<Face>> result =
                detector.process(image)
                        .addOnSuccessListener(
                                new OnSuccessListener<List<Face>>() {
                                    @Override
                                    public void onSuccess(List<Face> faces) {
                                        String result1 ="";
                                        String smile;
                                        int i = 1;
                                        Log.d("MSG:", "Success");
                                        for (Face face : faces) {
                                            if(face!=null) {
                                                Log.d("MSG:", "Success");
                                                System.out.println(face.getSmilingProbability());
                                                smile = "";
                                                float smile_prob = face.getSmilingProbability() * 100;
                                                float left_prob = face.getLeftEyeOpenProbability() * 100;
                                                float right_prob = face.getRightEyeOpenProbability() * 100;
                                                if (smile_prob > 75) {
                                                    smile = "Keep Your Smile";
                                                } else if (smile_prob > 45 && smile_prob < 74) {
                                                    smile = "Smile More";
                                                } else {
                                                    smile = "What happened! Smile";
                                                }
                                                result1 = result1
                                                        .concat("\n"+ "Face " + i + "\n\n")
                                                        .concat("Smile = " + String.format("%.2f", smile_prob) + "%" + "\n")
                                                        .concat("Left Eye = " + String.format("%.2f", left_prob) + "%" + "\n")
                                                        .concat("Right Eye = " + String.format("%.2f", right_prob) + "%" + "\n")
                                                        .concat("\n" + smile + "\n");
                                                System.out.println(result1);
                                                i++;
                                            } else {
                                                result1 = result1.concat("No Face Detected");
                                            }
                                        }
                                        buildDialog(result1);
                                        dialog.setCanceledOnTouchOutside(false);
                                        dialog.show();
//                                        imageView1.setImageResource(R.drawable.camera);
                                    }
                                })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        e.printStackTrace();
                                    }
                                });
    }

    private void buildDialog(String aa) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.fragement_resultdialogue,null);

        TextView aaa = view.findViewById(R.id.result);
        aaa.setText(aa);
        builder.setView(view);
        builder.setTitle("Detection Report")
                .setPositiveButton("Reset", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        imageView1.setImageResource(R.drawable.camera);

                    }
                });
//        imageView1.setImageResource(R.drawable.camera);
        dialog = builder.create();
    }
}
