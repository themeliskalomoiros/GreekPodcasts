package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_controllers.fragments.portofolio;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import gr.kalymnos.sk3m3l10.greekpodcasts.firebase.ChildNames;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.portofolio_screen.personal.PortofolioPersonalViewMvc;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.portofolio_screen.personal.PortofolioPersonalViewMvcImpl;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Podcast;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Podcaster;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.PromotionLink;
import gr.kalymnos.sk3m3l10.greekpodcasts.utils.FileUtils;

public class PortofolioPersonalFragment extends Fragment implements SaveOperationer, PortofolioPersonalViewMvc.OnViewsClickListener {

    private static final String TAG = PortofolioPersonalFragment.class.getSimpleName();
    private static final int RC_POSTER_PIC = 1313;

    private List<PromotionLink> cachedPromotionLinks;
    private Podcaster cachedPodcaster;
    private Uri cachedPosterUri;

    private PortofolioPersonalViewMvc viewMvc;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference podcasterRef;
    private DatabaseReference promotionLinksRef;
    private FirebaseStorage firebaseStorage;
    private StorageReference podcasterImageRef;
    private FirebaseUser firebaseUser;

    private InsertTextDialogFragment nameDialog, statementDialog;
    private InsertTextDialogFragment.OnInsertedTextListener
            nameInsertedListener = text -> viewMvc.bindPodcasterName(text),
            statementInsertedListener = text -> viewMvc.bindPersonalStatement(text);
    private PromotionLinkDialogFragment promotionDialog;
    private PromotionLinkDialogFragment.OnInsertedTextListener promotionInsertedTextListener =
            (title, url) -> {
                if (cachedPromotionLinks == null) {
                    cachedPromotionLinks = new ArrayList<>();
                }

                boolean hasTitleAndUrl = !TextUtils.isEmpty(title) && !TextUtils.isEmpty(url);

                if (hasTitleAndUrl) {
                    //  Update Ui
                    PromotionLink newPromotionLink = new PromotionLink(title, url);
                    cachedPromotionLinks.add(newPromotionLink);
                    viewMvc.bindPromotionLinks(cachedPromotionLinks);

                    //  TODO: The new promotion link must be uploaded
                } else {
                    Toast.makeText(getContext(), viewMvc.getNoTitleOrUrlMessageId(), Toast.LENGTH_SHORT).show();
                }
            };


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        initializeFirebase();
        viewMvc = new PortofolioPersonalViewMvcImpl(inflater, container);
        viewMvc.setOnViewsClickListener(this);
        return viewMvc.getRootView();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (isValidSavedInstanceState(savedInstanceState)) {
            initializeUiFromSavedInstanceState(savedInstanceState);
        } else {
            podcasterRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Podcaster podcaster = dataSnapshot.getValue(Podcaster.class);
                    if (podcaster != null) {
                        cachedPodcaster = podcaster;
                        viewMvc.bindPersonalStatement(cachedPodcaster.getPersonalStatement());
                        viewMvc.bindPodcasterName(cachedPodcaster.getUsername());
                        viewMvc.bindImage(cachedPodcaster.getImageUrl());

                    } else {
                        //  There is not podcaster, borrow a temporary username from FirebaseUser
                        viewMvc.bindPodcasterName(firebaseUser.getDisplayName());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            promotionLinksRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    List<PromotionLink> promotionLinks = new ArrayList<>();
                    for (DataSnapshot promotionLinkSnapShot : dataSnapshot.getChildren()) {
                        PromotionLink promotionLink = promotionLinkSnapShot.getValue(PromotionLink.class);
                        promotionLinks.add(promotionLink);
                    }
                    if (promotionLinks != null && promotionLinks.size() > 0) {
                        viewMvc.bindPromotionLinks(cachedPromotionLinks = promotionLinks);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (cachedPosterUri != null) {
            String fileName = FileUtils.fileName(getContext().getContentResolver(), cachedPosterUri);
            viewMvc.bindImage(cachedPosterUri);
            viewMvc.bindImageFileName(fileName);
            viewMvc.displayImageHint(false);
            viewMvc.displayImageFileName(true);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_POSTER_PIC) {
            boolean imageFileFetched = resultCode == getActivity().RESULT_OK && data != null;
            if (imageFileFetched) {
                cachedPosterUri = data.getData();
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (cachedPodcaster != null) {
            outState.putParcelable(Podcaster.PODCASTER_KEY, cachedPodcaster);
        }
        if (cachedPromotionLinks != null && cachedPromotionLinks.size() > 0) {
            outState.putParcelableArrayList(PromotionLink.PROMOTION_LINKS_KEY, (ArrayList<? extends Parcelable>) cachedPromotionLinks);
        }
        if (cachedPosterUri != null) {
            outState.putParcelable(Podcast.POSTER_KEY, cachedPosterUri);
        }
    }

    @Override
    public void save() {
        if (isValidStateToSave()) {
            viewMvc.displayLoadingIndicator(true);

            if (cachedPosterUri != null) {
                podcasterImageRef.putFile(cachedPosterUri).addOnSuccessListener(taskSnapshot -> {
                    savePodcastWithNewImage(taskSnapshot);
                });
            } else {
                podcasterRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Podcaster podcaster = dataSnapshot.getValue(Podcaster.class);
                        if (podcaster != null) {
                            podcasterRef.updateChildren(getUpdatedValuesMap())
                                    .addOnSuccessListener(aVoid ->
                                    {
                                        viewMvc.displayLoadingIndicator(false);
                                        getActivity().finish();
                                    });
                        } else {
                            Toast.makeText(getContext(), viewMvc.getMustChoosePictureMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    private HashMap<String, Object> getUpdatedValuesMap() {
                        HashMap<String, Object> updatedValuesMap = new HashMap<>();
                        updatedValuesMap.put(Podcaster.FIELD_NAME_PERSONAL_STATEMENT, viewMvc.getPersonalStatement());
                        updatedValuesMap.put(Podcaster.FIELD_NAME_USERNAME, viewMvc.getPodcasterName());
                        updatedValuesMap.put(Podcaster.FIELD_NAME_PERSONAL_STATEMENT, viewMvc.getPersonalStatement());
                        return updatedValuesMap;
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            if (cachedPromotionLinks != null && cachedPromotionLinks.size() > 0) {
                promotionLinksRef.setValue(cachedPromotionLinks);
            }

        } else {
            Toast.makeText(getContext(), viewMvc.getMustCompleteAllFieldsMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void savePodcastWithNewImage(UploadTask.TaskSnapshot taskSnapshot) {
        podcasterRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Podcaster podcaster = dataSnapshot.getValue(Podcaster.class);

                if (podcaster != null) {
                    podcasterRef.updateChildren(getUpdatedValuesMap(taskSnapshot.getDownloadUrl().toString()))
                            .addOnSuccessListener(aVoid ->
                            {
                                viewMvc.displayLoadingIndicator(false);
                                boolean promotionLinksAreValid = viewMvc.getPromotionLinks() != null && viewMvc.getPromotionLinks().size() > 0;
                                if (promotionLinksAreValid) {
                                    promotionLinksRef.setValue(viewMvc.getPromotionLinks()).addOnSuccessListener(aVoid1 -> getActivity().finish());
                                } else {
                                    getActivity().finish();
                                }
                            });
                } else {
                    createNewPodcaster(podcaster, taskSnapshot.getDownloadUrl().toString());
                }
            }

            private void createNewPodcaster(Podcaster podcaster, String imageUrl) {
                if (podcaster == null) {
                    podcaster = new Podcaster();
                }
                setupPodcaster(podcaster, imageUrl);
                podcasterRef.setValue(podcaster)
                        .addOnSuccessListener(aVoid ->
                        {
                            viewMvc.displayLoadingIndicator(false);
                            boolean promotionLinksAreValid = viewMvc.getPromotionLinks() != null && viewMvc.getPromotionLinks().size() > 0;
                            if (promotionLinksAreValid) {
                                promotionLinksRef.setValue(viewMvc.getPromotionLinks()).addOnSuccessListener(aVoid1 -> getActivity().finish());
                            } else {
                                getActivity().finish();
                            }
                        });
            }

            private void setupPodcaster(Podcaster podcaster, String imageUrl) {
                podcaster.setJoinedDate(System.currentTimeMillis());
                podcaster.setEmail(firebaseUser.getEmail());
                podcaster.setUsername(viewMvc.getPodcasterName());
                podcaster.setPersonalStatement(viewMvc.getPersonalStatement());
                podcaster.setImageUrl(imageUrl);
            }

            @NonNull
            private HashMap<String, Object> getUpdatedValuesMap(String imageUrl) {
                HashMap<String, Object> updatedValuesMap = new HashMap<>();
                updatedValuesMap.put(Podcaster.FIELD_NAME_USERNAME, viewMvc.getPodcasterName());
                updatedValuesMap.put(Podcaster.FIELD_NAME_IMAGEURL, imageUrl);
                updatedValuesMap.put(Podcaster.FIELD_NAME_PERSONAL_STATEMENT, viewMvc.getPersonalStatement());
                return updatedValuesMap;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public String getConfirmationMessage() {
        return null;
    }

    @Override
    public boolean isValidStateToSave() {
        boolean nameIsValid = !TextUtils.isEmpty(viewMvc.getPodcasterName());
        boolean pictureIsValid = viewMvc.pictureExists() || cachedPosterUri != null;
        boolean statementIsValid = !TextUtils.isEmpty(viewMvc.getPersonalStatement());
        return nameIsValid && pictureIsValid && statementIsValid;
    }

    @Override
    public void onEditPodcasterName() {
        if (nameDialog == null) {
            nameDialog = createDialog(viewMvc.getNameDialogTitleRes());
            nameDialog.setOnInsertedTextListener(nameInsertedListener);
        }
        nameDialog.show(getFragmentManager(), getDialogFragmentTag(viewMvc.getNameDialogTitleRes()));
    }

    @Override
    public void onEditPersonalStatementClick() {
        if (statementDialog == null) {
            statementDialog = createDialog(viewMvc.getPersonalStatementDialogTitleRes());
            statementDialog.setOnInsertedTextListener(statementInsertedListener);
        }
        statementDialog.show(getFragmentManager(), getDialogFragmentTag(viewMvc.getPersonalStatementDialogTitleRes()));
    }

    @Override
    public void onEditPromotionLinkClick() {
        if (promotionDialog == null) {
            promotionDialog = new PromotionLinkDialogFragment();
            promotionDialog.setOnInsertedTextListener(promotionInsertedTextListener);
        }
        promotionDialog.show(getFragmentManager(), getPromotionDialogFragmentTag());
    }

    @Override
    public void onImageClick() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(intent, RC_POSTER_PIC);
        }
    }

    private InsertTextDialogFragment createDialog(int titleRes) {
        Bundle args = new Bundle();
        args.putInt(InsertTextDialogFragment.TITLE_KEY, titleRes);

        InsertTextDialogFragment dialogFragment = new InsertTextDialogFragment();
        dialogFragment.setArguments(args);

        return dialogFragment;
    }

    private String getDialogFragmentTag(int titleRes) {
        return InsertTextDialogFragment.TAG + String.valueOf(titleRes);
    }

    private String getPromotionDialogFragmentTag() {
        return InsertTextDialogFragment.TAG + "_promotion_dialog_tag";
    }

    private void initializeFirebase() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        podcasterRef = firebaseDatabase.getReference().child(ChildNames.PODCASTERS).child(firebaseUser.getUid());
        promotionLinksRef = firebaseDatabase.getReference().child(ChildNames.PROMOTION_LINKS).child(firebaseUser.getUid());
        podcasterImageRef = firebaseStorage.getReference().child(ChildNames.PODCASTERS).child(firebaseUser.getEmail());
    }

    private boolean isValidSavedInstanceState(@Nullable Bundle savedInstanceState) {
        return savedInstanceState != null
                && savedInstanceState.containsKey(PromotionLink.PROMOTION_LINKS_KEY)
                && savedInstanceState.containsKey(Podcaster.PODCASTER_KEY);
    }

    private void initializeUiFromSavedInstanceState(@NonNull Bundle savedInstanceState) {
        cachedPodcaster = savedInstanceState.getParcelable(Podcaster.PODCASTER_KEY);
        cachedPromotionLinks = savedInstanceState.getParcelableArrayList(PromotionLink.PROMOTION_LINKS_KEY);
        cachedPosterUri = savedInstanceState.getParcelable(Podcast.POSTER_KEY);
        viewMvc.bindPromotionLinks(cachedPromotionLinks);
        viewMvc.bindPodcasterName(cachedPodcaster.getUsername());
        viewMvc.bindPersonalStatement(cachedPodcaster.getPersonalStatement());
    }
}
