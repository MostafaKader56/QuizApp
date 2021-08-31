package com.technoship.quizapp.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.technoship.quizapp.R;
import com.technoship.quizapp.model.QuizListModel;
import com.technoship.quizapp.viewmodel.QuizListViewModel;

import org.jetbrains.annotations.NotNull;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DetailsFragment newInstance(String param1, String param2) {
        DetailsFragment fragment = new DetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_details, container, false);
        mAdView = view.findViewById(R.id.adView);
        detailsTitle = view.findViewById(R.id.details_Title);
        detailsDescription = view.findViewById(R.id.details_Description);
        detailsDifficulty = view.findViewById(R.id.details_Difficulty_text);
        detailsTotalQuestions = view.findViewById(R.id.details_totalQuestions_text);
        detailsLastScore = view.findViewById(R.id.details_lastScore_text);
        imageView = view.findViewById(R.id.details_ImageView);
        detailsStartQuizBtn = view.findViewById(R.id.list_ViewButton);
        detailsLastScore = view.findViewById(R.id.details_lastScore_text);
        detailsWriter = view.findViewById(R.id.details_writer);
        return view;
    }

    private TextView detailsTitle, detailsDescription, detailsDifficulty, detailsTotalQuestions, detailsLastScore, detailsWriter;
    private ImageView imageView;
    private Button detailsStartQuizBtn;
    private NavController navController;
    private AdView mAdView;

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MobileAds.initialize(requireContext(), initializationStatus -> { });
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        navController = Navigation.findNavController(view);

        QuizListViewModel quizListViewModel = new ViewModelProvider(requireActivity()).get(QuizListViewModel.class);

        QuizListModel quizListItem = DetailsFragmentArgs.fromBundle(getArguments()).getQuizItem();
        int position = DetailsFragmentArgs.fromBundle(getArguments()).getPosition();

        detailsStartQuizBtn.setOnClickListener(view1 -> {
            DetailsFragmentDirections.ActionDetailsFragmentToQuizFragment action =
                    DetailsFragmentDirections.actionDetailsFragmentToQuizFragment(quizListItem);
            action.setPosition(position);
            navController.navigate(action);
        });

        quizListViewModel.getQuizLastScore(quizListItem.getQuizId());

        detailsTitle.setText(quizListItem.getName());
        detailsWriter.setText(getString(R.string.writer_name,quizListItem.getWriterName()));
        detailsDescription.setText(quizListItem.getDesc());
        detailsDifficulty.setText(String.valueOf(quizListItem.getLevel()));
        detailsTotalQuestions.setText(String.valueOf(quizListItem.getQuestionsNum()));
        quizListViewModel.getQuizLastScore().observe(getViewLifecycleOwner(), s -> detailsLastScore.setText(s));
        Glide.with(requireContext())
                .load(quizListItem.getImage())
                .centerCrop()
                .placeholder(R.drawable.placeholder_image)
                .into(imageView);
    }
}