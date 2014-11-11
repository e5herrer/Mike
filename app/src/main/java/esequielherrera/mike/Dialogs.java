package esequielherrera.mike;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;

import java.util.concurrent.Callable;

/**
 * Created by esequielherrera-ortiz on 11/10/14.
 */
public class Dialogs extends DialogFragment {

    public static AlertDialog confirmation(final Context context){

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.confirmation_goBack)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage(R.string.lose_data_warning)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ((MainActivity) context).finishFragment();
                    }
                })
                .setNegativeButton(R.string.no, null);
        // Create the AlertDialog object and return it
        return builder.create();
    }

}
