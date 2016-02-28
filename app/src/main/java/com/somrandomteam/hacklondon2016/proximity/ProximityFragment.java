package com.somrandomteam.hacklondon2016.proximity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.gson.Gson;
import com.somrandomteam.hacklondon2016.HackApplication;
import com.somrandomteam.hacklondon2016.R;
import com.somrandomteam.hacklondon2016.proximity.dummy.DummyContent.DummyItem;
import com.somrandomteam.hacklondon2016.utils.Globals;
import com.somrandomteam.hacklondon2016.utils.Person;
import com.somrandomteam.hacklondon2016.utils.PersonAdapter;

import java.util.ArrayList;

import ch.uepaa.p2pkit.P2PKitClient;
import ch.uepaa.p2pkit.P2PKitStatusCallback;
import ch.uepaa.p2pkit.StatusResult;
import ch.uepaa.p2pkit.StatusResultHandling;
import ch.uepaa.p2pkit.discovery.InfoTooLongException;
import ch.uepaa.p2pkit.discovery.P2PListener;
import ch.uepaa.p2pkit.discovery.entity.Peer;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ProximityFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ProximityFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ProximityFragment newInstance(int columnCount) {
        ProximityFragment fragment = new ProximityFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    private String APP_KEY = HackApplication.getSecret("p2p.key");
    private PersonAdapter personAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_proximity_list, container, false);

        enableKit();
        startP2pDiscovery();

        // Set the adapter
        personAdapter = new PersonAdapter(getActivity(), new ArrayList<Person>());
        final ListView p2pList = (ListView) view.findViewById(R.id.p2p_list);
        p2pList.setAdapter(personAdapter);


        return view;
    }

    private void enableKit() {
        //Log.d("P2P Key", APP_KEY);
        final StatusResult result = P2PKitClient.isP2PServicesAvailable(getActivity());
        try {
            P2PKitClient.getInstance(getActivity()).getDiscoveryServices().setP2pDiscoveryInfo(Globals.user_id.getBytes());
        } catch (InfoTooLongException e) {
            Log.d("P2P: ", "P2pListener | The discovery info is too long");
        }
        if (result.getStatusCode() == StatusResult.SUCCESS) {
            P2PKitClient client = P2PKitClient.getInstance(getActivity());
            client.enableP2PKit(mStatusCallback, APP_KEY);
        } else {
            StatusResultHandling.showAlertDialogForStatusError(getActivity(), result);
        }
    }

    private void disableKit() {
        P2PKitClient client = P2PKitClient.getInstance(getActivity());
        client.getDiscoveryServices().removeP2pListener(mP2pDiscoveryListener);

        client.disableP2PKit();
    }

    private void startP2pDiscovery() {
        try {
            P2PKitClient.getInstance(getActivity()).getDiscoveryServices().setP2pDiscoveryInfo(getActivity().getIntent().getExtras().getString("Name").getBytes());
        } catch (InfoTooLongException e) {
            Log.d("P2P:", "P2pListener | The discovery info is too long");
        }
        P2PKitClient.getInstance(getActivity()).getDiscoveryServices().addP2pListener(mP2pDiscoveryListener);
    }

    private final P2PListener mP2pDiscoveryListener = new P2PListener() {

        @Override
        public void onP2PStateChanged(final int state) {
            Log.d("P2P: ", "P2pListener | State changed: " + state);
        }

        @Override
        public void onPeerDiscovered(final Peer peer) {
            byte[] colorBytes = peer.getDiscoveryInfo();
            if (colorBytes != null) {
                Gson gson = new Gson();
                String infoJSON = "{\"proximity_id\" : \"" + new String(peer.getDiscoveryInfo()) + "\"}";
                Person person = gson.fromJson(infoJSON, Person.class);
                personAdapter.add(person);
                Log.d("P2P: ", "P2pListener | Peer discovered: " + peer.getDiscoveryInfo().toString());
            } else {
                Log.d("P2P: ", "P2pListener | Peer discovered: No name");
            }
        }

        @Override
        public void onPeerLost(final Peer peer) {
            Log.d("P2P: ", "P2pListener | Peer lost: " + peer.getNodeId());
        }

        @Override
        public void onPeerUpdatedDiscoveryInfo(Peer peer) {
            byte[] colorBytes = peer.getDiscoveryInfo();
            if (colorBytes != null && colorBytes.length == 3) {
                Log.d("P2P: ", "P2pListener | Peer updated: " + peer.getNodeId());
            }
        }
    };

    private void stopP2pDiscovery() {
        P2PKitClient.getInstance(getActivity()).getDiscoveryServices().removeP2pListener(mP2pDiscoveryListener);
        Log.d("P2P: ", "P2pListener removed");
    }

    private final P2PKitStatusCallback mStatusCallback = new P2PKitStatusCallback() {
        @Override
        public void onEnabled() {
            Log.d("P2P: ", "Successfully enabled P2P Services");

        }

        @Override
        public void onSuspended() {
            Log.d("P2P: ", "P2P Services suspended");
        }

        @Override
        public void onError(StatusResult statusResult) {
            Log.d("P2P: ", "Error in P2P Services with status: " + statusResult.getStatusCode());
        }
    };

    String TAG = "P2P";
    private final P2PListener mP2PDiscoveryListener = new P2PListener() {
        @Override
        public void onP2PStateChanged(final int state) {
            Log.d(TAG, "P2PListener | State changed: " + state);
        }

        @Override
        public void onPeerDiscovered(final Peer peer) {
            Log.d(TAG, "P2PListener | Peer discovered: " + peer.getNodeId() + " with info: " + new String(peer.getDiscoveryInfo()));
        }

        @Override
        public void onPeerLost(final Peer peer) {
            Log.d(TAG, "P2PListener | Peer lost: " + peer.getNodeId());
        }

        @Override
        public void onPeerUpdatedDiscoveryInfo(Peer peer) {
            Log.d(TAG, "P2PListener | Peer updated: " + peer.getNodeId() + " with new info: " + new String(peer.getDiscoveryInfo()));
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
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
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(DummyItem item);
    }
}
