package com.xenithturtle.texasim.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xenithturtle.texasim.adapters.IMSqliteAdapter;
import com.xenithturtle.texasim.cards.LeagueCard;
import com.xenithturtle.texasim.R;

import java.util.ArrayList;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.view.CardListView;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MyLeaguesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MyLeaguesFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class MyLeaguesFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    public static MyLeaguesFragment newInstance() {
        MyLeaguesFragment fragment = new MyLeaguesFragment();
        return fragment;
    }
    public MyLeaguesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_my_leagues, container, false);

        //TODO: for now do this here, but move cardCreation to oncreate later
        IMSqliteAdapter sqliteAdapter = new IMSqliteAdapter(getActivity());
        sqliteAdapter.open();
        ArrayList<Card> cards = new ArrayList<Card>();
        //add cards here
        for (Integer i : sqliteAdapter.getFollowingLeagues()) {
            LeagueCard l = new LeagueCard(getActivity(), i);
            cards.add(l);
        }
        sqliteAdapter.close();

        CardArrayAdapter ca = new CardArrayAdapter(getActivity(), cards);
        CardListView list = (CardListView) v.findViewById(R.id.cardlist);
        list.setAdapter(ca);

        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
