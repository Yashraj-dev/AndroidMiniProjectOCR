package com.example.opticalcharacterrecognitionapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Home extends AppCompatActivity {
    private Button captureBtn, historyBtn;
    private Bitmap bitmap;
    private static final int REQUEST_CAMERA_CODE = 100;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        captureBtn = findViewById(R.id.captureBtn);
        historyBtn = findViewById(R.id.historyBtn);
        try {
            Bundle bundle = getIntent().getBundleExtra("data");
            username = bundle.getString("Username");
        }catch (Exception e){
            Log.e("myTag", "" + e);
        }

        captureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                captureImage();
            }
        });

        historyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent historyIntent = new Intent(Home.this, SavedDetailsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("Username", username);
                historyIntent.putExtra("data", bundle);
                startActivity(historyIntent);
            }
        });
    }

    private void captureImage() {
        CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(Home.this);
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                    detectText(bitmap);
                } catch (IOException e) {
                    Toast.makeText(Home.this, "Error loading cropped image", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(Home.this, "Crop error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.i("resultCode", String.valueOf(resultCode));
        }
    }

    private void detectText(Bitmap bitmap) {
        InputImage image = InputImage.fromBitmap(bitmap, 0);
        TextRecognizerOptions options = new TextRecognizerOptions.Builder().build();
        TextRecognizer recognizer = TextRecognition.getClient(options);

        Task<Text> result = recognizer.process(image).addOnSuccessListener(new OnSuccessListener<Text>() {
            @Override
            public void onSuccess(@NonNull Text text) {
                String blockText = "";
                String tempBlockText = "";
                StringBuilder result = new StringBuilder();
                try {
                    for (Text.TextBlock block : text.getTextBlocks()) {
                        blockText = block.getText();
                        Point[] blockCornerPoint = block.getCornerPoints();
                        Rect blockFrame = block.getBoundingBox();

                        for (Text.Line line : block.getLines()) {
                            String lineText = line.getText();
                            Point[] linearCornerPoint = line.getCornerPoints();
                            Rect linRect = line.getBoundingBox();
                            for (Text.Element element : line.getElements()) {
                                String elementText = element.getText();
                                result.append(elementText);
                            }
                            result.append("\n");
                        }
                        tempBlockText = tempBlockText + blockText + "\n";
                        result.append("\n");
                    }
                    Intent myIntent = new Intent(Home.this, ResultActivity.class);
                    myIntent.putExtra("recognizedText", tempBlockText);

                    ByteArrayOutputStream _bs = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 20, _bs);
                    myIntent.putExtra("byteArray", _bs.toByteArray());
                    startActivity(myIntent);

                    myIntent.putExtra("capturedImage", bitmap);
                    startActivity(myIntent);
                } catch (Exception e) {
                    Log.e("myTag", "" + e );
//                    Toast.makeText(Home.this, "Failed to detect text from Image", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Home.this, "Failed to detect text from Image", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
