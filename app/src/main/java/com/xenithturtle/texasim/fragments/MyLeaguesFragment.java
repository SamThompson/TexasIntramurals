package com.xenithturtle.texasim.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.xenithturtle.texasim.adapters.IMSqliteAdapter;
import com.xenithturtle.texasim.cards.LeagueCard;
import com.xenithturtle.texasim.R;

import java.util.ArrayList;
import java.util.List;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.view.CardListView;


/**
 * @author Sam
 *
 */
public class MyLeaguesFragment extends Fragment {

    private OnFollowButtonPressedListener mListener;

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
        List<Integer> followingLeagues = sqliteAdapter.getFollowingLeagues();
        if (followingLeagues.size() > 0) {
            for (Integer i : followingLeagues) {
                LeagueCard l = new LeagueCard(getActivity(), i);
                cards.add(l);
            }
            sqliteAdapter.close();

            CardArrayAdapter ca = new CardArrayAdapter(getActivity(), cards);
            CardListView list = (CardListView) v.findViewById(R.id.cardlist);
            list.setAdapter(ca);
            (v.findViewById(R.id.progress_bar)).setVisibility(View.GONE);
            list.setVisibility(View.VISIBLE);
        } else {
            (v.findViewById(R.id.follow_button)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onFollowButtonPressed();
                }
            });
            (v.findViewById(R.id.progress_bar)).setVisibility(View.GONE);
            (v.findViewById(R.id.no_leagues)).setVisibility(View.VISIBLE);
        }

        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFollowButtonPressedListener) activity;
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
    public interface OnFollowButtonPressedListener {
        // TODO: Update argument type and name
        public void onFollowButtonPressed();
    }

}
