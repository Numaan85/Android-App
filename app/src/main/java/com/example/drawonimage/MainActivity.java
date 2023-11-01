package com.example.drawonimage;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;



public class MainActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private Button selectImageButton;
    private Button addTextButton;
    private Button shareButton;
    private ImageView imageView;
    private EditText textInput;
    private Bitmap selectedImageBitmap;
    private Bitmap imageWithText;
    private PointF textPosition = new PointF(100, 100);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        selectImageButton = findViewById(R.id.selectImageButton);
        addTextButton = findViewById(R.id.addTextButton);
        shareButton = findViewById(R.id.shareButton);
        imageView = findViewById(R.id.imageView);
        textInput = findViewById(R.id.textInput);

        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        addTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTextToImage();
            }
        });
    }
     shareButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            shareImageWithText();
        }
    });

    // Set up touch event handling for adjusting text position
        imageView.setOnTouchListener(new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            textPosition.set(event.getX(), event.getY());
            imageView.invalidate(); // Redraw the view
            return true;
        }
    });
}



    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
    }

    private void addTextToImage() {
        String text = textInput.getText().toString();
        if (!text.isEmpty() && selectedImageBitmap != null) {
            // Create a copy of the selected image bitmap
            Bitmap imageWithText = selectedImageBitmap.copy(Bitmap.Config.ARGB_8888, true);

            // Create a Canvas and Paint to draw text on the image
            Canvas canvas = new Canvas(imageWithText);
            Paint paint = new Paint();
            paint.setColor(Color.WHITE);
            paint.setTextSize(50); // Adjust text size as needed
            paint.setAntiAlias(true);

            // Calculate the position for the text (adjust coordinates as needed)
            int x = 100;
            int y = 100;

            // Draw the text on the image
            canvas.drawText(text, x, y, paint);

            // Update the ImageView with the image containing the added text
            imageView.setImageBitmap(imageWithText);
        }
    }
    private void shareImageWithText() {
        if (selectedImageBitmap != null) {
            // Create a temporary file to store the image with added text
            File file = createTempImageFile();
            if (file != null) {
                try {
                    FileOutputStream fos = new FileOutputStream(file);
                    selectedImageBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    fos.flush();
                    fos.close();

                    // Create an intent to share the image
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("image/*");
                    shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                    shareIntent.putExtra(Intent.EXTRA_TEXT, textInput.getText().toString());

                    // Start the sharing activity
                    startActivity(Intent.createChooser(shareIntent, "Share Image"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();

            try {
                selectedImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                imageWithText = Bitmap.createBitmap(selectedImageBitmap);
                imageView.setImageBitmap(imageWithText);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        // Redraw the view when the app resumes
        imageView.invalidate();
}


    private File createTempImageFile() {
        try {
            File dir = new File(Environment.getExternalStorageDirectory(), "YourAppFolder");
            if (!dir.exists()) {
                dir.mkdirs();
            }

            return File.createTempFile("temp_image", ".png", dir);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();

            try {
                selectedImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                imageView.setImageBitmap(selectedImageBitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}