package com.example.nareshviriyala.farmifyagentfarmer.Fragments;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nareshviriyala.farmifyagentfarmer.Activities.HomeActivity;
import com.example.nareshviriyala.farmifyagentfarmer.Adapters.AdapterImageInformation;
import com.example.nareshviriyala.farmifyagentfarmer.Dialogs.DialogAddPartnerItem;
import com.example.nareshviriyala.farmifyagentfarmer.Dialogs.DialogExpandPicture;
import com.example.nareshviriyala.farmifyagentfarmer.Helpers.Convertor;
import com.example.nareshviriyala.farmifyagentfarmer.Helpers.DatabaseHelper;
import com.example.nareshviriyala.farmifyagentfarmer.Helpers.LogErrors;
import com.example.nareshviriyala.farmifyagentfarmer.Models.ModelImageInformation;
import com.example.nareshviriyala.farmifyagentfarmer.Models.ModelImageParameters;
import com.example.nareshviriyala.farmifyagentfarmer.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FragmentAFImages extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {

    private View rootView;
    private LogErrors logErrors;
    private DatabaseHelper dbHelper;
    private String className;
    public JSONObject farmerImageData;
    private GridView gv_images;
    private Button btn_imagedatasave;
    private AdapterImageInformation adapterImageInformation;
    private String[] itemList;
    private Convertor convertor;
    private ImageView img_picturetile, img_leftpic, img_rightpic, img_deletepic;
    public FragmentAFImages(){}
    private String currentPictureType;
    private int currentPictureId = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_af_images, container, false);

        try{
            logErrors = LogErrors.getInstance(getActivity());
            className = new Object(){}.getClass().getEnclosingClass().getName();
            dbHelper = new DatabaseHelper(getActivity());
            //dbHelper.deleteParameter(getResources().getString(R.string.Images));
            String data = dbHelper.getParameter(getResources().getString(R.string.Images));
            itemList = getResources().getStringArray(R.array.image_types);
            convertor = new Convertor(getActivity());
            if(data.isEmpty() || data == null)
                farmerImageData = new JSONObject();
            else
                farmerImageData = new JSONObject(data);
            ((HomeActivity) getActivity()).setActionBarTitle("Images");

            gv_images = rootView.findViewById(R.id.gv_images);
            gv_images.setOnItemClickListener(this);

            img_picturetile = rootView.findViewById(R.id.img_picturetile);
            img_leftpic = rootView.findViewById(R.id.img_leftpic);
            img_rightpic = rootView.findViewById(R.id.img_rightpic);
            img_deletepic = rootView.findViewById(R.id.img_deletepic);

            img_picturetile.setOnClickListener(this);
            img_leftpic.setOnClickListener(this);
            img_rightpic.setOnClickListener(this);
            img_deletepic.setOnClickListener(this);

            btn_imagedatasave = rootView.findViewById(R.id.btn_imagedatasave);
            btn_imagedatasave.setOnClickListener(this);

            refreshImageListView();
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }
        return rootView;
    }

    public void refreshImageListView(){
        try{
            ArrayList<ModelImageInformation> imageDataList = new ArrayList<>();
            for(int i = 0; i < itemList.length; i++)
                imageDataList.add(new ModelImageInformation((i+1), itemList[i], getImageByteArray(itemList[i])));


            adapterImageInformation = new AdapterImageInformation(this.getContext(), imageDataList);
            gv_images.setAdapter(adapterImageInformation);
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }
    }

    public byte[] getImageByteArray(String pictureType){
        byte[] imageByteArray = null;
        try{
            pictureType = pictureType.replace(" ","");
            JSONArray imageJsonArray = null;
            if(farmerImageData.has(pictureType)) {
                imageJsonArray = farmerImageData.getJSONArray(pictureType);
                if(imageJsonArray.length() > 0)
                    imageByteArray = dbHelper.getImage(imageJsonArray.getInt(0));
            }
            else
                imageByteArray = null;
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }
        return imageByteArray;
    }

    @Override
    public void onClick(View v) {
        try{

            switch (v.getId()) {
                case R.id.btn_imagedatasave:
                    validatePartnerData();
                    break;
                case R.id.img_picturetile:
                    if(currentPictureId == -1)
                        loadFragment();
                    else {
                        JSONArray imagesArray = farmerImageData.getJSONArray(currentPictureType);
                        byte[] imgSource = dbHelper.getImage(imagesArray.getInt(currentPictureId));
                        new DialogExpandPicture(getActivity(), imgSource).show();
                    }
                    break;
                case R.id.img_leftpic:
                    loadPreviousPicture();
                    break;
                case R.id.img_rightpic:
                    loadNextPicture();
                    break;
                case R.id.img_deletepic:
                    if(currentPictureId != -1)
                        deleteImage();
                    break;
            }

        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }
    }

    public void deleteImage(){
        try{
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Are you sure?");
            builder.setCancelable(true);

            builder.setPositiveButton(
                    "Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            try {
                                JSONArray imageArray = farmerImageData.getJSONArray(currentPictureType);
                                JSONArray newImageArray = null;
                                if(imageArray.length() > 0){
                                    newImageArray = new JSONArray();
                                    for(int i = 0; i < imageArray.length(); i++){
                                        if(i != currentPictureId)
                                            newImageArray.put(imageArray.get(i));
                                        else
                                            dbHelper.deleteImage(i);
                                    }
                                    farmerImageData.put(currentPictureType, newImageArray);
                                    dbHelper.setParameter(getResources().getString(R.string.Images), farmerImageData.toString());
                                }else
                                    return;
                                if(currentPictureId > 0)
                                    currentPictureId = currentPictureId - 1;
                                else if(currentPictureId == 0 && newImageArray.length() > 0)
                                    currentPictureId = 0;
                                else if(currentPictureId == 0 && (newImageArray == null || newImageArray.length() == 0))
                                    currentPictureId = -1;
                                new loadImage().execute(currentPictureType, String.valueOf(currentPictureId));
                                refreshImageListView();
                            }catch (Exception ex){
                                logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
                            }
                        }
                    });

            builder.setNegativeButton(
                    "No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert = builder.create();
            alert.show();
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }
    }

    public void loadPreviousPicture(){
        try{
            JSONArray imageArray = null;
            if(farmerImageData.has(currentPictureType))
                imageArray = farmerImageData.getJSONArray(currentPictureType);

            if(imageArray == null || imageArray.length() == 0)
                currentPictureId = -1;
            else if(imageArray.length() > 0 && currentPictureId == -1)
                currentPictureId = imageArray.length()-1;
            else if(imageArray.length() > 0 && currentPictureId != -1)
                currentPictureId = currentPictureId - 1;
            else
                currentPictureId = -1;
            new loadImage().execute(currentPictureType, String.valueOf(currentPictureId));
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }
    }

    public void loadNextPicture(){
        try{
            JSONArray imageArray = null;
            if(farmerImageData.has(currentPictureType))
                imageArray = farmerImageData.getJSONArray(currentPictureType);

            if(imageArray == null || imageArray.length() == 0)
                currentPictureId = -1;
            else if(imageArray.length() > currentPictureId+1)
                currentPictureId = currentPictureId+1;
            else if(imageArray.length() == currentPictureId+1)
                currentPictureId = -1;
            else
                currentPictureId = -1;

            new loadImage().execute(currentPictureType, String.valueOf(currentPictureId));
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }
    }

    public void validatePartnerData(){
        try{
            dbHelper.setParameter(getString(R.string.Images), farmerImageData.toString());
            dbHelper.setParameter(getString(R.string.ImagesStatus), "3");
            if(!farmerImageData.has("Farmer") || farmerImageData.getJSONArray("Farmer").length() == 0
                    || !farmerImageData.has("Aadharcard")  || farmerImageData.getJSONArray("Aadharcard").length() == 0)
                dbHelper.setParameter(getString(R.string.ImagesStatus), "1");
            else if(farmerImageData.has("Farmer") && farmerImageData.getJSONArray("Farmer").length() > 0
                    && farmerImageData.has("Aadharcard")  && farmerImageData.getJSONArray("Aadharcard").length() > 0
                    && (!farmerImageData.has("Bankbook")  || (farmerImageData.has("Bankbook") && farmerImageData.getJSONArray("Bankbook").length() == 0)
                        || !farmerImageData.has("Rationcard") || (farmerImageData.has("Rationcard") && farmerImageData.getJSONArray("Rationcard").length() == 0)
                        || !farmerImageData.has("Pancard") || (farmerImageData.has("Pancard") && farmerImageData.getJSONArray("Pancard").length() == 0)
                        || !farmerImageData.has("Additional") || (farmerImageData.has("Additional") && farmerImageData.getJSONArray("Additional").length() == 0)))
                dbHelper.setParameter(getString(R.string.ImagesStatus), "2");
            goBack();
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }
    }

    public void goBack(){
        try{
            if ( getFragmentManager().getBackStackEntryCount() > 0)
            {
                getFragmentManager().popBackStack();
                return;
            }
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage());
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        try {
            (rootView.findViewById(R.id.rl_container)).setVisibility(View.VISIBLE);
            currentPictureType = ((TextView) view.findViewById(R.id.tv_picturetype)).getText().toString().trim().replace(" ","");
            currentPictureId = 0;
            new loadImage().execute(currentPictureType, String.valueOf(currentPictureId));
        }
        catch (Exception e){logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), e.getMessage());}
    }

    public class loadImage extends AsyncTask<String, Void, ModelImageParameters> {
        @Override
        protected void onPreExecute(){
            img_picturetile.setImageResource(R.drawable.processing);
            img_picturetile.getLayoutParams().width = 200;
            img_picturetile.getLayoutParams().height = 200;
        }

        @Override
        protected ModelImageParameters doInBackground(String... strings) {
            ModelImageParameters response = null;
            try {
                String imageType = strings[0];
                int imageId = Integer.parseInt(strings[1]);
                JSONArray imagesArray = null;
                if(farmerImageData.has(imageType))
                    imagesArray = farmerImageData.getJSONArray(imageType);

                if(imagesArray == null || imagesArray.length() == 0){
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.baseline_add_photo_alternate_black_24dp);
                    response = new ModelImageParameters(bitmap,200,200,View.GONE,View.GONE, View.GONE);
                    currentPictureId = -1;
                }else if (imageId == -1 && imagesArray.length() > 0){
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.baseline_add_photo_alternate_black_24dp);
                    response = new ModelImageParameters(bitmap,200,200,View.VISIBLE,View.GONE, View.GONE);
                    currentPictureId = -1;
                }else if(imageId > imagesArray.length()){
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.baseline_add_photo_alternate_black_24dp);
                    response = new ModelImageParameters(bitmap,200,200,View.GONE,View.GONE, View.GONE);
                    currentPictureId = -1;
                }else if(imageId == 0){
                    byte[] imgbyte = dbHelper.getImage(imagesArray.getInt(0));
                    Bitmap bitmap = BitmapFactory.decodeByteArray(imgbyte, 0, imgbyte.length);
                    response = new ModelImageParameters(bitmap,(rootView.findViewById(R.id.rl_container)).getHeight()
                            ,(rootView.findViewById(R.id.rl_container)).getWidth(),View.GONE,View.VISIBLE, View.VISIBLE);
                    currentPictureId = 0;
                }else {
                    byte[] imgbyte = dbHelper.getImage(imagesArray.getInt(imageId));
                    Bitmap bitmap = BitmapFactory.decodeByteArray(imgbyte, 0, imgbyte.length);
                    response = new ModelImageParameters(bitmap,(rootView.findViewById(R.id.rl_container)).getHeight()
                            ,(rootView.findViewById(R.id.rl_container)).getWidth(),View.VISIBLE,View.VISIBLE, View.VISIBLE);
                    currentPictureId = imageId;

                }
            }catch (JSONException e) {
                logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), e.getMessage().toString());
            }catch (Exception ex){
                logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
            }
            return response;
        }

        @Override
        protected void onPostExecute(ModelImageParameters response) {
            try {
                //Toast.makeText(getActivity(), "Done"+response.getBitmap().getByteCount(), Toast.LENGTH_SHORT).show();
                img_picturetile.setImageBitmap(response.getBitmap());
                img_picturetile.getLayoutParams().width = response.getWidth();
                img_picturetile.getLayoutParams().height = response.getHeight();
                img_deletepic.setVisibility(response.getDeletePic());
                img_rightpic.setVisibility(response.getRightPic());
                img_leftpic.setVisibility(response.getLeftPic());
            }catch (Exception ex){
                logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
            }
        }
    }

    public void loadImage(String imageType, int imageId){
        try{
            JSONArray imagesArray = null;
            if(farmerImageData.has(imageType))
                imagesArray = farmerImageData.getJSONArray(imageType);

            if(imagesArray == null || imagesArray.length() == 0){
                img_picturetile.setImageResource(R.drawable.baseline_add_photo_alternate_black_24dp);
                img_picturetile.getLayoutParams().width = 200;
                img_picturetile.getLayoutParams().height = 200;
                img_deletepic.setVisibility(View.GONE);
                img_leftpic.setVisibility(View.GONE);
                img_rightpic.setVisibility(View.GONE);
            }else if (imageId == -1 && imagesArray.length() > 0){
                img_picturetile.setImageResource(R.drawable.baseline_add_photo_alternate_black_24dp);
                img_picturetile.getLayoutParams().width = 200;
                img_picturetile.getLayoutParams().height = 200;
                img_deletepic.setVisibility(View.GONE);
                img_leftpic.setVisibility(View.VISIBLE);
                img_rightpic.setVisibility(View.GONE);
            }else if(imageId > imagesArray.length()){
                img_picturetile.setImageResource(R.drawable.baseline_add_photo_alternate_black_24dp);
                img_picturetile.getLayoutParams().width = 200;
                img_picturetile.getLayoutParams().height = 200;
                img_deletepic.setVisibility(View.GONE);
                img_leftpic.setVisibility(View.GONE);
                img_rightpic.setVisibility(View.GONE);
            }else if(imageId == 0){
                byte[] imgbyte = Base64.decode(imagesArray.getString(0), Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(imgbyte, 0, imgbyte.length);
                img_picturetile.setImageBitmap(bitmap);
                img_picturetile.getLayoutParams().width = (rootView.findViewById(R.id.rl_container)).getWidth();
                img_picturetile.getLayoutParams().height = (rootView.findViewById(R.id.rl_container)).getHeight();
                img_deletepic.setVisibility(View.VISIBLE);
                img_rightpic.setVisibility(View.VISIBLE);
                img_leftpic.setVisibility(View.GONE);
            }else {
                byte[] imgbyte = Base64.decode(imagesArray.getString(imageId), Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(imgbyte, 0, imgbyte.length);
                img_picturetile.setImageBitmap(bitmap);
                img_picturetile.getLayoutParams().width = (rootView.findViewById(R.id.rl_container)).getWidth();
                img_picturetile.getLayoutParams().height = (rootView.findViewById(R.id.rl_container)).getHeight();
                img_deletepic.setVisibility(View.VISIBLE);
                img_rightpic.setVisibility(View.VISIBLE);
                img_leftpic.setVisibility(View.VISIBLE);
            }
        }catch (Exception e){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), e.getMessage());
        }
    }

    public void loadFragment(){
        try{
            Bundle args = new Bundle();
            args.putString("PictureType", currentPictureType);
            args.putInt("PictureId", currentPictureId);
            Fragment fragment = new FragmentCameraPicture();
            fragment.setArguments(args);
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.slideinleft,R.anim.slideoutleft);
            fragmentTransaction.replace(R.id.frame, fragment, "Camera");
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commitAllowingStateLoss();
        }catch (Exception e){logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), e.getMessage());}
    }
}