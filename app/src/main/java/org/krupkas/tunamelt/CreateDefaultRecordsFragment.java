package org.krupkas.tunamelt;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class CreateDefaultRecordsFragment extends DialogFragment {

    public static CreateDefaultRecordsFragment newInstance() {
        CreateDefaultRecordsFragment frag = new CreateDefaultRecordsFragment();
        return frag;
    }
    
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        
        return new AlertDialog.Builder(getActivity())
                .setTitle("Really insert the default records?")
                .setPositiveButton(android.R.string.yes,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            ((MainDebug)getActivity()).addDefaultRecords();
                        }
                    }
                )
                .setNegativeButton(android.R.string.no,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            ((MainDebug)getActivity()).noop();
                        }
                    }
                )
                .create();
    }
}