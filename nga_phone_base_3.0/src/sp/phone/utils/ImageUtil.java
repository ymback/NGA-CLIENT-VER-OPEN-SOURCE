package sp.phone.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import org.apache.commons.io.FilenameUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.util.GlideApp;
import sp.phone.common.PhoneConfiguration;

public class ImageUtil {
    static final String LOG_TAG = ImageUtil.class.getSimpleName();
    //final static int max_avatar_width = 200;
    final static int max_avatar_height = 255;
    public static ZipFile zf;

    private static Drawable sDefaultAvatar;

    public static Bitmap zoomImageByWidth(Bitmap bitmap, int bookWidth) {
        if (bitmap == null)
            return null;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();


        int newWidth = bookWidth;
        float newHeight = ((height * newWidth) / width);

        if (newWidth < 2 || newHeight < 1.01f)
            return null;

        float scaleWidth = 1f * newWidth / width;
        float scaleHeight = newHeight / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height,
                matrix, true);
        return resizedBitmap;

    }

    public static Bitmap zoomImageByHeight(Bitmap bitmap, int bookHeight) {
        if (bitmap == null)
            return null;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();


        int newHeight = bookHeight;
        float newWidth = ((width * newHeight) / height);


        if (newWidth < 2 || newHeight < 1.01f)
            return null;

        float scaleWidth = 1f * newWidth / width;
        float scaleHeight = 1f * newHeight / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height,
                matrix, true);
        return resizedBitmap;

    }

    /**
     * @param drawable  source Drawable
     * @param bookWidth
     * @return
     */
    public static Bitmap zoomImageByWidth(Drawable drawable, int bookWidth) {
        if (drawable == null)
            return null;

		/*int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();



		int newWidth = width;
		int newHeight = height;

		//if (width > bookWidth) {
			newWidth = bookWidth;
			newHeight = (height * newWidth) / width;
		//}

		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;

		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);*/


        Bitmap origBmp = drawableToBitmap(drawable);
        Bitmap newbmp = zoomImageByWidth(origBmp, bookWidth);
        if (origBmp != newbmp)
            origBmp.recycle();
        return newbmp;
    }

    /**
     * convert Drawable to Bitmap
     *
     * @param drawable
     * @return
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(width, height, config);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return bitmap;
    }

    public static String newImage2(String oldImage, String userId) {
        if (oldImage.indexOf(".") != -1) {
            String fileType = oldImage.substring(oldImage.lastIndexOf("."),
                    oldImage.length());
            if (fileType.indexOf("?") != -1) {
                fileType = fileType.split("\\?")[0];
            }
            String lf = HttpUtil.PATH_AVATAR + "/" + userId + fileType;
            return lf;
        } else {
            return null;
        }
    }

    public static String getAvatarById(String extersion, String userId) {
        return HttpUtil.PATH_AVATAR + "/" + userId + "." + extersion;
    }

    public static String newImage(String oldImage, String userId) {
        String extension = FilenameUtils.getExtension(oldImage);
        String path = FilenameUtils.getPath(oldImage);
        String newName;
        if (extension != null) {
            if (path == null || "".equals(path)) {
                return null;
            } else if (extension.length() == 3) {
                newName = HttpUtil.PATH_AVATAR + "/" + userId + "." + extension;

            } else if (extension.length() >= 4
                    && "?".equals(extension.substring(3, 4))) {
                newName = HttpUtil.PATH_AVATAR + "/" + userId + "."
                        + extension.substring(0, 3);

            } else {
                newName = HttpUtil.PATH_AVATAR + "/" + userId + ".jpg";
            }
        } else {
            newName = HttpUtil.PATH_AVATAR + "/" + userId + ".jpg";
        }
        return newName;
    }

    public static InputStream getCacheStream(String userId, String extension) {

        InputStream is = null;
        try {

            if (zf != null) {
                ZipEntry entry = zf.getEntry("avatarImage/" + userId + "."
                        + extension);
                if (entry != null) {
                    is = zf.getInputStream(entry);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return is;

    }

    public static String getImageType(String uri) {

        String extension = FilenameUtils.getExtension(uri);
        if (extension.length() > 3 && extension.indexOf("?") == 3) {
            extension = extension.substring(0, 3);
        }
        if (extension.length() == 3) {
            return extension;
        } else {
            return null;
        }
    }

    static public String getImageName(String uri) {
        if (StringUtils.isEmpty(uri))
            return null;
        String ret = FilenameUtils.getName(uri);
        if (StringUtils.isEmpty(ret))
            return null;
        int pos = ret.indexOf("?");
        if (pos != -1) {

            ret = ret.substring(0, pos);
        }
        return ret;
    }

    private static int computeSampleSize(BitmapFactory.Options options,
                                         int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength,
                maxNumOfPixels);

        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }

        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options,
                                                int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;

        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
                .sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math
                .floor(w / minSideLength), Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }

        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    static public Bitmap loadAvatarFromSdcard(String avatarPath) {

        return loadAvatarFromSdcard(avatarPath, max_avatar_height);
    }

    static public Bitmap loadAvatarFromSdcard(String avatarPath, int maxHeight) {

        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(avatarPath, opts);
        final int avatarWidth = PhoneConfiguration.getInstance().getNikeWidth();

        final int minSideLength = Math.min(avatarWidth, maxHeight);
        opts.inSampleSize = ImageUtil.computeSampleSize(opts, minSideLength,
                avatarWidth * maxHeight);
        opts.inJustDecodeBounds = false;
        opts.inInputShareable = true;
        opts.inPurgeable = true;
        bitmap = BitmapFactory.decodeFile(avatarPath, opts);
        if (bitmap != null && bitmap.getWidth() != avatarWidth) {
            Bitmap tmp = bitmap;
            bitmap = zoomImageByWidth(tmp, avatarWidth);
            tmp.recycle();
        }

        return bitmap;
    }

    @SuppressWarnings("ResourceType")
    public static Bitmap loadDefaultAvatar() {
        Resources res = ApplicationContextHolder.getResources();
        InputStream is = res.openRawResource(R.drawable.default_avatar);
        InputStream is2 = res.openRawResource(R.drawable.default_avatar);
        return loadAvatarFromStream(is, is2);
    }

    static public Bitmap loadAvatarFromStream(InputStream is, InputStream is2) {
        return loadAvatarFromStream(is, is2, max_avatar_height);
    }

    static public Bitmap loadAvatarFromStream(InputStream is, InputStream is2, int maxHeight) {
        if (is == null)
            return null;
        if (is == is2)
            return null;
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        final int avatarWidth = PhoneConfiguration.getInstance().getNikeWidth();

        final int minSideLength = Math.min(avatarWidth, maxHeight);
        opts.inSampleSize = ImageUtil.computeSampleSize(opts, minSideLength,
                avatarWidth * maxHeight);
        opts.inJustDecodeBounds = false;
        opts.inInputShareable = true;
        opts.inPurgeable = true;
        Bitmap bitmap = BitmapFactory.decodeStream(is2, null, opts);
        if (bitmap != null && bitmap.getWidth() != avatarWidth) {
            Bitmap tmp = bitmap;
            bitmap = zoomImageByWidth(tmp, avatarWidth);
            tmp.recycle();
        }
        return bitmap;
    }

    static public byte[] fitImageToUpload(InputStream is, InputStream is2) {
        if (is == null)
            return null;
        if (is == is2)
            return null;
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream(is, null, opts);
        final int minSideLength = 512;
        opts.inSampleSize = ImageUtil.computeSampleSize(opts, minSideLength,
                1024 * 1024);
        opts.inJustDecodeBounds = false;
        opts.inInputShareable = true;
        opts.inPurgeable = true;
        bitmap = BitmapFactory.decodeStream(is2, null, opts);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.PNG, 100, stream);
        return stream.toByteArray();

    }


    static public byte[] fitNGAImageToUpload(InputStream is, BitmapFactory.Options opts) {
        if (is == null)
            return null;

        int width = opts.outWidth;
        int height = opts.outHeight;

        if (height > 255) {
            if (width <= 180) {
                width = (int) (255 * width / height);
                height = 255;
            } else {
                if (((float) (height / width)) > ((float) (255 / 180))) {
                    width = (int) (255 * width / height);
                    height = 255;
                } else if (((float) (height / width)) < ((float) (255 / 180))) {
                    height = (int) (180 * height / width);
                    width = 180;
                } else {
                    height = 255;
                    width = 180;
                }
            }
        } else {
            if (width > 180) {
                height = (int) (180 * height / width);
                width = 180;
            }
        }

        int widthchuli = 1, heightchuli = 1;
        if (opts.outWidth % width == 0) {
            widthchuli = opts.outWidth / width;
        } else {
            widthchuli = opts.outWidth / width + 1;
        }
        if (opts.outHeight % height == 0) {
            heightchuli = opts.outHeight / height;
        } else {
            heightchuli = opts.outHeight / height + 1;
        }
        opts.inSampleSize = Math.max(widthchuli, heightchuli);
        opts.inJustDecodeBounds = false;
        opts.inPurgeable = true;
        Bitmap bitmap = BitmapFactory.decodeStream(is, null, opts);
        if (opts.outHeight > 255 || opts.outWidth > 180)
            bitmap = Bitmap.createScaledBitmap(bitmap, width, height,
                    true);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    public static void recycleImageView(ImageView avatarIV) {

        Drawable drawable = avatarIV.getDrawable();
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            Bitmap bitmap = bitmapDrawable.getBitmap();
            if (bitmap != null)
                bitmap.recycle();
        }
    }

    public static Bitmap toRoundCorner(Bitmap bitmap, float ratio) { // 绝无问题
        if (bitmap.getWidth() > bitmap.getHeight()) {
            bitmap = Bitmap.createBitmap(bitmap,
                    (bitmap.getWidth() - bitmap.getHeight()) / 2, 0,
                    bitmap.getHeight(), bitmap.getHeight());
        } else if (bitmap.getWidth() < bitmap.getHeight()) {
            bitmap = Bitmap.createBitmap(bitmap, 0,
                    (bitmap.getHeight() - bitmap.getWidth()) / 2,
                    bitmap.getWidth(), bitmap.getWidth());
        }
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawRoundRect(rectF, bitmap.getWidth() / ratio,
                bitmap.getHeight() / ratio, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    public static void loadRoundCornerAvatar(ImageView imageView, String url) {
        Context context = ApplicationContextHolder.getContext();
        if (sDefaultAvatar == null) {
            Bitmap defaultAvatar = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_avatar);
            sDefaultAvatar = new BitmapDrawable(context.getResources(), ImageUtil.toRoundCorner(defaultAvatar, 2));
        }
        GlideApp.with(ApplicationContextHolder.getContext())
                .load(url)
                .placeholder(sDefaultAvatar)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .transforms(new BitmapTransformation() {
                    @Override
                    protected Bitmap transform(@NonNull BitmapPool bitmapPool, @NonNull Bitmap bitmap, int i, int i1) {
                        Bitmap roundBitmap = ImageUtil.toRoundCorner(bitmap, 2);
                        bitmapPool.put(roundBitmap);
                        return roundBitmap;
                    }

                    @Override
                    public void updateDiskCacheKey(MessageDigest messageDigest) {

                    }
                })
                .into(imageView);
    }

}
