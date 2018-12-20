package sp.phone.fragment.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.ImageView;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.gallery.ImageZoomActivity;
import sp.phone.util.ImageUtils;

public class AvatarDialogFragment extends BaseDialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        Bundle args = getArguments();
        if (args == null) {
            return super.onCreateDialog(savedInstanceState);
        }
        String name = args.getString("name");
        String url = args.getString("url");

        ImageView avatarView = new ImageView(getContext());
        int padding = getResources().getDimensionPixelSize(R.dimen.material_standard);
        avatarView.setPadding(0, padding, 0, 0);
        ImageUtils.loadAvatar(avatarView, url);
        if (!TextUtils.isEmpty(url)) {
            avatarView.setOnClickListener(v -> {
                Intent intent = new Intent();
                intent.putExtra(ImageZoomActivity.KEY_GALLERY_CUR_URL, url);
                intent.setClass(getContext(), ImageZoomActivity.class);
                getContext().startActivity(intent);
            });
        }

        builder.setTitle(name + "的头像")
                .setView(avatarView)
                .setPositiveButton("关闭", null);

        return builder.create();
    }

}
