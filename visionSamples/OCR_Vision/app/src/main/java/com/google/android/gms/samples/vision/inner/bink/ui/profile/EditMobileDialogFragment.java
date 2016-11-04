package com.google.android.gms.samples.vision.inner.bink.ui.profile;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.loyaltyangels.bink.R;
import com.loyaltyangels.bink.model.user.User;
import com.loyaltyangels.bink.ui.wallet.fragments.BaseDialogFragment;
import com.trello.rxlifecycle.FragmentEvent;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by jmcdonnell on 12/09/2016.
 */

public class EditMobileDialogFragment extends BaseDialogFragment {

    private static final String EXTRA_USER = "user";

    public interface Listener {
        void onUserUpdated(User user);
    }

    public static EditMobileDialogFragment newInstance(@NonNull User user) {
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_USER, user);

        EditMobileDialogFragment fragment = new EditMobileDialogFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @BindView(R.id.mobile_number)
    EditText mobileNumber;

    @BindView(R.id.progress)
    ProgressBar progress;

    @BindView(R.id.save)
    Button save;

    private Listener listener;

    @Override
    protected int getLayoutRes() {
        return R.layout.layout_edit_mobile_dialog;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            User user = getArguments().getParcelable(EXTRA_USER);

            if (user != null && !TextUtils.isEmpty(user.getPhone())) {
                mobileNumber.setText(user.getPhone());
                mobileNumber.setSelection(user.getPhone().length());
            }
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        /**
         * Keyboard is not automatically shown.
         */
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        return dialog;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @OnEditorAction(R.id.mobile_number)
    boolean onMobileEditorAction() {
        saveMobile();
        return true;
    }

    @OnClick(R.id.save)
    void onSaveClicked() {
        saveMobile();
    }

    private void saveMobile() {
        if (!TextUtils.isEmpty(mobileNumber.getText())) {
            User user = new User();
            user.setPhone(mobileNumber.getText().toString());

            save.setVisibility(View.INVISIBLE);
            progress.setVisibility(View.VISIBLE);

            model.updateUser(user)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .compose(bindUntilEvent(FragmentEvent.STOP))
                    .subscribe(updated -> {
                        if (listener != null) {
                            listener.onUserUpdated(updated);
                            dismiss();
                        }
                    }, error -> {
                        error.printStackTrace();
                        progress.setVisibility(View.INVISIBLE);
                        save.setVisibility(View.VISIBLE);
                    });

        }
    }
}
