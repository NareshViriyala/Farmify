package com.example.nareshviriyala.farmifyagentfarmer.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.nareshviriyala.farmifyagentfarmer.Activities.HomeActivity;
import com.example.nareshviriyala.farmifyagentfarmer.Helpers.Convertor;
import com.example.nareshviriyala.farmifyagentfarmer.Helpers.DatabaseHelper;
import com.example.nareshviriyala.farmifyagentfarmer.Helpers.LogErrors;
import com.example.nareshviriyala.farmifyagentfarmer.R;
import com.example.nareshviriyala.farmifyagentfarmer.zxing.DecodeHintType;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by Home on 7/18/2016.
 */
public class FragmentCameraPicture extends Fragment implements SurfaceHolder.Callback, View.OnClickListener{

    private View rootView;
    private LogErrors logErrors;
    private DatabaseHelper dbHelper;
    private String className;
    private JSONObject farmerPictureData;
    private boolean screenChanging = false;
    public SurfaceView sfv_camview;
    public Camera mCamera;
    public SurfaceHolder surfaceHolder;
    public Camera.PictureCallback pictureCallback;
    public Hashtable<DecodeHintType, Object> decodeHints = new Hashtable<DecodeHintType, Object>();
    private String currentPictureType = "";
    private int currentPictureId = 0;
    private ImageView img_picturecontour;

    public Vibrator v;
    public boolean isCamOn = true;

    private int REQUEST_CAMERA_CODE = 100;
    private ImageView btn_takepicture;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_camerapicture, container, false);
        try {
            logErrors = LogErrors.getInstance(getActivity());
            className = new Object(){}.getClass().getEnclosingClass().getName();
            dbHelper = new DatabaseHelper(getActivity());
            ((HomeActivity) getActivity()).setActionBarTitle("Take picture");
            decodeHints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
            currentPictureType = getArguments().getString("PictureType");
            currentPictureId = getArguments().getInt("PictureId");
            String data = dbHelper.getParameter(getString(R.string.Images));
            if(data.isEmpty() || data == null)
                farmerPictureData = new JSONObject();
            else
                farmerPictureData = new JSONObject(data);

            btn_takepicture = rootView.findViewById(R.id.btn_takepicture);
            btn_takepicture.setOnClickListener(this);

            img_picturecontour = rootView.findViewById(R.id.img_picturecontour);

            switch (currentPictureType.toLowerCase()){
                case "farmer":
                    img_picturecontour.setImageResource(R.drawable.face_conture);
                    break;
                case "pancard":
                    img_picturecontour.setImageResource(R.drawable.rectangle_outline);
                    break;
                case "aadharcard":
                    img_picturecontour.setImageResource(R.drawable.rectangle_outline);
                    break;
                default:
                    img_picturecontour.setVisibility(View.GONE);
                    break;

            }

            sfv_camview = (SurfaceView) rootView.findViewById(R.id.sfv_camview);
            surfaceHolder = sfv_camview.getHolder();
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
            v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
            pictureCallback = new Camera.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] data, Camera camera) {
                    try {
                        if(!screenChanging) {
                            Object boxdata = data;
                            new savePicture().execute(boxdata);
                        }
                    } catch (Exception e) {
                        logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), e.getMessage());
                    }
                }
            };

        }
        catch (Exception e){logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), e.getMessage());}
        return rootView;
    }

    public class savePicture extends AsyncTask<Object, Integer, Object> {

        @Override
        protected String doInBackground(Object[] params) {
            //android.os.Debug.waitForDebugger();
            byte[] data = null;
            try {
                data = (byte[])params[0];
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                ByteArrayOutputStream bytearrayopstream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG,40,bytearrayopstream);
                data = bytearrayopstream.toByteArray();
                JSONArray jsonArray = null;
                if(farmerPictureData.has(currentPictureType))
                    jsonArray = farmerPictureData.getJSONArray(currentPictureType);
                else
                    jsonArray = new JSONArray();
                jsonArray.put(Base64.encodeToString(data, Base64.DEFAULT));
                farmerPictureData.put(currentPictureType, jsonArray);
            }
            catch (Exception e) {
                logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), e.getMessage());
            }
            return "";
        }

        @Override
        protected void onPostExecute(Object decodedText){
            super.onPostExecute(decodedText);
            mCamera.stopPreview();
            dbHelper.setParameter(getResources().getString(R.string.Images), farmerPictureData.toString());
            camOperation(false);
            goBack();
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

    public void camOperation(boolean start){
        try{
            if(start){
                mCamera.startPreview();
            }
            else{
                if(mCamera != null)
                    mCamera.stopPreview();
            }
        }
        catch (Exception e){logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), e.getMessage());}
    }

    public void requestCameraPermissions(){
        try{
            int hasCameraAccess = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA);
            if (hasCameraAccess != PackageManager.PERMISSION_GRANTED) {

                showMessageOKCancel(getResources().getString(R.string.app_name)+ " " +getResources().getString(R.string.campermission),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            requestPermissions(new String[] {Manifest.permission.CAMERA}, REQUEST_CAMERA_CODE);
                        }
                    });
            }
        }
        catch (Exception e){logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), e.getMessage());}
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private Camera.Size getBestPreviewSize(Camera.Parameters parameters){
        int wid = sfv_camview.getWidth();
        int hig = sfv_camview.getHeight();

        Camera.Size bestSize = null;
        List<Camera.Size> sizeList = parameters.getSupportedPreviewSizes();
        bestSize = sizeList.get(0);
        int diff = Math.abs((wid * hig)-(sizeList.get(0).width * sizeList.get(0).height));
        for(int i = 0; i < sizeList.size(); i++){
            int slw = sizeList.get(i).width;
            int slh = sizeList.get(i).height;
            if(diff > Math.abs((wid * hig)-(sizeList.get(i).width * sizeList.get(i).height))){
                bestSize = sizeList.get(i);
                diff = Math.abs((wid * hig)-(sizeList.get(i).width * sizeList.get(i).height));
            }
        }
        return bestSize;
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()){
                case R.id.btn_takepicture:
                    mCamera.takePicture(null, null, pictureCallback);
                    break;
                default:
                    break;
            }
        }
        catch (Exception e){logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), e.getMessage());}
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        try{
            if(mCamera != null) {
                mCamera.stopPreview();
                mCamera.release();
                mCamera = null;
            }
        }
        catch (Exception e){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), e.getMessage());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        try {
            switch (requestCode) {
                case 100: {
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        mCamera = Camera.open();
                        Camera.Parameters params = mCamera.getParameters();
                        params.setRotation(90);
                        Camera.Size size = getBestPreviewSize(params);
                        params.setPreviewSize(size.width, size.height);
                        params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                        mCamera.setParameters(params);
                        mCamera.setDisplayOrientation(90);
                        mCamera.setPreviewDisplay(surfaceHolder);
                        mCamera.startPreview();

                    } else {
                        Toast.makeText(getActivity(), "We can not scan QR without CAMERA ACCESS \n Go to Settings and allow access.", Toast.LENGTH_SHORT).show();
                    }
                    return;
                }
            }
        }
        catch (Exception e){logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), e.getMessage());}
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try{
            mCamera = Camera.open();
            Camera.Parameters params = mCamera.getParameters();
            //params.setPreviewSize(sfv_camview.getWidth(), sfv_camview.getHeight());
            params.setRotation(90);
            Camera.Size size = getBestPreviewSize(params);
            params.setPreviewSize(size.width, size.height);
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            mCamera.setParameters(params);
            mCamera.setDisplayOrientation(90);
            mCamera.setPreviewDisplay(surfaceHolder);
            if(isCamOn)
                camOperation(true);
            //timer.schedule(takeshots, 100, shotInterval);
        }
        catch (RuntimeException ex){
            requestCameraPermissions();
        }
        catch (Exception e){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), e.getMessage());
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        try {
            if(mCamera != null)
                camOperation(isCamOn);
            ((HomeActivity) getActivity()).setActionBarTitle("Take picture");
            screenChanging = false;
            //mCamera.startPreview();
        }
        catch (Exception e){logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), e.getMessage());}
    }

    @Override
    public void onPause(){
        super.onPause();
        try {
            screenChanging = true;
            camOperation(false);
        }
        catch (Exception e){logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), e.getMessage());}
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
