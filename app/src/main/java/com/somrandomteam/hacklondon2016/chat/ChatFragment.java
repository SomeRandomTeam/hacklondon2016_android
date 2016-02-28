package com.somrandomteam.hacklondon2016.chat;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.pusher.client.Pusher;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.SubscriptionEventListener;
import com.somrandomteam.hacklondon2016.HackApplication;
import com.somrandomteam.hacklondon2016.R;
import com.somrandomteam.hacklondon2016.chat.dummy.DummyContent;
import com.somrandomteam.hacklondon2016.chat.dummy.DummyContent.DummyItem;
import com.somrandomteam.hacklondon2016.utils.Message;
import com.somrandomteam.hacklondon2016.utils.MessageAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ChatFragment extends Fragment implements View.OnClickListener {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ChatFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ChatFragment newInstance(int columnCount) {
        ChatFragment fragment = new ChatFragment();
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

    private Button sendMessage;
    private EditText messageRaw;
    private String username;
    private String dataUrl = "http://szekelyszilv.com:3333/api/v1/events/";
    private String MESSAGE_ENDPOINT;
    private MessageAdapter messageAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_list, container, false);

        Bundle extras = getActivity().getIntent().getExtras();
        username = extras.getString("Name").toString();
        MESSAGE_ENDPOINT = dataUrl + extras.getString("EventID").toString() + "/message/";

        sendMessage = (Button) view.findViewById(R.id.sendMessage);
        sendMessage.setOnClickListener(this);

        messageRaw = (EditText) view.findViewById(R.id.message);

        messageAdapter = new MessageAdapter(getActivity(), new ArrayList<Message>());
        final ListView messagesView = (ListView) view.findViewById(R.id.chat_list);
        messagesView.setAdapter(messageAdapter);

        JSONArray messages = null;
        try {
            messages = (new JSONObject(getActivity().getIntent().getExtras().getString("Details"))).getJSONArray("messages");
        } catch (Exception e) {
            Log.d("JSONArray: ", "Fucked");
        }

        if (messages != null) {
            Gson gson = new Gson();
            for (int i = 0; i < messages.length(); i++) {
                String data;
                try {
                    data = messages.getString(i);
                } catch (Exception e) {
                    data = null;
                }
                if (data != null) {
                    Message message = gson.fromJson(data, Message.class);
                    messageAdapter.add(message);
                    messagesView.setSelection(messageAdapter.getCount() - 1);
                }
            }
        }

        Pusher pusher = new Pusher(HackApplication.getSecret("pusher.key"));
        Channel channel = pusher.subscribe(getActivity().getIntent().getExtras().getString("EventID"));

        channel.bind("message", new SubscriptionEventListener() {


            @Override
            public void onEvent(String channelName, String eventName, final String data) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Gson gson = new Gson();
                        Message message = gson.fromJson(data, Message.class);
                        messageAdapter.add(message);

                        // have the ListView scroll down to the new message
                        messagesView.setSelection(messageAdapter.getCount() - 1);
                    }

                });
            }

        });

        // connect to the Pusher API
        pusher.connect();

        return view;
    }


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

    @Override
    public void onClick(View v) {
        postMessage();
    }

    private void postMessage() {
        String text = messageRaw.getText().toString();

        // return if the text is blank
        if (text.equals("")) {
            return;
        }


        RequestParams params = new RequestParams();

        // set our JSON object
        params.put("sender", username);
        params.put("message", text);

        // create our HTTP client
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(MESSAGE_ENDPOINT, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        messageRaw.setText("");
                    }
                });
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(
                        getActivity().getApplicationContext(),
                        "Something went wrong :(",
                        Toast.LENGTH_LONG
                ).show();
            }
        });
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
