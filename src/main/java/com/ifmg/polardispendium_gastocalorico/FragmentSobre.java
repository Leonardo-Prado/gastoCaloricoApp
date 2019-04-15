package com.ifmg.polardispendium_gastocalorico;


import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import nucleo.entidades_do_nucleo.Usuario;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentSobre extends Fragment {


    public FragmentSobre() {
        // Required empty public constructor
    }

    public static FragmentSobre newInstance() {
        FragmentSobre fragment = new FragmentSobre();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_sobre, container, false);
        WebView wvSobre = view.findViewById(R.id.wvSobre);
        wvSobre.getSettings().setJavaScriptEnabled(true);
        final Activity activity = getActivity();
        wvSobre.setWebViewClient(new WebViewClient(){
            @SuppressWarnings("deprecation")
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(activity, description, Toast.LENGTH_SHORT).show();
            }
            @TargetApi(android.os.Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError rerr) {
                // Redirect to deprecated method, so you can use it in all SDK versions
                onReceivedError(view, rerr.getErrorCode(), rerr.getDescription().toString(), req.getUrl().toString());
            }
        });
        wvSobre.loadUrl("https://polardispendium.home.blog/sobre/");
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        try {
            this.finalize();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}
