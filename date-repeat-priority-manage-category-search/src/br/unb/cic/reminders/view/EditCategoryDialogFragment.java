package br.unb.cic.reminders.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import br.unb.cic.framework.persistence.DBException;
import br.unb.cic.reminders.controller.Controller;
import br.unb.cic.reminders.model.Category;
import br.unb.cic.reminders.model.InvalidTextException;
import br.unb.cic.reminders2.R;

public class EditCategoryDialogFragment extends DialogFragment {

    Category category;

    public static EditCategoryDialogFragment newInstance(Category category) {
        EditCategoryDialogFragment fragment = new EditCategoryDialogFragment();
        fragment.setCategory(category);
        return fragment;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.dialog_editcategory_title);
        LayoutInflater inflater = getActivity().getLayoutInflater();

        final View view = inflater.inflate(R.layout.category_dialog, null);

        EditText edtCategoryName = ( EditText )
                view.findViewById(R.id.dialog_category);
        edtCategoryName.setText(category.getName());

        builder.setView(view)
                .setPositiveButton(R.string.dialog_editcategory_save, new
                        DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                EditText edtCategoryName = ( EditText )
                                        view.findViewById(R.id.dialog_category);
                                try {
                                    category.setName(edtCategoryName.getText().toString());
                                    Controller.instance(getActivity()).updateCategory(category);
                                }
                                catch(InvalidTextException e) {
                                    Log.e("CategoryDialogFragment", e.getMessage());
                                    e.printStackTrace();
                                    Toast.makeText(getActivity().getApplicationContext(),
                                            "Invalid category.", Toast.LENGTH_SHORT).show();
                                }
                                catch(DBException e) {
                                    Log.e("CategoryDialogFragment", e.getMessage());
                                    e.printStackTrace();
                                }
                                catch(Exception e) {
                                    Log.e("CategoryDialogFragment", e.getMessage());
                                    e.printStackTrace();
                                }
                            }
                        })
                .setNegativeButton(R.string.dialog_category_cancel, new
                        DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                EditCategoryDialogFragment.this.getDialog().cancel();
                            }
                        });

        return builder.create();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().recreate();
    }

}
