package com.technoship.quizapp.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.technoship.quizapp.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class StartFragment extends Fragment {

    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private boolean isOpening;
    private SharedPreferences sharedPreferences;
    private ProgressBar startProgressBar;
    private TextView startFeedbackText;
    private FirebaseAuth auth;
    private NavController navController;

    private static final String START_FRAGMENT = "START_FRAGMENT";

    public StartFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_start, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @org.jetbrains.annotations.NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        auth = FirebaseAuth.getInstance();
        startProgressBar = view.findViewById(R.id.startProgress);
        startFeedbackText = view.findViewById(R.id.startDetails);
        startFeedbackText.setText(R.string.check_app_version);
    }

    @Override
    public void onStart() {
        super.onStart();

        sharedPreferences = requireContext().getSharedPreferences("SHARED_PREFS_1", MODE_PRIVATE);

        Map<String, Object> defaultsRate = new HashMap<>();
        defaultsRate.put("mustUpdateVersions", "");
        defaultsRate.put("ask4UpdateVersions", "");

        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(14400) //14400
                .build();

        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);
        mFirebaseRemoteConfig.setDefaultsAsync(defaultsRate);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (!isOpening) {
                Toast.makeText(requireContext(), getString(R.string.splash_poorConnectionTip), Toast.LENGTH_SHORT).show();
                workByOldSavedData();
            }
        }, 4500);

        mFirebaseRemoteConfig.fetchAndActivate().addOnCompleteListener(task -> {
            if (isOpening) return;

            if (task.isSuccessful()) {
                isOpening = true;

                final String mustUpdateVersionsString = mFirebaseRemoteConfig.getString("mustUpdateVersions");
                final String ask4UpdateVersionsString = mFirebaseRemoteConfig.getString("ask4UpdateVersions");

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("mustUpdateVersionsString", mustUpdateVersionsString);
                editor.putString("ask4UpdateVersionsString", ask4UpdateVersionsString);
                editor.apply();

                final String[] mustUpdateVersions = mustUpdateVersionsString.split(":");
                final String[] ask4UpdateVersions = ask4UpdateVersionsString.split(":");

                ArrayList<String> mustUpdateVersionsList = new ArrayList<>(Arrays.asList(mustUpdateVersions));
                ArrayList<String> ask4UpdateVersionsList = new ArrayList<>(Arrays.asList(ask4UpdateVersions));

                if(mustUpdateVersionsList.contains(String.valueOf(getVersionCode()))){
                    showUpdateDialog(true);
                }
                else if(ask4UpdateVersionsList.contains(String.valueOf(getVersionCode()))){
                    showUpdateDialog(false);
                }
                else {
                    startOpen();
                }
            }
            else {
                Log.w("ASDASD", task.getException().toString());
            }
        });
    }

    private int getVersionCode() {
        PackageInfo pInfo = null;
        try {
            pInfo = requireContext().getPackageManager().getPackageInfo(requireContext().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e){
            Log.wtf("ASDASD", "NameNotFoundException: "+e.getMessage());
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            return (int) pInfo.getLongVersionCode();
        }
        else {
            //noinspection deprecation
            return pInfo.versionCode;
        }
    }

    private void workByOldSavedData() {
        isOpening = true;
        String mustUpdateVersionsString = sharedPreferences.getString("mustUpdateVersionsString", "--");
        String ask4UpdateVersionsString = sharedPreferences.getString("ask4UpdateVersionsString", "--");

        if (mustUpdateVersionsString.equals("--")){
            ask4connectToNetwork();
            return;
        }

        String[] mustUpdateVersions = mustUpdateVersionsString.split(":");
        String[] ask4UpdateVersions = ask4UpdateVersionsString.split(":");

        ArrayList<String> mustUpdateVersionsList = new ArrayList<>(Arrays.asList(mustUpdateVersions));
        ArrayList<String> ask4UpdateVersionsList = new ArrayList<>(Arrays.asList(ask4UpdateVersions));

        if(mustUpdateVersionsList.contains(String.valueOf(getVersionCode()))){
            showUpdateDialog(true);
        }
        else if(ask4UpdateVersionsList.contains(String.valueOf(getVersionCode()))){
            showUpdateDialog(false);
        }
        else {
            startOpen();
        }
    }

    private void ask4connectToNetwork() {
        AlertDialog.Builder builder=new AlertDialog.Builder(requireContext());
        builder.setCancelable(false);
        builder.setMessage(R.string.splashActivity_ask4network_message);
        builder.setTitle(R.string.splashActivity_ask4network_title);
        builder.setNegativeButton(R.string.splashActivity_Exit, (dialog, which) -> navController.popBackStack());
        builder.create();
        builder.show();
    }

    private void showUpdateDialog(boolean extremist) {
        AlertDialog.Builder builder=new AlertDialog.Builder(requireContext());
        builder.setCancelable(false);
        builder.setTitle(R.string.splashActivity_updateDialogTitle);
        builder.setMessage(R.string.splashActivity_updateDialogContent);
        builder.setPositiveButton(R.string.splashActivity_update, (dialog, which) -> {
            openAppOnPlayStore();
        });
        if (extremist){
            builder.setNegativeButton(R.string.splashActivity_Exit, (dialog, which) -> {
                navController.popBackStack();
            });
        }
        else {
            builder.setNegativeButton(R.string.splashActivity_discard, (dialog, which) -> startOpen());
        }
        builder.create();
        builder.show();
    }

    private void openAppOnPlayStore() {
        final String appPackageName = requireContext().getPackageName();
        try {
            requireContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            requireContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    private void startOpen(){
        startFeedbackText.setText(R.string.checkUserAccount);
        new Handler().postDelayed(() -> {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser == null){
                startFeedbackText.setText(R.string.createAccount);
                auth.signInAnonymously().addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()){
                        startFeedbackText.setText(R.string.accountCreated);
                        navController.navigate(R.id.action_startFragment_to_listFragment);
                    }
                    else {
                        startFeedbackText.setText(R.string.errorCreateAccount);
                    }
                });
            }
            else {
                startFeedbackText.setText(R.string.loggedIn);
                navController.navigate(R.id.action_startFragment_to_listFragment);
            }
        }, 2000);
    }
}