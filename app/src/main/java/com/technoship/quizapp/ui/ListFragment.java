package com.technoship.quizapp.ui;

import android.Manifest;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.technoship.quizapp.R;
import com.technoship.quizapp.adapter.QuizListAdapter;
import com.technoship.quizapp.viewmodel.QuizListViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;

///**
// * A simple {@link Fragment} subclass.
// * Use the {@link ListFragment#newInstance} factory method to
// * create an instance of this fragment.
// */
public class ListFragment extends Fragment {

//    // TODO: Rename parameter arguments, choose names that match
//    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//
//    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;

    public ListFragment() {
        // Required empty public constructor
    }

//    @Override
//    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
//        MenuItem fav = menu.add("add");
//        super.onCreateOptionsMenu(menu, inflater);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
//        if (item.getItemId() == R.id.addNewQuiz)
//        return super.onOptionsItemSelected(item);
//    }

    //    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment ListFragment.
//     */
//    // TODO: Rename and change types and number of parameters
//    public static ListFragment newInstance(String param1, String param2) {
//        ListFragment fragment = new ListFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
         In this method, you can assign variables, get Intent extras, and anything else that doesn't
         involve the View hierarchy (i.e. non-graphical initialisations). This is because this method
         can be called when the Activity's onCreate() is not finished, and so trying to access
         the View hierarchy here may result in a crash.
         */

//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /*
        After the onCreate() is called (in the Fragment), the Fragment's onCreateView() is called.
        You can assign your View variables and do any graphical initialisations. You are expected to
        return a View from this method, and this is the main UI view, but if your Fragment does not
        use any layouts or graphics, you can return null (happens by default if you don't override).
         */
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        recyclerView = view.findViewById(R.id.list_recyclerView);
        listProgress = view.findViewById(R.id.list_progressBar);

        // Inflate the layout for this fragment
        return view;
    }

    private RecyclerView recyclerView;
    private QuizListAdapter adapter;
    private ProgressBar listProgress;
    private Animation fadeInAnim, fadeOutAnim;
    private NavController navController;
    private View addView;
    private TextView changeLang;

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /*
        // This comment for onActivityCreated() which called immediately after onViewCreated() but deprecated

        As the name states, this is called after the Activity's onCreate() has completed.
        It is called after onCreateView(), and is mainly used for final initialisations
        (for example, modifying UI elements).
         */

        changeLang = view.findViewById(R.id.changeLang);
        changeLang.setOnClickListener(view12 -> {
            ((MainActivity)requireActivity()).setLanguage(getString(R.string.lang_suffix));
//            Locale myLocale = new Locale(getString(R.string.lang_suffix));
//            Resources res = requireContext().getResources();
//            DisplayMetrics dm = res.getDisplayMetrics();
//            Configuration conf = res.getConfiguration();
//            conf.locale = myLocale;
//            res.updateConfiguration(conf, dm);
//            Intent refresh = new Intent(requireContext(), MainActivity.class);
//            startActivity(refresh);
//            requireActivity().finish();
        });

        navController = Navigation.findNavController(view);

        adapter = new QuizListAdapter((quizItem, position) -> {
            ListFragmentDirections.ActionListFragmentToDetailsFragment action =
                    ListFragmentDirections.actionListFragmentToDetailsFragment(quizItem);
            action.setPosition(position);
            navController.navigate(action);
        });

        fadeInAnim = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        fadeOutAnim = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        QuizListViewModel quizListViewModel = new ViewModelProvider(requireActivity()).get(QuizListViewModel.class);
        quizListViewModel.orderQuizListData();
        quizListViewModel.getQuizListModelData().observe(getViewLifecycleOwner(), quizListModels -> {
            recyclerView.startAnimation(fadeInAnim);
            listProgress.startAnimation(fadeOutAnim);
            listProgress.setVisibility(View.INVISIBLE);

            adapter.setQuizListModels(quizListModels);

            adapter.notifyDataSetChanged();
        });

        addView = view.findViewById(R.id.listAddView);
        addView.setOnClickListener(view1 -> {
            PopupMenu popup = new PopupMenu(requireContext(), addView);
            popup.getMenuInflater()
                    .inflate(R.menu.list_fragment_menu, popup.getMenu());
            popup.setOnMenuItemClickListener(item -> {
                NavDirections action =
                        ListFragmentDirections.actionListFragmentToCreateQuizFragment();
                navController.navigate(action);
                return true;
            });
            popup.show();
        });
    }

    // Reference for where to do every work Link: https://stackoverflow.com/questions/28929637/difference-and-uses-of-oncreate-oncreateview-and-onactivitycreated-in-fra
    // TODO: maybe here there is some different from the documentation change it later.
}
