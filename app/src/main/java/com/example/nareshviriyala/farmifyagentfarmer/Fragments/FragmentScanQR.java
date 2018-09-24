package com.example.nareshviriyala.farmifyagentfarmer.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.Snackbar;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.StringReader;
import java.util.Hashtable;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.example.nareshviriyala.farmifyagentfarmer.Activities.HomeActivity;
import com.example.nareshviriyala.farmifyagentfarmer.Helpers.DatabaseHelper;
import com.example.nareshviriyala.farmifyagentfarmer.Helpers.LogErrors;
import com.example.nareshviriyala.farmifyagentfarmer.Helpers.ValidationFarmerData;
import com.example.nareshviriyala.farmifyagentfarmer.Helpers.WebServiceOperation;
import com.example.nareshviriyala.farmifyagentfarmer.R;
import com.example.nareshviriyala.farmifyagentfarmer.zxing.BinaryBitmap;
import com.example.nareshviriyala.farmifyagentfarmer.zxing.ChecksumException;
import com.example.nareshviriyala.farmifyagentfarmer.zxing.DecodeHintType;
import com.example.nareshviriyala.farmifyagentfarmer.zxing.FormatException;
import com.example.nareshviriyala.farmifyagentfarmer.zxing.NotFoundException;
import com.example.nareshviriyala.farmifyagentfarmer.zxing.PlanarYUVLuminanceSource;
import com.example.nareshviriyala.farmifyagentfarmer.zxing.Reader;
import com.example.nareshviriyala.farmifyagentfarmer.zxing.Result;
import com.example.nareshviriyala.farmifyagentfarmer.zxing.common.HybridBinarizer;
import com.example.nareshviriyala.farmifyagentfarmer.zxing.qrcode.QRCodeReader;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

/**
 * Created by Home on 7/18/2016.
 */
public class FragmentScanQR extends Fragment implements SurfaceHolder.Callback, View.OnClickListener{

    private View rootView;
    private LogErrors logErrors;
    private DatabaseHelper dbHelper;
    private String className;

    public TakeShots takeshots;
    public Timer timer;
    private TextView tv_scanqr;
    private ImageView img_processing;
    private TextView tv_scanStatus;
    private ProgressBar pb_loading;

    public int shotInterval = 100;
    public boolean decodingQR = false;
    private boolean screenChanging = false;
    public SurfaceView sfv_camview;
    public Camera mCamera;
    public SurfaceHolder surfaceHolder;
    public Camera.PreviewCallback previewCallback;
    public int vibrateMilli = 400;
    public Hashtable<DecodeHintType, Object> decodeHints = new Hashtable<DecodeHintType, Object>();

    public WebServiceOperation wso;

    public String lastQRContent = "";
    public Vibrator v;
    private boolean progress = true;
    public boolean isCamOn = true;

    private int REQUEST_CAMERA_CODE = 100;

    private Button btn_entermanual;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_scanqr, container, false);
        try {
            logErrors = LogErrors.getInstance(getActivity());
            className = new Object(){}.getClass().getEnclosingClass().getName();
            dbHelper = new DatabaseHelper(getActivity());
            ((HomeActivity) getActivity()).setActionBarTitle("Scan aadhar QR");
            decodeHints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
            wso = new WebServiceOperation();

            btn_entermanual = rootView.findViewById(R.id.btn_entermanual);
            btn_entermanual.setOnClickListener(this);
            pb_loading = rootView.findViewById(R.id.pb_loading);
            img_processing = (ImageView) rootView.findViewById(R.id.img_processing);
            Glide.with(getActivity()).load(R.drawable.preloader).into(img_processing);
            tv_scanqr = (TextView) rootView.findViewById(R.id.tv_scanqr);
            tv_scanqr.setOnClickListener(this);
            tv_scanStatus = (TextView) rootView.findViewById(R.id.tv_scanStatus);

            sfv_camview = (SurfaceView) rootView.findViewById(R.id.sfv_camview);
            sfv_camview.setOnClickListener(this);
            surfaceHolder = sfv_camview.getHolder();
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
            v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
            previewCallback = new Camera.PreviewCallback() {
                @Override
                public void onPreviewFrame(byte[] data, Camera camera) {
                    try {
                        if(!decodingQR && !screenChanging) {
                            decodingQR = true;
                            Object boxdata = data;
                            new decodeQR().execute(boxdata);
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

    public class decodeQR extends AsyncTask<Object, Integer, String> {

        @Override
        protected String doInBackground(Object[] params) {
            //android.os.Debug.waitForDebugger();
            String decodedText = null;
            try {
                byte[] data = (byte[])params[0];
                Camera.Parameters parameters = mCamera.getParameters();
                int previewHeight = parameters.getPreviewSize().width;
                int previewWidth = parameters.getPreviewSize().height;
                PlanarYUVLuminanceSource source = new PlanarYUVLuminanceSource(data, previewHeight, previewWidth, 0, 0, previewHeight, previewWidth, false);
                BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
                Reader reader = new QRCodeReader();
                Result result = reader.decode(bitmap, decodeHints);
                decodedText = result.getText();
            }
            catch (NotFoundException e) {
                decodingQR = false;
            }
            catch (ChecksumException e) {
                decodingQR = false;
            }
            catch (FormatException e) {
                decodingQR = false;
            }
            return decodedText;
        }

        @Override
        protected void onPostExecute(String decodedText){
            super.onPostExecute(decodedText);
            if(decodedText != null) {
                doOperation(decodedText);
            }
            else
                new scanAnimation().execute();
        }
    }

    public class scanAnimation extends AsyncTask<Object,Integer,Object>{

        @Override
        protected Object doInBackground(Object[] params) {
            return null;
        }

        @Override
        protected void onPostExecute(Object input){
            super.onPostExecute(input);
            try{
                if(progress){
                    if(tv_scanStatus.getText().toString().length() > 40) {
                        progress = false;
                    }
                    else
                        tv_scanStatus.setText("*"+tv_scanStatus.getText().toString()+"*");
                }
                else{
                    if(tv_scanStatus.getText().toString().length() <= 12){
                        progress = true;
                        tv_scanStatus.setText("Searching QR");
                    }
                    else{
                        tv_scanStatus.setText(tv_scanStatus.getText().toString().substring(1, tv_scanStatus.getText().toString().length()-1));
                    }
                }
            }
            catch (Exception e){}
        }
    }

    public void doOperation(String QRContent){
        try{
            if(lastQRContent.equalsIgnoreCase(QRContent))
                return;
            lastQRContent = QRContent;

            if(validateData(QRContent)) {
                mCamera.stopPreview();
                decodingQR = false;
                v.vibrate(vibrateMilli/4);
                new getAadharDataFromServer().execute(dbHelper.getParameter(getResources().getString(R.string.Individual)), dbHelper.getParameter("token"));
                //loadFragment("FragmentAFIndividual");
            }
            else {
                decodingQR = false;
                lastQRContent = "";
            }
        }
        catch (Exception e){logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), e.getMessage());}
    }

    public class getAadharDataFromServer extends AsyncTask<String, Integer, JSONObject>{
        @Override
        protected void onPreExecute(){
            pb_loading.setVisibility(View.VISIBLE);
            btn_entermanual.setText("");
        }

        @Override
        protected JSONObject doInBackground(String[] params) {
            //android.os.Debug.waitForDebugger();
            JSONObject response = null;
            try {
                JSONObject json = new JSONObject(params[0]);
                String token = params[1];
                JSONObject postDataParams = new JSONObject();
                postDataParams.put("Aadhar", json.getString("Aadhar"));
                postDataParams.put("Phone", "");
                response = wso.MakePostCall("FarmerData/verifyAadhar", postDataParams.toString(), token);
            }
            catch (Exception e) {
                logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), e.getMessage());
            }

            return response;
        }

        @Override
        protected void onPostExecute(JSONObject result){
            super.onPostExecute(result);
            pb_loading.setVisibility(View.GONE);
            btn_entermanual.setText("Enter Manually");
            try {
                if (result.getInt("responseCode") == 200) {
                    JSONObject response = new JSONObject(result.getString("response"));
                    if(response.getBoolean("status")){ //found aadhar
                        //replace already set data with the new data
                        response = response.getJSONObject("result");
                        dbHelper.setParameter(getResources().getString(R.string.Individual), response.getString("individual_data"));
                        dbHelper.setParameter(getResources().getString(R.string.Bank), response.getString("bank_data"));
                        dbHelper.setParameter(getResources().getString(R.string.Agronomic), response.getString("agronomic_data"));
                        dbHelper.setParameter(getResources().getString(R.string.Social), response.getString("social_data"));
                        dbHelper.setParameter(getResources().getString(R.string.Commerce), response.getString("commerce_data"));
                        dbHelper.setParameter(getResources().getString(R.string.Partner), response.getString("partner_data"));
                        dbHelper.setParameter(getResources().getString(R.string.Images), "");
                        dbHelper.setParameter(getResources().getString(R.string.ImagesSHA), response.getString("image_data"));
                        new ValidationFarmerData(getActivity()).validateAllData();
                        loadFragment("FragmentAFIndividual");
                    }else{ //aadhar not found
                        // move to next fragment with scanned data from QR code
                        loadFragment("FragmentAFIndividual");
                    }
                } else if(result.getInt("responseCode") == 401){
                    Snackbar.make(getActivity().findViewById(R.id.fab), "Session expired", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    /*Snackbar.make(getActivity().findViewById(R.id.fab), "Loading...", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();*/
                    loadFragment("FragmentAFIndividual");
                }
            }
            catch (Exception e){logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), e.getMessage());}
        }
    }

    /*public JSONObject saveImagesLocally(String imagedata){
        JSONObject finalJson = new JSONObject();
        try {

            JSONObject imageString = new JSONObject(imagedata);
            JSONArray imageType = imageString.names();
            for(int i = 0; i < imageType.length(); i++){//loop each image category Farmer, Aadhar, Ration, Pan, Bankbook, Additional
                JSONArray imagearray = imageString.getJSONArray(imageType.getString(i));
                JSONArray localImageIntArray = new JSONArray();
                for(int j = 0; j < imagearray.length(); j++){
                    byte[] img = Base64.decode(imagearray.getString(j), Base64.DEFAULT);
                    localImageIntArray.put(dbHelper.setImage(img));
                }
                finalJson.put(imageType.getString(i), localImageIntArray);
            }

        }catch (Exception e){logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), e.getMessage());}
        return finalJson;
    }*/

    public boolean validateData(String QRContent){
        boolean ret = false;
        decodingQR = false;
        String uid = "", uname = "", dob = "", gender = "", co = "", address1 = "", address2 = "", area = "", district = "", mandal = "", state = "", pincode = "";
        try{
            if(!QRContent.contains("PrintLetterBarcodeData") &&
                    !QRContent.contains("uid") &&
                    !QRContent.contains("name") &&
                    !QRContent.contains("dob") &&
                    !QRContent.contains("gender") &&
                    !QRContent.contains("house") &&
                    !QRContent.contains("state") &&
                    !QRContent.contains("dist"))
                return ret;
            XmlPullParserFactory xmlFactoryObject = XmlPullParserFactory.newInstance();
            // get the parser
            XmlPullParser myparser = xmlFactoryObject.newPullParser();
            myparser.setInput(new StringReader(QRContent));

            // parse the XML
            int eventType = myparser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String name= myparser.getName();
                switch (eventType){
                    case XmlPullParser.START_TAG:
                        break;

                    case XmlPullParser.END_TAG:
                        if(name.equals("PrintLetterBarcodeData")){
                            uid = myparser.getAttributeValue(null,"uid");
                            uname = myparser.getAttributeValue(null,"name");
                            dob = myparser.getAttributeValue(null,"dob");
                            gender = myparser.getAttributeValue(null,"gender");
                            co = myparser.getAttributeValue(null,"co");
                            address1 = myparser.getAttributeValue(null,"house");
                            address2 = myparser.getAttributeValue(null,"street");
                            area = myparser.getAttributeValue(null,"po");
                            district = myparser.getAttributeValue(null,"dist");
                            mandal = myparser.getAttributeValue(null,"subdist");
                            state = myparser.getAttributeValue(null,"state");
                            pincode = myparser.getAttributeValue(null,"pc");
                        }
                        break;
                }
                // update eventType
                eventType = myparser.next();
            }
            JSONObject farmerIdvData = new JSONObject();
            if(uid.length() == 12)
                farmerIdvData.put("Aadhar", uid);
            String[] names = uname.split(" ");
            if(names.length > 0)
                farmerIdvData.put("Surname", names[0]);
            if(names.length > 1)
                farmerIdvData.put("FirstName", names[1]);
            String lastname = "";
            if(names.length > 2) {
                for(int i = 2; i < names.length; i++)
                    lastname = names[i]+" ";
                farmerIdvData.put("LastName", lastname.trim());
            }
            farmerIdvData.put("DOB", dob);
            farmerIdvData.put("Gender", (gender.equalsIgnoreCase("M"))?"Male":"Female");
            farmerIdvData.put("Address1", address1);
            farmerIdvData.put("Address2", (address2 + " " + area).trim());
            farmerIdvData.put("State", state);
            farmerIdvData.put("District", district);
            farmerIdvData.put("VillageTown", mandal);
            farmerIdvData.put("Pincode", pincode);
            dbHelper.setParameter(getString(R.string.Individual), farmerIdvData.toString());
            ret = true;
        }
        catch (Exception e){logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), e.getMessage());}
        return ret;
    }

    public void loadFragment(String fragmentName){
        try{
            //Bundle bundle = new Bundle();
            //bundle.putString("guid",lastQRContent);
            screenChanging = true;
            int i = 0;
            while (decodingQR && i < 10) {
                Thread.sleep(100);
                i = i + 1;
            }
            decodingQR = false;
            //Class<?> c = Class.forName("com.example.nareshviriyala.farmifyagentfarmer.Fragments.FragmentAFIndividual");
            //Fragment fragment = (Fragment) c.newInstance();
            Fragment fragment = new FragmentAFIndividual();
            //fragment.setArguments(bundle);
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.slideinleft, R.anim.slideoutleft);
            fragmentTransaction.replace(R.id.frame, fragment, fragmentName);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commitAllowingStateLoss();
        }
        catch (Exception e){logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), e.getMessage());}
    }

    public void camOperation(boolean start){
        try{
            if(start){
                if (timer == null) {
                    mCamera.startPreview();
                    timer = new Timer();
                    takeshots = new TakeShots();
                    timer.schedule(takeshots, 100, shotInterval);
                }
            }
            else{
                if(mCamera != null)
                    mCamera.stopPreview();
                if(timer != null){
                    timer.cancel();
                    timer = null;
                }
                if(takeshots != null){
                    takeshots.cancel();
                    takeshots = null;
                }
            }
        }
        catch (Exception e){logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), e.getMessage());}
    }

    public class TakeShots extends TimerTask {
        public void run(){
            try {
                mCamera.setOneShotPreviewCallback(previewCallback);
            }
            catch (Exception e){
                logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), e.getMessage());
            }
        }
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
                case R.id.btn_entermanual:
                    loadFragment("FragmentAFIndividual");
                    break;
                case R.id.tv_scanqr:
                    if(isCamOn) {
                        camOperation(false);
                        isCamOn = false;
                    }
                    else {
                        isCamOn = true;
                        camOperation(true);
                    }
                    break;
                case R.id.sfv_camview:
                    if(isCamOn) {
                        camOperation(false);
                        isCamOn = false;
                    }
                    else {
                        isCamOn = true;
                        camOperation(true);
                    }
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
                        params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                        params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                        mCamera.setParameters(params);
                        mCamera.setDisplayOrientation(90);
                        mCamera.setPreviewDisplay(surfaceHolder);
                        mCamera.startPreview();
                        if(takeshots == null)
                            takeshots = new TakeShots();
                        if(timer == null)
                            timer = new Timer();
                        timer.schedule(takeshots, 100, shotInterval);

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
            ((HomeActivity) getActivity()).setActionBarTitle("Scan aadhar QR");
            screenChanging = false;
            lastQRContent = "";
            //mCamera.startPreview();
        }
        catch (Exception e){logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), e.getMessage());}
    }

    @Override
    public void onPause(){
        super.onPause();
        try {
            screenChanging = true;
            while (decodingQR) {
                Thread.sleep(100);
            }
            camOperation(false);
        }
        catch (Exception e){logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), e.getMessage());}
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
