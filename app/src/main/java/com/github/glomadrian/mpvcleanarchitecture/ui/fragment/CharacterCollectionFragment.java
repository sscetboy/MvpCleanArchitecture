package com.github.glomadrian.mpvcleanarchitecture.ui.fragment;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.github.glomadrian.mpvcleanarchitecture.R;
import com.github.glomadrian.mpvcleanarchitecture.app.BaseFragment;
import com.github.glomadrian.mpvcleanarchitecture.domain.LogUtils;
import com.github.glomadrian.mpvcleanarchitecture.domain.model.MarvelCharacterCollection;
import com.github.glomadrian.mpvcleanarchitecture.ui.adapter.ModelAdapter;
import com.github.glomadrian.mpvcleanarchitecture.ui.custom.recycler.ClickRecyclerView;
import com.github.glomadrian.mpvcleanarchitecture.ui.presenter.CharacterCollectionPresenter;
import com.github.glomadrian.mpvcleanarchitecture.ui.view.CharacterCollectionView;
import com.github.glomadrian.mpvcleanarchitecture.ui.viewModel.Model;

import org.parceler.Parcels;

import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;

/**
 * @author glomadrian
 */
public class CharacterCollectionFragment extends BaseFragment implements CharacterCollectionView {

    private static final String EXTRA_CHARACTER_COLLECTION = "extraCharacterCollection";

    @Inject
    CharacterCollectionPresenter characterCollectionPresenter;

    @InjectView(R.id.collection_view)
    ClickRecyclerView collectionView;
    @InjectView(R.id.loading)
    ProgressBar loading;

    private ModelAdapter modelAdapter;
    private LinearLayoutManager mLayoutManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(LogUtils.generateTag(this), "on crate()");
        super.onCreate(savedInstanceState);
        modelAdapter = new ModelAdapter();
        mLayoutManager = new LinearLayoutManager(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.character_collection, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeCollectionView();
        characterCollectionPresenter.setView(this);
        characterCollectionPresenter.onViewCreate();

        if (savedInstanceState == null) {
            Log.i(LogUtils.generateTag(this), "First time running");
            characterCollectionPresenter.initialize();
        }

        addClickListenerToCharacterList();

    }


    /**
     * In android the view is not a simple view, there is some cases when the functionality of the
     * view is more than the excepted, in this case for example the view save the state
     *
     * @param outState
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //Get the actual state of the characters
        MarvelCharacterCollection marvelCharacters = characterCollectionPresenter.getParcelableCollection();

        //Parcel the object to be saved in the bundle
        Parcelable marvelCharactersWrapped = Parcels.wrap(marvelCharacters);

        //Save the parcelable
        outState.putParcelable(EXTRA_CHARACTER_COLLECTION, marvelCharactersWrapped);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (savedInstanceState != null) {
            Log.i(LogUtils.generateTag(this), "onViewStateRestored");
            //Get parcelable from bundle
            Parcelable marvelCharactersWrapped = savedInstanceState.getParcelable(EXTRA_CHARACTER_COLLECTION);
            MarvelCharacterCollection marvelCharacters = Parcels.unwrap(marvelCharactersWrapped);
            characterCollectionPresenter.restoreParcelableCollection(marvelCharacters);
        }
    }

    private void initializeCollectionView() {
        collectionView.setAdapter(modelAdapter);
        collectionView.setLayoutManager(mLayoutManager);
        collectionView.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public void add(Model model) {
        modelAdapter.add(model);
    }

    @Override
    public void add(List<Model> models) {
        modelAdapter.add(models);
    }

    @Override
    public void remove(Model model) {
        //TODO implement
    }

    @Override
    public int getModelsRenderer() {
        return modelAdapter.getItemCount();
    }

    @Override
    public void showLoading() {
        loading.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        loading.setVisibility(View.GONE);
    }


    @Override
    public void activateLastCharacterViewListener() {
        enableSearchOnFinish();
    }

    @Override
    public void disableLastCharacterViewListener() {
        disableSearchOnFinish();
    }

    private void addClickListenerToCharacterList() {
        collectionView.setOnItemClickListener(new CharacterClickListener());
    }

    private void enableSearchOnFinish() {
        collectionView.setOnScrollListener(new FinishScrollListener());
    }

    private void disableSearchOnFinish() {
        collectionView.setOnScrollListener(null);
    }

    private class FinishScrollListener implements RecyclerView.OnScrollListener {

        @Override
        public void onScrollStateChanged(int i) {
            //Do nothing
        }

        @Override
        public void onScrolled(int i, int i2) {

            int lastVisibleItemPosition = mLayoutManager.findLastVisibleItemPosition() + 1;
            int modelsCount = modelAdapter.getItemCount();

            if (lastVisibleItemPosition == modelsCount) {
                Log.i(LogUtils.generateTag(this), "finish scroll!");
                characterCollectionPresenter.onLastCharacterViewed();
            }
        }
    }

    private class CharacterClickListener implements ClickRecyclerView.OnItemClickListener {

        @Override
        public void onItemClick(RecyclerView parent, View view, int position, long id) {
            characterCollectionPresenter.onCharacterSelected(position);
        }
    }

}
