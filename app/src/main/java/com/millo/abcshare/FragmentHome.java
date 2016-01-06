package com.millo.abcshare;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentHome.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentHome#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentHome extends Fragment {
    public static final String TAG = "ABCShare FragmentHome";

//    private TextView tvPreset;
//    private TextView tvTime;
//    private TextView tvPlayStatus;
//    private Button buttonPlayPause;

    private TextView tvURI;
    private TextView tvServing;
    private ImageView ivShare;
    ImageView ivWebServerStarted;
    ImageView ivWebServerConnected;
    private TextView tvData;
    ImageView ivQRcode;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentHomeInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentHome.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentHome newInstance(String param1, String param2) {
        FragmentHome fragment = new FragmentHome();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public FragmentHome() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        ivShare = (ImageView) v.findViewById(R.id.ivShare);
        tvURI = (TextView) v.findViewById(R.id.tvURI);
        tvServing = (TextView) v.findViewById(R.id.tvServing);
        ivWebServerStarted = (ImageView) v.findViewById(R.id.ivWebServerStarted);
        ivWebServerConnected = (ImageView) v.findViewById(R.id.ivWebServerConnected);
        tvData = (TextView) v.findViewById(R.id.tvData);
        ivQRcode = (ImageView) v.findViewById(R.id.ivBarcode);

        if (savedInstanceState != null) {
            // Restore last state for checked position.
//            mPreset = (PlayService.MyPreset)savedInstanceState.getSerializable("mPreset");
            GUI_SetConnected(savedInstanceState.getBoolean("ivWebServerConnected"));
            GUI_SetStarted(savedInstanceState.getBoolean("ivWebServerStarted"));
            GUI_SetURI(savedInstanceState.getString("tvURI"));
            //GUI_SetURI_QRCode(savedInstanceState.getString("ivQRcode"));
            GUI_SetShare(savedInstanceState.getString("ivShare"));
            GUI_SetData(savedInstanceState.getString("tvData"));
            GUI_SetServing(savedInstanceState.getString("tvServing"));
        }

        return v;
    }

//    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentHomeInteraction(uri);
//        }
//    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentHomeInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.i(TAG,"onSaveInstanceState");
        super.onSaveInstanceState(outState);
        if (ivShare!=null)
        outState.putSerializable("ivShare", ivShare.getTag().toString());
        if (tvURI!=null)
        outState.putSerializable("tvURI", tvURI.getTag().toString());
        if (tvServing!=null)
        outState.putSerializable("tvServing", tvServing.getTag().toString());
        if (ivWebServerStarted!=null)
        outState.putSerializable("ivWebServerStarted", ivWebServerStarted.getTag().toString());
        if (ivWebServerConnected!=null)
        outState.putSerializable("ivWebServerConnected", ivWebServerConnected.getTag().toString());
        if (tvData!=null)
        outState.putSerializable("tvData", tvData.getTag().toString());
        if (ivQRcode!=null)
        outState.putSerializable("ivQRcode", ivQRcode.getTag().toString());
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentHomeInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentHomeInteraction_OpenFragmentSettings();
//        public void onFragmentHomeInteraction_BindService();
//        public void onFragmentHomeInteraction_UnbindService();
    }

    public void GUI_SetStarted(boolean started){
        Log.d(TAG, "GUI_SetStarted "+started);
        if (ivWebServerStarted!=null) {
            ivWebServerStarted.setTag(started);
            if(started) {
                ivWebServerStarted.setImageURI(null);
                ivWebServerStarted.setImageResource(R.drawable.led_gre);

                //String url = "http://"+MilloHelpers.getLocalIpAddress()+":8080";
                //GUI_SetURI(url);
                //GUI_SetShare(url);
            }
            else{
                ivWebServerStarted.setImageURI(null);
                ivWebServerStarted.setImageResource(R.drawable.led_red);
                GUI_SetURI("-");
            }
        }
    }
    public void GUI_SetConnected(boolean connected){
        Log.d(TAG, "GUI_SetConnected "+connected);
        if (ivWebServerConnected!=null) {
            ivWebServerConnected.setTag(connected);
            if(connected) {
                ivWebServerConnected.setImageURI(null);
                ivWebServerConnected.setImageResource(R.drawable.led_gre);
            }
            else{
                ivWebServerConnected.setImageURI(null);
                ivWebServerConnected.setImageResource(R.drawable.led_red);
            }
        }
    }
    public void GUI_SetURI_QRCode(String url, Display display) {
        Log.d(TAG, "GUI_SetURI_QRCode "+url);
        ivQRcode.setTag(url);
        if (ivQRcode != null) {
            //String qrData = "Data I want to encode in QR code";
            int qrCodeDimention = 500;

            //		Display display = getWindowManager().getDefaultDisplay();
            //		Point size = new Point();

            int width = 0;
            int height = 0;
//            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                display.getSize(size);
                width = size.x;
                height = size.y;
            } else {
                width = display.getWidth();  // deprecated
                height = display.getHeight();  // deprecated
            }

            qrCodeDimention = width;

            QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(url, null,
                    Contents.Type.TEXT, BarcodeFormat.QR_CODE.toString(), qrCodeDimention);

            try {
                Bitmap bitmap = qrCodeEncoder.encodeAsBitmap();
                ivQRcode.setImageBitmap(bitmap);
            } catch (WriterException e) {
                e.printStackTrace();
            }
        }
    }

    public void GUI_SetURI(String url){
        Log.d(TAG, "GUI_SetURI "+url);
        if (tvURI!=null) {
            ivShare.setTag(url);
            tvURI.setText(url);
        }
    }
    public void GUI_SetShare(String url){
        Log.d(TAG, "GUI_SetShare " + url);
        if (ivShare!=null) {
            ivShare.setTag(url);
            ivShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_VIEW);
                    sendIntent.setData(Uri.parse(v.getTag().toString()));

//                sendIntent.setAction(Intent.ACTION_SEND);
//				sendIntent.putExtra(Intent.EXTRA_TEXT, v.getTag().toString());
//				sendIntent.setType("text/plain");
                    //sendIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    sendIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(sendIntent);
                }
            });
        }
    }

    public void GUI_SetServing(String s){
        Log.d(TAG, "GUI_SetServing "+s);
        if (tvServing!=null) {
            tvServing.setText(s);
        }
    }
    public void GUI_SetData(String s){
        Log.d(TAG, "GUI_SetData "+s);
        if (tvData!=null) {
            tvData.setText(s);
        }
    }
}
