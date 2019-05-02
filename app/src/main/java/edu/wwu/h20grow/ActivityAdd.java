package edu.wwu.h20grow;

import android.Manifest;
import android.app.usage.UsageEvents;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.CalendarContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;






import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import java.io.File;
import java.io.IOError;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by carpend3 on 10/25/18.
 */

public class ActivityAdd extends AppCompatActivity{

    private Button takePicButton;
    private Button importPicButton;
    private Button addButton;
    private Button deleteButton;
    private boolean hasChanged = false;
    private ImageView imageView;
    private String currPhotoPath;
    private static final int REQUEST_CAMERA = 1;
    private static final int PICK_IMAGE = 2;


    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("Scientific");
    private ArrayList<String> sciName;
    private ArrayList<String> water;
    private ArrayList<String> common;
    ArrayAdapter<String> adapter;
    ArrayAdapter<String> adapterCom;

    private DatabaseManager db;

    Context context;



        @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_add);
        sciName = new ArrayList<>();
        common = new ArrayList<>();
        water = new ArrayList<>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, sciName);
        adapterCom = new ArrayAdapter<String> (this, android.R.layout.simple_dropdown_item_1line, common);
        TextChangeHandler tch = new TextChangeHandler();
        AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.editSci);
        AutoCompleteTextView textViewCom = (AutoCompleteTextView) findViewById(R.id.editCommonName);
        textView.addTextChangedListener(tch);
        textViewCom.addTextChangedListener(tch);
        textView.setAdapter(adapter);
        textViewCom.setAdapter(adapterCom);
        myRef.addListenerForSingleValueEvent(valueEventListener);
        takePicButton = (Button) findViewById(R.id.picButton);
        importPicButton = (Button) findViewById(R.id.galleryButton);
        imageView = (ImageView) findViewById(R.id.plantImage);



        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            takePicButton.setEnabled(false);
            importPicButton.setEnabled(false);
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }

        db = new DatabaseManager(this);

        if(getIntent().getExtras() != null){
            TextView title = findViewById(R.id.textTitle);
            title.setText("Edit Plant");
            Bundle bundle = getIntent().getExtras();
            int plantID = bundle.getInt("plantID");
            Plant myPlant = db.selectByID(plantID);

            EditText plantNameET = (EditText) findViewById(R.id.editName);
            System.out.println(myPlant.getWaterInterval());
            EditText waterIntervalET = (EditText) findViewById(R.id.editWater);
            addButton = (Button) findViewById(R.id.add_update);
            addButton.setText("UPDATE");
            deleteButton = (Button) findViewById(R.id.delete_button);
            deleteButton.setVisibility(View.VISIBLE);

            File imgFile = new File(myPlant.getImgPath());
            if(imgFile.exists()){
                Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                bitmap = checkRotate(bitmap, imgFile.getAbsolutePath());
                imageView.setImageBitmap(bitmap);
            }else{
                imageView.setImageResource(R.drawable.temp_plant);
            }



            plantNameET.setText(myPlant.getPlantName());
            textView.setText(myPlant.getSciName());
            textViewCom.setText(myPlant.getCategory());

            waterIntervalET.setText(Integer.toString((myPlant.getWaterInterval()/24)));
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    protected void onPause(){
        super.onPause();
    }



        public void openGallery(View v){
            Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(gallery, PICK_IMAGE);
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
            if(requestCode == 0){
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                    takePicButton.setEnabled(true);
                    importPicButton.setEnabled(true);
                }
            }

        }


    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            sciName.clear();
            common.clear();
            //water.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot sci : dataSnapshot.getChildren()) {
                        for (DataSnapshot snapshot : sci.getChildren()) {
                            for (DataSnapshot snap : snapshot.getChildren()) {
                                if (snap.getKey().equals("sci")) {
                                        String plant = (String) snap.getValue();
                                        sciName.add(plant);
                                    }
                                else if (snap.getKey().equals("common")){
                                    String plant = (String) snap.getValue();
                                    common.add(plant);
                                }
                                else if(snap.getKey().equals("water")){
                                    String waterInt =  snap.getValue().toString();
                                    water.add(waterInt);
                                }

                            }
                            }
                    }
                    adapter.notifyDataSetChanged();
                    adapterCom.notifyDataSetChanged();
                }

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
    public void addPic(View v){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if(intent.resolveActivity(getPackageManager())!= null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex){
                Toast.makeText(this,"Error accessing camera",Toast.LENGTH_SHORT).show();
            }
            if(photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(intent, REQUEST_CAMERA);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
            if(resultCode == RESULT_OK){
                switch (requestCode){
                    case REQUEST_CAMERA:
                        File imgFile = new File(currPhotoPath);
                        if(imgFile.exists()){
                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inSampleSize = 8;
                            Bitmap b = BitmapFactory.decodeFile(currPhotoPath, options);
                            b = checkRotate(b, currPhotoPath);
                            imageView.setImageBitmap(b);
                        }
                        break;
                    case PICK_IMAGE:
                        Uri imageUri = data.getData();
                        currPhotoPath = getRealPath(imageUri);
                        Bitmap thisImage = BitmapFactory.decodeFile(currPhotoPath);
                        thisImage = checkRotate(thisImage, currPhotoPath);
                        imageView.setImageBitmap(thisImage);
                        break;
                }
            }

    }



    public String getRealPath(Uri imgURI){
        String result;
        Cursor cursor = getContentResolver().query(imgURI, null, null, null, null);
        if(cursor == null){
            result = imgURI.getPath();
        }else{
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(index);
            cursor.close();
        }
        return result;
    }

    public Bitmap checkRotate(Bitmap image, String path){
        Bitmap b = null;
        try{
            ExifInterface ex = new ExifInterface(path);
            int orient = ex.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

            switch (orient){
                case ExifInterface.ORIENTATION_ROTATE_90:
                    b = rotateImage(image, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    b = rotateImage(image, 180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    b = rotateImage(image, 270);
                    break;
                default:
                    b = image;
            }
        }catch (IOException e){
            Toast.makeText(this, "Error opening image", Toast.LENGTH_SHORT).show();
        }
        return b;
    }

    public Bitmap rotateImage(Bitmap image, int orient){
        Matrix matrix = new Matrix();
        matrix.postRotate(orient);

        return Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), matrix, true);
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        currPhotoPath = image.getAbsolutePath();
        return image;
    }



    public void addPlant(View v){
        EditText plantNameET = (EditText) findViewById(R.id.editName);
        EditText sciNameET = (EditText) findViewById(R.id.editSci);
        EditText waterIntervalET = (EditText) findViewById(R.id.editWater);
        EditText commNameET = (EditText) findViewById(R.id.editCommonName);
        //ImageView plantImg = (ImageView) findViewById(R.id.plantImage);

        String plantName = plantNameET.getText().toString();
        String sciName = sciNameET.getText().toString();
        String commName = commNameET.getText().toString();
        String waterString = waterIntervalET.getText().toString();
        if(waterString.equals("0") || waterString.equals("00")){
            Toast.makeText(this,"Invalid water interval", Toast.LENGTH_LONG).show();
        }else if(plantName.matches("") || waterString.matches("")){
            Toast.makeText(this,"Missing required fields", Toast.LENGTH_LONG).show();
        }else {
            //Update plant
            if(getIntent().getExtras() != null) {
                Bundle bundle = getIntent().getExtras();
                int plantID = bundle.getInt("plantID");
                Plant myPlant = db.selectByID(plantID);
                myPlant.setPlantName(plantNameET.getText().toString());
                myPlant.setSciName(sciNameET.getText().toString());
                myPlant.setCategory(commNameET.getText().toString());
                myPlant.setWaterInterval(Integer.parseInt(waterIntervalET.getText().toString())*24);
                if(currPhotoPath != null) {
                    myPlant.setImgPath(currPhotoPath);
                }
                db.updateById(myPlant.getId(), myPlant.getPlantName(), myPlant.getSciName(), myPlant.getCategory(),
                        myPlant.getWaterInterval(), myPlant.getLastWaterTime(), myPlant.getImgPath());
                Toast.makeText(this, "Plant Updated", Toast.LENGTH_LONG).show();
                this.finish();

            }
            //Add plant
            else{

                int waterInterval = Integer.parseInt(waterString) * 24;
                long currTime = Calendar.getInstance().getTimeInMillis();
                Plant plant = new Plant(0, plantName, sciName, commName, waterInterval, currTime, currPhotoPath);
                db.insert(plant);
                Toast.makeText(this, "Added plant", Toast.LENGTH_LONG).show();

                plantNameET.setText("");
                sciNameET.setText("");
                commNameET.setText("");
                waterIntervalET.setText("");
                imageView.setImageResource(R.drawable.temp_plant);
                currPhotoPath = "";
                TextView waterSugg = (TextView) findViewById(R.id.newWaterSuggest);
                waterSugg.setText("");
                if(MainActivity.user_settings.isCalendarEv()) {
                    addCalenderEvent(waterInterval, plantName);
                }
            }
            this.finish();
        }
    }

    public void deletePlant(View v){
        if(getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            int plantID = bundle.getInt("plantID");
            if(MainActivity.user_settings.isDeleteConfrim()){
                deleteConfirm(plantID);

            }else{
                db.deleteById(plantID);
                Toast.makeText(this, "Plant Deleted", Toast.LENGTH_LONG).show();
                this.finish();
            }


        }
        else{
            System.err.println("ERROR: in delete plant without plant ID");
        }
    }
    private void autoFill(int after){
        if(!hasChanged) {
            EditText sciText = (EditText) findViewById(R.id.editSci);
            EditText commonName = (EditText) findViewById(R.id.editCommonName);
            EditText editWater= (EditText) findViewById(R.id.editWater);
            TextView waterText = (TextView) findViewById(R.id.newWaterSuggest);

            String sci = sciText.getText().toString();
            String com = commonName.getText().toString();
            int i = 0;
            int sciLen = sci.length();
            int comLen = com.length();
            if (sciLen > 5 && sciLen == after) {
                for (String s : sciName) {
                    if (s.equalsIgnoreCase(sci)) {
                        hasChanged = true;
                        commonName.setText(common.get(i));
                        String waterInterval = water.get(i);
                        waterInterval = waterInterval.trim();
                        editWater.setText(waterInterval);
                        waterText.setText(water.get(i)+" days");
                    }
                    i++;
                }
            } else if (comLen > 5 && comLen == after) {
                for (String s : common) {
                    if (s.equalsIgnoreCase(com)) {
                        hasChanged = true;
                        sciText.setText(sciName.get(i));
                        String waterInterval = water.get(i);
                        waterInterval = waterInterval.trim();
                        editWater.setText(waterInterval);
                        waterText.setText(water.get(i)+" days");
                    }
                    i++;
                }
            }
        }else hasChanged = false;
    }
    private class TextChangeHandler implements TextWatcher {
        public void afterTextChanged(Editable e){}
        public void beforeTextChanged(CharSequence s,int start,int count,int after){}
        public void onTextChanged(CharSequence s,int start,int before,int after){
            if(before < after && !hasChanged) {
                autoFill(after);
                hasChanged = false;
            }
        }
    }

    public void goBack(View v){
        this.finish();
    }

    private void deleteConfirm(final int plantID){
        final Plant plant = db.selectByID(plantID);
        RelativeLayout RelativeLayout1 = findViewById(R.id.relative_layout_add);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.confirm_layout, null);

        Button cancelButton = customView.findViewById(R.id.popup_cancel);
        Button confirmButton = customView.findViewById(R.id.popup_confirm);

        final PopupWindow popupWindow = new PopupWindow(customView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.showAtLocation(RelativeLayout1, Gravity.CENTER, 0, 0);

        cancelButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                popupWindow.dismiss();
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.deleteById(plant.getId());
                Toast.makeText(context,plant.getPlantName() + " Has Been Deleted",Toast.LENGTH_SHORT).show();
                finish();
                popupWindow.dismiss();
            }
        });
    }


    private void addCalenderEvent(int waterInterval, String name){
        SimpleDateFormat curFormat = new SimpleDateFormat("yyyy/MM/dd");
        Calendar cal = Calendar.getInstance();
        Date event;
        cal.add(Calendar.DAY_OF_MONTH,waterInterval/24);
        event = cal.getTime();
        String dateFormatted = curFormat.format(event);
        String[] date = dateFormatted.split("/");
        int[] eventArgs = new int[3];
        for(int i = 0 ; i< 3;i++){
            try {
                eventArgs[i] = Integer.parseInt(date[i]);

            }catch (NumberFormatException e){

            }
        }
        Calendar startTime = Calendar.getInstance();
        startTime.set(eventArgs[0],eventArgs[1]-1,eventArgs[2],7,30);
        Calendar endTime = Calendar.getInstance();
        endTime.set(eventArgs[0],eventArgs[1]-1,eventArgs[2],7,35);
        Intent intent = new Intent(Intent.ACTION_EDIT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,startTime.getTimeInMillis())
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME,endTime.getTimeInMillis())
                .putExtra(CalendarContract.Events.TITLE, "Water " +name)
                .putExtra(CalendarContract.Events.DESCRIPTION, name)
                .putExtra(CalendarContract.Events.EVENT_LOCATION,"")
                .putExtra(CalendarContract.Events.AVAILABILITY, 0)
                .putExtra(CalendarContract.Events.RRULE,waterInterval);
        startActivity(intent);
    }
}

