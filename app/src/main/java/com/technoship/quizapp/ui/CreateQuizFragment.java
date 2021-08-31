package com.technoship.quizapp.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.technoship.quizapp.R;
import com.technoship.quizapp.model.QuizListModel;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;
import java.util.UUID;

public class CreateQuizFragment extends Fragment {

    public CreateQuizFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private Uri quizImageUri;
    private ImageView imageView;
    private Button plusBtn, minusBtn, continueBtn;
    private TextInputLayout quizNameTextInputLayout, quizDescTextInputLayout, quizWriterNameTextInputLayout;
    private TextInputEditText quizNameTextInputEditText, quizDescTextInputEditText, quizWriterNameTextInputEditText;
    private EditText questionsEditText;
    private RadioGroup radioGroup;

    private ActivityResultLauncher<Intent> activityResultLauncherSelectImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_quiz, container, false);
        questionsEditText = view.findViewById(R.id.quizQuestionNum);
        imageView = view.findViewById(R.id.imageView);
        plusBtn = view.findViewById(R.id.plusBtn);
        minusBtn = view.findViewById(R.id.minusBtn);
        continueBtn = view.findViewById(R.id.continueBtn);
        quizNameTextInputLayout = view.findViewById(R.id.inputLayoutQuizName);
        quizDescTextInputLayout = view.findViewById(R.id.inputLayoutQuizDesc);
        quizWriterNameTextInputLayout = view.findViewById(R.id.inputLayoutWriterName);
        quizNameTextInputEditText = view.findViewById(R.id.inputEditTextQuizName);
        quizNameTextInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                quizNameTextInputLayout.setError(null);
            }
        });
        quizDescTextInputEditText = view.findViewById(R.id.inputEditTextQuizDesc);
        quizDescTextInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                quizDescTextInputLayout.setError(null);
            }
        });
        quizWriterNameTextInputEditText = view.findViewById(R.id.inputEditTextWriterName);
        quizWriterNameTextInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                quizWriterNameTextInputLayout.setError(null);
            }
        });
        radioGroup = view.findViewById(R.id.radioGroup);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        continueBtn.setOnClickListener(view14 -> continueBtnClicked(view));

        plusBtn.setOnClickListener(view12 -> {
            if (Integer.parseInt(questionsEditText.getText().toString() ) >= 20) return;
            questionsEditText.setText(String.valueOf(Integer.parseInt(questionsEditText.getText().toString())+1));
        });
        minusBtn.setOnClickListener(view13 -> {
            if (Integer.parseInt(questionsEditText.getText().toString() ) <= 1) return;
            questionsEditText.setText(String.valueOf(Integer.parseInt(questionsEditText.getText().toString())-1));
        });

        imageView.setOnClickListener(view1 -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            activityResultLauncherSelectImage.launch(intent);
        });

        activityResultLauncherSelectImage = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        assert data != null;
                        uploadImage(data.getData());
                    }
                    else {
                        Toast.makeText(requireContext(), "Image not selected", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void continueBtnClicked(View view){
        String quizName = Objects.requireNonNull(quizNameTextInputEditText.getText()).toString().trim();
        String quizDescription = Objects.requireNonNull(quizDescTextInputEditText.getText()).toString().trim();
        String writerName = Objects.requireNonNull(quizWriterNameTextInputEditText.getText()).toString().trim();
        int questionsNum = Integer.parseInt(questionsEditText.getText().toString());
        int difficulty = -1;
        int checkedRadioButtonId = radioGroup.getCheckedRadioButtonId();
        if (checkedRadioButtonId == R.id.radioBtn1) {
            difficulty = 1;
        } else if (checkedRadioButtonId == R.id.radioBtn2) {
            difficulty = 2;
        } else if (checkedRadioButtonId == R.id.radioBtn3) {
            difficulty = 3;
        } else if (checkedRadioButtonId == R.id.radioBtn4) {
            difficulty = 4;
        }

        if (quizName.isEmpty()){
            quizNameTextInputLayout.setError(getString(R.string.filed_required));
            return;
        }
        else if (quizDescription.isEmpty()){
            quizDescTextInputLayout.setError(getString(R.string.filed_required));
            return;
        }
        else if (writerName.isEmpty()){
            quizWriterNameTextInputLayout.setError(getString(R.string.filed_required));
            return;
        }
        else if (difficulty == -1){
            Toast.makeText(requireContext(), getString(R.string.select_difficulty_level), Toast.LENGTH_SHORT).show();
            return;
        }
        else if (quizImageUri == null){
            Toast.makeText(requireContext(), getString(R.string.attach_image), Toast.LENGTH_SHORT).show();
            return;
        }

        QuizListModel quizListModel =
                new QuizListModel(quizName, quizDescription, quizImageUri.toString(), difficulty, writerName, questionsNum);

        CreateQuizFragmentDirections.ActionCreateQuizFragmentToCreateQuestionsFragment action =
                CreateQuizFragmentDirections.actionCreateQuizFragmentToCreateQuestionsFragment(quizListModel);
        Navigation.findNavController(view).navigate(action);
    }

    private void uploadImage(Uri imageUri){
        ProgressDialog pd = new ProgressDialog(requireContext());
        pd.setCancelable(false);
        pd.setTitle(getString(R.string.uplaoding_image));
        pd.show();

        String randomKey = UUID.randomUUID().toString();
        StorageReference ref = FirebaseStorage.getInstance().getReference().child(dateForFileStorageReference()+"/Quiz Cover/"+randomKey);

        ref.putFile(imageUri)
                .addOnFailureListener(e -> {
                    pd.dismiss();
                    Toast.makeText(requireContext(), getString(R.string.error_upload_image), Toast.LENGTH_SHORT).show();
                })
                .addOnProgressListener(snapshot -> {
                    double progressPercentage = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                    pd.setMessage(getString(R.string.percentage)+ (int) progressPercentage +"%");
                })
                .addOnSuccessListener(taskSnapshot -> ref.getDownloadUrl().addOnSuccessListener(onlineUri -> {
                    pd.dismiss();
                    imageView.setImageURI(imageUri);
                    setImageUrl(onlineUri);
                }).addOnFailureListener(e -> {
                    pd.dismiss();
                    Toast.makeText(requireContext(), getString(R.string.error_upload_image), Toast.LENGTH_SHORT).show();
                }));
    }

    private void setImageUrl(Uri uri) {
        this.quizImageUri = uri;
    }

    public static String dateForFileStorageReference() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy", Locale.ENGLISH);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormat.format(new Date());
    }
}