//
// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license.
//
// Microsoft Cognitive Services (formerly Project Oxford): https://www.microsoft.com/cognitive-services
//
// Microsoft Cognitive Services (formerly Project Oxford) GitHub:
// https://github.com/Microsoft/Cognitive-Emotion-Android
//
// Copyright (c) Microsoft Corporation
// All rights reserved.
//
// MIT License:
// Permission is hereby granted, free of charge, to any person obtaining
// a copy of this software and associated documentation files (the
// "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to
// permit persons to whom the Software is furnished to do so, subject to
// the following conditions:
//
// The above copyright notice and this permission notice shall be
// included in all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED ""AS IS"", WITHOUT WARRANTY OF ANY KIND,
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
// NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
// LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
// OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
// WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
//
package com.microsoft.projectoxford.emotionsample;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.microsoft.projectoxford.emotion.EmotionServiceClient;
import com.microsoft.projectoxford.emotion.EmotionServiceRestClient;
import com.microsoft.projectoxford.emotion.contract.FaceRectangle;
import com.microsoft.projectoxford.emotion.contract.RecognizeResult;
import com.microsoft.projectoxford.emotion.rest.EmotionServiceException;
import com.microsoft.projectoxford.emotionsample.helper.ImageHelper;

import com.microsoft.projectoxford.face.FaceServiceRestClient;
import com.microsoft.projectoxford.face.contract.Face;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Random;

public class RecognizeActivity extends ActionBarActivity {

    // Flag to indicate which task is to be performed.
    private static final int REQUEST_SELECT_IMAGE = 0;
    private static final int REQUEST_SELECT_IMAGE2 = 1;

    // The button to select an image
    private Button mButtonSelectImage;
    private Button mButtonSelectImage2;

    // The URI of the image selected to detect.
    private Uri mImageUri;
    private Uri mImageUri2;

    private boolean taskflag = true;
    private boolean firstflag = true;
   // private boolean leftfirstpic_flag= true;
   // private boolean rightfirstpic_flag= true;

    // The image selected to detect.
    private Bitmap mBitmap;
    private Bitmap mBitmap2;

    // The edit to show status and result.
    private TextView mEditText;
    private TextView mEditText2;

    private int question_emotion = 0;
    private TextView qeustion_text;

    private EmotionServiceClient client;

    private TextView winner;
    private TextView p1_notification;
    private TextView p2_notification;
    private double scoreA = 0.0;
    private double scoreB = 0.0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognize);

        if (client == null) {
            client = new EmotionServiceRestClient(getString(R.string.subscription_key));
        }

        qeustion_text = (TextView) findViewById(R.id.questiontext);
        mButtonSelectImage = (Button) findViewById(R.id.buttonSelectImage);
        mButtonSelectImage2 = (Button) findViewById(R.id.buttonSelectImage2);
        mEditText = (TextView) findViewById(R.id.editTextResult);
        mEditText2 = (TextView) findViewById(R.id.editTextResult2);
        winner = (TextView) findViewById(R.id.winner);
        p1_notification = (TextView) findViewById(R.id.notificationp1);
        p2_notification = (TextView) findViewById(R.id.notificationp2);
        mButtonSelectImage2.setEnabled(false);
        p1_notification.setText("Player 1 , \nit's your turn !");
        p2_notification.setText("Please wait \nplayer 1...");
        p1_notification.setTextColor(Color.rgb(255, 0, 0));
        p2_notification.setTextColor(Color.rgb(0, 0, 0));

        Random rand = new Random();
        question_emotion = rand.nextInt(8); //0-7

        if(question_emotion == 0)
            qeustion_text.setText("Q：生氣(Anger)");
        else if(question_emotion == 1)
            qeustion_text.setText("Q：輕視(Contempt)");
        else if(question_emotion == 2)
            qeustion_text.setText("Q：厭惡(Disgust)");
        else if(question_emotion == 3)
            qeustion_text.setText("Q：害怕(Fear)");
        else if(question_emotion == 4)
            qeustion_text.setText("Q：開心(Happiness)");
        else if(question_emotion == 5)
            qeustion_text.setText("Q：無情緒(Neutral)");
        else if(question_emotion == 6)
            qeustion_text.setText("Q：傷心(Sadness)");
        else if(question_emotion == 7)
            qeustion_text.setText("Q：驚訝(Surprise)");
        else
            qeustion_text.setText("Q：error");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_recognize, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar wills
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.random_question)
        {

            Random rand = new Random();
            question_emotion = rand.nextInt(8); //0-7

            if (question_emotion == 0)
                qeustion_text.setText("Q：生氣(Anger)");
            else if (question_emotion == 1)
                qeustion_text.setText("Q：輕視(Contempt)");
            else if (question_emotion == 2)
                qeustion_text.setText("Q：厭惡(Disgust)");
            else if (question_emotion == 3)
                qeustion_text.setText("Q：害怕(Fear)");
            else if (question_emotion == 4)
                qeustion_text.setText("Q：開心(Happiness)");
            else if (question_emotion == 5)
                qeustion_text.setText("Q：無情緒(Neutral)");
            else if (question_emotion == 6)
                qeustion_text.setText("Q：傷心(Sadness)");
            else if (question_emotion == 7)
                qeustion_text.setText("Q：驚訝(Surprise)");
            else
                qeustion_text.setText("Q：error");

            return true;
        }
        else if(id == R.id.anger)
        {
            question_emotion = 0;
            qeustion_text.setText("Q：生氣(Anger)");
            return true;
        }
        else if(id == R.id.contempt)
        {
            question_emotion = 1;
            qeustion_text.setText("Q：輕視(Contempt)");
            return true;
        }
        else if(id == R.id.disgust)
        {
            question_emotion = 2;
            qeustion_text.setText("Q：厭惡(Disgust)");
            return true;
        }
        else if(id == R.id.fear)
        {
            question_emotion = 3;
            qeustion_text.setText("Q：害怕(Fear)");
            return true;
        }
        else if(id == R.id.happiness)
        {
            question_emotion = 4;
            qeustion_text.setText("Q：開心(Happiness)");
            return true;
        }
        else if(id == R.id.neutral)
        {
            question_emotion = 5;
            qeustion_text.setText("Q：無情緒(Neutral)");
            return true;
        }
        else if(id == R.id.sadness)
        {
            question_emotion = 6;
            qeustion_text.setText("Q：傷心(Sadness)");
            return true;
        }
        else if(id == R.id.surprise)
        {
            question_emotion = 7;
            qeustion_text.setText("Q：驚訝(Surprise)");
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    public void doRecognize() {
        mButtonSelectImage.setEnabled(false);
        mButtonSelectImage2.setEnabled(false);

        // Do emotion detection using auto-detected faces.
        try {
            new doRequest(false).execute();
        } catch (Exception e) {
            mEditText.append("Error encountered. Exception is: " + e.toString());
        }

        String faceSubscriptionKey = getString(R.string.faceSubscription_key);
        if (faceSubscriptionKey.equalsIgnoreCase("Please_add_the_face_subscription_key_here")) {
            mEditText.append("\n\nThere is no face subscription key in res/values/strings.xml. Skip the sample for detecting emotions using face rectangles\n");
        } else {
            // Do emotion detection using face rectangles provided by Face API.
            try {
                new doRequest(true).execute();
            } catch (Exception e) {
                mEditText.append("Error encountered. Exception is: " + e.toString());
            }
        }
    }
    public void doRecognize2() {
        mButtonSelectImage.setEnabled(false);
        mButtonSelectImage2.setEnabled(false);

        // Do emotion detection using auto-detected faces.
        try {
            new doRequest(false).execute();
        } catch (Exception e) {
            mEditText.append("Error encountered. Exception is: " + e.toString());
        }

        String faceSubscriptionKey = getString(R.string.faceSubscription_key);
        if (faceSubscriptionKey.equalsIgnoreCase("Please_add_the_face_subscription_key_here")) {
            mEditText.append("\n\nThere is no face subscription key in res/values/strings.xml. Skip the sample for detecting emotions using face rectangles\n");
        } else {
            // Do emotion detection using face rectangles provided by Face API.
            try {
                new doRequest(true).execute();
            } catch (Exception e) {
                mEditText.append("Error encountered. Exception is: " + e.toString());
            }
        }
    }

    // Called when the "Select Image" button is clicked.
    public void selectImage(View view) {
        mEditText.setText("");
        taskflag = false;
        Intent intent;
        intent = new Intent(RecognizeActivity.this, com.microsoft.projectoxford.emotionsample.helper.SelectImageActivity.class);
        startActivityForResult(intent, REQUEST_SELECT_IMAGE);
    }
    public void selectImage2(View view) {
       /* if (firstflag == true)
        {
            taskflag = true;
            Intent intent2;
            intent2 = new Intent(RecognizeActivity.this, com.microsoft.projectoxford.emotionsample.helper.SelectImageActivity.class);
            //intent2 = null;
            startActivityForResult(intent2, REQUEST_SELECT_IMAGE2);
        }
        else*/
        {
            mEditText2.setText("");
            taskflag = true;
            Intent intent2;
            intent2 = new Intent(RecognizeActivity.this, com.microsoft.projectoxford.emotionsample.helper.SelectImageActivity.class);
            startActivityForResult(intent2, REQUEST_SELECT_IMAGE2);
        }
    }

    // Called when image selection is done.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("RecognizeActivity", "onActivityResult");
        switch (requestCode) {
            case REQUEST_SELECT_IMAGE:
                if (resultCode == RESULT_OK) {
                    // If image is selected successfully, set the image URI and bitmap.
                    mImageUri = data.getData();
                    //mImageUri2 = data.getData();

                    mBitmap = ImageHelper.loadSizeLimitedBitmapFromUri(
                            mImageUri, getContentResolver());
                    //mBitmap2 = ImageHelper.loadSizeLimitedBitmapFromUri(mImageUri2, getContentResolver());

                    if (mBitmap != null) {
                        // Show the image on screen.
                        ImageView imageView = (ImageView) findViewById(R.id.selectedImage);
                        imageView.setImageBitmap(mBitmap);

                        // Add detection log.
                        Log.d("RecognizeActivity", "Image: " + mImageUri + " resized to " + mBitmap.getWidth()
                                + "x" + mBitmap.getHeight());

                        doRecognize();
                    }

                }
                break;

           case REQUEST_SELECT_IMAGE2:
                if (resultCode == RESULT_OK) {
                    // If image is selected successfully, set the image URI and bitmap.
                    mImageUri2 = data.getData();

                    mBitmap2 = ImageHelper.loadSizeLimitedBitmapFromUri(
                            mImageUri2, getContentResolver());

                    if (mBitmap2 != null) {
                        // Show the image on screen.
                            ImageView imageView2 = (ImageView) findViewById(R.id.selectedImage2);
                            imageView2.setImageBitmap(mBitmap2);
                        // Add detection log.
                        Log.d("RecognizeActivity", "Image: " + mImageUri2 + " resized to " + mBitmap2.getWidth()
                                + "x" + mBitmap2.getHeight());

                        doRecognize2();
                    }
                }
                break;
            default:
                break;
        }
    }


    private List<RecognizeResult> processWithAutoFaceDetection() throws EmotionServiceException, IOException {
        Log.d("emotion", "Start emotion detection with auto-face detection");

        Gson gson = new Gson();
        // Put the image into an input stream for detection.
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        List<RecognizeResult> result = null;

        if(firstflag == true)
        {
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());
            result = this.client.recognizeImage(inputStream);
        }
        else if(taskflag == false)
        {
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());
            result = this.client.recognizeImage(inputStream);
        }
        else {
            mBitmap2.compress(Bitmap.CompressFormat.JPEG, 100, output);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());
            result = this.client.recognizeImage(inputStream);

        }

        return result;
    }

   private List<RecognizeResult> processWithFaceRectangles() throws EmotionServiceException, com.microsoft.projectoxford.face.rest.ClientException, IOException {
        Log.d("emotion", "Do emotion detection with known face rectangles");
        Gson gson = new Gson();

        // Put the image into an input stream for detection.
        ByteArrayOutputStream output = new ByteArrayOutputStream();
       List<RecognizeResult> result = null;

       if(firstflag == true)
       {
           mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
           ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());
           FaceRectangle[] faceRectangles = null;
           String faceSubscriptionKey = getString(R.string.faceSubscription_key);
           FaceServiceRestClient faceClient = new FaceServiceRestClient(faceSubscriptionKey);
           Face faces[] = faceClient.detect(inputStream, false, false, null);

           if (faces != null) {
               faceRectangles = new FaceRectangle[faces.length];

               for (int i = 0; i < faceRectangles.length; i++) {
                   // Face API and Emotion API have different FaceRectangle definition. Do the conversion.
                   com.microsoft.projectoxford.face.contract.FaceRectangle rect = faces[i].faceRectangle;
                   faceRectangles[i] = new com.microsoft.projectoxford.emotion.contract.FaceRectangle(rect.left, rect.top, rect.width, rect.height);
               }
           }


           if (faceRectangles != null) {
               inputStream.reset();

               result = this.client.recognizeImage(inputStream, faceRectangles);

               String json = gson.toJson(result);
           }
       }
       else if(taskflag == false)
       {
           mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
           ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());
           FaceRectangle[] faceRectangles = null;
           String faceSubscriptionKey = getString(R.string.faceSubscription_key);
           FaceServiceRestClient faceClient = new FaceServiceRestClient(faceSubscriptionKey);
           Face faces[] = faceClient.detect(inputStream, false, false, null);

           if (faces != null) {
               faceRectangles = new FaceRectangle[faces.length];

               for (int i = 0; i < faceRectangles.length; i++) {
                   // Face API and Emotion API have different FaceRectangle definition. Do the conversion.
                   com.microsoft.projectoxford.face.contract.FaceRectangle rect = faces[i].faceRectangle;
                   faceRectangles[i] = new com.microsoft.projectoxford.emotion.contract.FaceRectangle(rect.left, rect.top, rect.width, rect.height);
               }
           }


           if (faceRectangles != null) {
               inputStream.reset();

               result = this.client.recognizeImage(inputStream, faceRectangles);

               String json = gson.toJson(result);
           }
       }
       else
       {
           mBitmap2.compress(Bitmap.CompressFormat.JPEG, 100, output);
           ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());
           FaceRectangle[] faceRectangles = null;
           String faceSubscriptionKey = getString(R.string.faceSubscription_key);
           FaceServiceRestClient faceClient = new FaceServiceRestClient(faceSubscriptionKey);
           Face faces[] = faceClient.detect(inputStream, false, false, null);

           if (faces != null) {
               faceRectangles = new FaceRectangle[faces.length];

               for (int i = 0; i < faceRectangles.length; i++) {
                   // Face API and Emotion API have different FaceRectangle definition. Do the conversion.
                   com.microsoft.projectoxford.face.contract.FaceRectangle rect = faces[i].faceRectangle;
                   faceRectangles[i] = new com.microsoft.projectoxford.emotion.contract.FaceRectangle(rect.left, rect.top, rect.width, rect.height);
               }
           }


           if (faceRectangles != null) {
               inputStream.reset();

               result = this.client.recognizeImage(inputStream, faceRectangles);

               String json = gson.toJson(result);
           }
       }

        return result;
    }

    private class doRequest extends AsyncTask<String, String, List<RecognizeResult>> {
        // Store error message
        private Exception e = null;
        private boolean useFaceRectangles = false;

        public doRequest(boolean useFaceRectangles) {this.useFaceRectangles = useFaceRectangles;
        }

        @Override
        protected List<RecognizeResult> doInBackground(String... args) {
            if (this.useFaceRectangles == false) {
                try {
                    return processWithAutoFaceDetection();
                } catch (Exception e) {
                    this.e = e;    // Store error
                }
            } else {
                try {
                    return processWithFaceRectangles();
                } catch (Exception e) {
                    this.e = e;    // Store error
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<RecognizeResult> result) {
            super.onPostExecute(result);
            // Display based on error existence

            if (this.useFaceRectangles == false) {
                //mEditText.append("\n\nRecognizing emotions with auto-detected face rectangles...\n");
            } //else {
            //mEditText.append("\n\nRecognizing emotions with existing face rectangles from Face API...\n");
            // }
            if (e != null) {
                if (this.useFaceRectangles == false)
                {
                mEditText.setText("Error: " + e.getMessage());
                mEditText2.setText("Error: " + e.getMessage());
                }
                //mEditText.setText("Please put your pic here!!!");
                //mEditText2.setText("Initialization finished!");
                //mButtonSelectImage.setEnabled(true);
                //mButtonSelectImage2.setEnabled(false);
                //this.e = null;
                //firstflag = false;
            }
            else {
                if (result.size() == 0) {
                    if(firstflag==true)
                    {
                        mEditText.append("No emotion detected :(\nTry again!");
                        mButtonSelectImage.setEnabled(true);
                        mButtonSelectImage2.setEnabled(false);
                    }
                    else if(taskflag==true)
                    {
                        mEditText2.append("No emotion detected :(\nTry again!");
                        mButtonSelectImage2.setEnabled(true);
                        mButtonSelectImage.setEnabled(false);

                    }
                    else
                    {
                        mEditText.append("No emotion detected :(\nTry again!");
                        mButtonSelectImage.setEnabled(true);
                        mButtonSelectImage2.setEnabled(false);
                    }
                } else {
                    Integer count = 0;
                    // Covert bitmap to a mutable bitmap by copying it
                    if(firstflag == true)
                    {
                        Bitmap bitmapCopy = mBitmap.copy(Bitmap.Config.ARGB_8888, true);
                        Canvas faceCanvas = new Canvas(bitmapCopy);
                        faceCanvas.drawBitmap(mBitmap, 0, 0, null);
                    }
                    else if(taskflag==true)
                    {
                        Bitmap bitmapCopy = mBitmap.copy(Bitmap.Config.ARGB_8888, true);
                        Canvas faceCanvas = new Canvas(bitmapCopy);
                        faceCanvas.drawBitmap(mBitmap, 0, 0, null);
                    }
                    else
                    {
                        Bitmap bitmapCopy2 = mBitmap2.copy(Bitmap.Config.ARGB_8888, true);
                        Canvas faceCanvas2 = new Canvas(bitmapCopy2);
                        faceCanvas2.drawBitmap(mBitmap2, 0, 0, null);
                    }

                    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(5);
                    paint.setColor(Color.RED);

                    for (RecognizeResult r : result) {
                        if (this.useFaceRectangles == false) {
                            if(taskflag == true){
                                if(question_emotion == 0)
                                {
                                    mEditText2.append(String.format("Your score:\n"+"%1$.5f\n", r.scores.anger*100));
                                    scoreA = r.scores.anger*100;
                                }
                                else if(question_emotion == 1)
                                {
                                    mEditText2.append(String.format("Your score:\n"+"%1$.5f\n", r.scores.contempt*100));
                                    scoreA = r.scores.contempt*100;
                                }
                                else if(question_emotion == 2)
                                {
                                    mEditText2.append(String.format("Your score:\n"+"%1$.5f\n", r.scores.disgust*100));
                                    scoreA = r.scores.disgust*100;
                                }
                                else if(question_emotion == 3)
                                {
                                    mEditText2.append(String.format("Your score:\n"+"%1$.5f\n", r.scores.fear*100));
                                    scoreA = r.scores.fear*100;
                                }
                                else if(question_emotion == 4)
                                {
                                    mEditText2.append(String.format("Your score:\n"+"%1$.5f\n", r.scores.happiness*100));
                                    scoreA = r.scores.happiness*100;
                                }
                                else if(question_emotion == 5)
                                {
                                    mEditText2.append(String.format("Your score:\n"+"%1$.5f\n", r.scores.neutral*100));
                                    scoreA = r.scores.neutral*100;
                                }
                                else if(question_emotion == 6)
                                {
                                    mEditText2.append(String.format("Your score:\n"+"%1$.5f\n", r.scores.sadness*100));
                                    scoreA = r.scores.sadness*100;
                                }
                                else if(question_emotion == 7)
                                {
                                    mEditText2.append(String.format("Your score:\n"+"%1$.5f\n", r.scores.surprise*100));
                                    scoreA = r.scores.surprise*100;
                                }
                                else
                                {
                                    mEditText2.append(String.format("Your score: error\n"));
                                    scoreA = 0;
                                }
                                p1_notification.setText("Player 1 , \nit's your turn !");
                                p2_notification.setText("Please wait \nplayer 1...");
                                p1_notification.setTextColor(Color.rgb(255, 0, 0));
                                p2_notification.setTextColor(Color.rgb(0, 0, 0));
                                mButtonSelectImage.setEnabled(true);
                                mButtonSelectImage2.setEnabled(false);
                            }
                            else{
                                if(question_emotion == 0)
                                {
                                    mEditText.append(String.format("Your score:\n"+"%1$.5f\n", r.scores.anger*100));
                                    scoreB = r.scores.anger*100;
                                }
                                else if(question_emotion == 1)
                                {
                                    mEditText.append(String.format("Your score:\n"+"%1$.5f\n", r.scores.contempt*100));
                                    scoreB = r.scores.contempt*100;
                                }
                                else if(question_emotion == 2)
                                {
                                    mEditText.append(String.format("Your score:\n"+"%1$.5f\n", r.scores.disgust*100));
                                    scoreB = r.scores.disgust*100;
                                }
                                else if(question_emotion == 3)
                                {
                                    mEditText.append(String.format("Your score:\n"+"%1$.5f\n", r.scores.fear*100));
                                    scoreB = r.scores.fear*100;
                                }
                                else if(question_emotion == 4)
                                {
                                    mEditText.append(String.format("Your score:\n"+"%1$.5f\n", r.scores.happiness*100));
                                    scoreB = r.scores.happiness*100;
                                }
                                else if(question_emotion == 5)
                                {
                                    mEditText.append(String.format("Your score:\n"+"%1$.5f\n", r.scores.neutral*100));
                                    scoreB = r.scores.neutral*100;
                                }
                                else if(question_emotion == 6)
                                {
                                    mEditText.append(String.format("Your score:\n"+"%1$.5f\n", r.scores.sadness*100));
                                    scoreB = r.scores.sadness*100;
                                }
                                else if(question_emotion == 7)
                                {
                                    mEditText.append(String.format("Your score:\n"+"%1$.5f\n", r.scores.surprise*100));
                                    scoreB = r.scores.surprise*100;
                                }
                                else
                                {
                                    mEditText.append(String.format("Your score: error\n"));
                                    scoreB = 0;
                                }
                                firstflag = false;
                                p1_notification.setText("Please wait \nplayer 2...");
                                p2_notification.setText("Player 2 , \nit's your turn !");
                                p2_notification.setTextColor(Color.rgb(255, 0, 0));
                                p1_notification.setTextColor(Color.rgb(0, 0, 0));
                                mButtonSelectImage.setEnabled(false);
                                mButtonSelectImage2.setEnabled(true);
                            }
                        }
                        count++;
                    }
                    ImageView imageView = (ImageView) findViewById(R.id.selectedImage);
                    imageView.setImageDrawable(new BitmapDrawable(getResources(), mBitmap));

                    //ImageView imageView2 = (ImageView) findViewById(R.id.selectedImage2);
                    //imageView.setImageDrawable(new BitmapDrawable(getResources(), mBitmap2));
                }
                //mEditText.setSelection(0);
                //mEditText2.setSelection(0);
            }

            if( scoreA < scoreB && scoreA != 0 && scoreB != 0)
            {
                winner.setText("Player 1 win !!!");
            }
            else if( scoreA > scoreB && scoreA != 0 && scoreB != 0)
            {
                winner.setText("Player 2 win !!!");
            }
            else if( scoreA == scoreB && scoreA != 0 && scoreB != 0)
            {
                winner.setText(" Tie  !!!");
            }
            else
            {

            }
        }
}
}
