package org.trinity.wallet.activity.card;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.trinity.wallet.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String CARD_NO = "card_no";

    public PlaceholderFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static PlaceholderFragment newInstance(int cardNo) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle args = new Bundle();
        args.putInt(CARD_NO, cardNo);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_card, container, false);
        ImageView imageView = rootView.findViewById(R.id.cardIcon);
        TextView textView = rootView.findViewById(R.id.cardTextView);
        int cardNo = getArguments().getInt(CARD_NO);
        switch (cardNo) {
            case 1:
                imageView.setImageResource(R.mipmap.ic_tnc);
                textView.setText(getString(R.string.card_text, "TNC"));
                break;
            case 2:
                imageView.setImageResource(R.mipmap.ic_neo);
                textView.setText(getString(R.string.card_text, "NEO"));
                break;
            case 3:
                imageView.setImageResource(R.mipmap.ic_gas);
                textView.setText(getString(R.string.card_text, "GAS"));
                break;
        }
        return rootView;
    }
}
