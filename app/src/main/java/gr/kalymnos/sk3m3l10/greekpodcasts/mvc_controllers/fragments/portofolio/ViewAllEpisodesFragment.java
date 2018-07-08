package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_controllers.fragments.portofolio;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import gr.kalymnos.sk3m3l10.greekpodcasts.R;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.portofolio_screen.publish.EpisodesAdapter;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Episode;

/*  This fragment is not a controller, it just displays a list of episodes*/

public class ViewAllEpisodesFragment extends Fragment {

    interface OnAddButtonClickListener {
        void onViewAllEpisodesFragmentAddButtonClicked();
    }

    private OnAddButtonClickListener callback;

    private RecyclerView episodesRecyclerView;
    private EpisodesAdapter episodesAdapter;

    private ImageButton addEpisodeButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.view_all_episodes_fragment, container, false);
        addEpisodeButton = root.findViewById(R.id.add_episode_imagebutton);
        //  Recycler View initialization
        episodesRecyclerView = root.findViewById(R.id.recycler_view);
        episodesAdapter = new EpisodesAdapter(getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        episodesRecyclerView.setLayoutManager(layoutManager);
        episodesRecyclerView.setHasFixedSize(true);
        episodesRecyclerView.setAdapter(episodesAdapter);
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        episodesAdapter.addEpisodes(getArguments().getParcelableArrayList(Episode.EPISODES_KEY));
        episodesAdapter.notifyDataSetChanged();
    }
}
