package org.krupkas.tunamelt;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class DeleteFragment extends DialogFragment {

    public static DeleteFragment newInstance() {
        DeleteFragment frag = new DeleteFragment();
        return frag;
    }
    
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        
        return new AlertDialog.Builder(getActivity())
                .setTitle("Really delete the TunaMelt?")
                .setPositiveButton(android.R.string.yes,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            ((ShowDetails)getActivity()).doPositiveClick();
                        }
                    }
                )
                .setNegativeButton(android.R.string.no,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            ((ShowDetails)getActivity()).doNegativeClick();
                        }
                    }
                )
                .create();
    }
}