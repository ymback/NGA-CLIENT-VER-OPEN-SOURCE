package sp.phone.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.FilenameUtils;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

public class ImageUtil {
	static final String LOG_TAG = ImageUtil.class.getSimpleName();
	public static Bitmap zoomImageByWidth(Bitmap bitmap, int bookWidth) {
		if(bitmap == null)
			return null;
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		

		
		int newWidth = bookWidth;
		float newHeight = ((height * newWidth) / width);
		
		if(newWidth <2 || newHeight < 1.01f)
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
		if(bitmap == null)
			return null;
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		

		int newHeight = bookHeight;
		float newWidth = ((width * newHeight) / height);
		
		
		if(newWidth <2 || newHeight < 1.01f)
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
	 * 
	 * @param drawable
	 *            source Drawable
	 * @param bookWidth
	 *          
	 * @return
	 */
	public static Bitmap zoomImageByWidth(Drawable drawable, int bookWidth) {
		if(drawable==null)
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
		if(origBmp != newbmp)
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

	/**
	 * source to Drawable
	 * 
	 * @param activity
	 *           
	 * @param source
	 *            
	 * @return
	 */
	/*public static Drawable reSetDrawable(Activity activity, String source) {
		// System.out.println("source:" + source);
		Drawable drawable = null;
		if (source.startsWith("http://")) {
			try {
				Log.i(LOG_TAG, "fetch from " + source);
				URL url = new URL(source);
				URLConnection conn = url.openConnection();
				conn.setConnectTimeout(1*1000);
				drawable = Drawable.createFromStream(conn.getInputStream(), "");
			} catch (Exception e) {
				return null;
			}

			if (drawable == null)
				drawable = activity.getResources().getDrawable(
						R.drawable.defult_img);
		}else if (source.equals("[s:1]")) {
			drawable = activity.getResources().getDrawable(R.drawable.smile);
		} else if (source.equals("[s:2]")) {
			drawable = activity.getResources().getDrawable(R.drawable.mrgreen);
		} else if (source.equals("[s:3]")) {
			drawable = activity.getResources().getDrawable(R.drawable.question);
		} else if (source.equals("[s:4]")) {
			drawable = activity.getResources().getDrawable(R.drawable.wink);
		} else if (source.equals("[s:5]")) {
			drawable = activity.getResources().getDrawable(R.drawable.redface);
		} else if (source.equals("[s:6]")) {
			drawable = activity.getResources().getDrawable(R.drawable.sad);
		} else if (source.equals("[s:7]")) {
			drawable = activity.getResources().getDrawable(R.drawable.cool);
		} else if (source.equals("[s:8]")) {
			drawable = activity.getResources().getDrawable(R.drawable.crazy);
		} else if (source.equals("[s:34]")) {
			drawable = activity.getResources().getDrawable(R.drawable.a14);
		} else if (source.equals("[s:32]")) {
			drawable = activity.getResources().getDrawable(R.drawable.a12);
		} else if (source.equals("[s:30]")) {
			drawable = activity.getResources().getDrawable(R.drawable.a10);
		} else if (source.equals("[s:29]")) {
			drawable = activity.getResources().getDrawable(R.drawable.a09);
		} else if (source.equals("[s:28]")) {
			drawable = activity.getResources().getDrawable(R.drawable.a08);
		} else if (source.equals("[s:27]")) {
			drawable = activity.getResources().getDrawable(R.drawable.a07);
		} else if (source.equals("[s:26]")) {
			drawable = activity.getResources().getDrawable(R.drawable.a06);
		} else if (source.equals("[s:24]")) {
			drawable = activity.getResources().getDrawable(R.drawable.a04);
		} else if (source.equals("[s:35]")) {
			drawable = activity.getResources().getDrawable(R.drawable.a15);
		} else if (source.equals("[s:36]")) {
			drawable = activity.getResources().getDrawable(R.drawable.a16);
		} else if (source.equals("[s:37]")) {
			drawable = activity.getResources().getDrawable(R.drawable.a17);
		} else if (source.equals("[s:38]")) {
			drawable = activity.getResources().getDrawable(R.drawable.a18);
		} else if (source.equals("[s:39]")) {
			drawable = activity.getResources().getDrawable(R.drawable.a19);
		} else if (source.equals("[s:40]")) {
			drawable = activity.getResources().getDrawable(R.drawable.a20);
		} else if (source.equals("[s:41]")) {
			drawable = activity.getResources().getDrawable(R.drawable.a21);
		} else if (source.equals("[s:42]")) {
			drawable = activity.getResources().getDrawable(R.drawable.a22);
		} else if (source.equals("[s:43]")) {
			drawable = activity.getResources().getDrawable(R.drawable.a23);
		} else {
			//
			drawable = activity.getResources().getDrawable(R.drawable.question);
		}
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable
				.getIntrinsicHeight());
		return drawable;
	}
*/
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
	public static String getAvatarById(String extersion, String userId){
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
				newName = HttpUtil.PATH_AVATAR+ "/" + userId + "."
						+ extension.substring(0, 3);
				
			} else {
				newName = HttpUtil.PATH_AVATAR + "/" + userId + ".jpg";
			}
		} else {
			newName = HttpUtil.PATH_AVATAR + "/" + userId + ".jpg";
		}
		return newName;
	}

	public static ZipFile zf;

	public static InputStream getCacheStream(String userId, String extension) {

		InputStream is = null;
		try {
			System.out.println(HttpUtil.PATH_ZIP);

			if (zf != null) {
				ZipEntry entry = zf.getEntry("avatarImage/" + userId + "."
						+ extension);
				if (entry != null) {
					System.out.println("ZipFile:" + entry.getName());
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
	
	static public String getImageName(String uri){
		if(StringUtil.isEmpty(uri))
			return null;
		String ret = FilenameUtils.getName(uri);
		if(StringUtil.isEmpty(ret))
			return null;
		int pos = ret.indexOf("?");
		if(pos != -1){
			
			ret = ret.substring(0,pos);
		}
		return ret ;
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
	
	//final static int max_avatar_width = 200;
	final static  int max_avatar_height = 255;
	static public Bitmap loadAvatarFromSdcard(String avatarPath)
	{
		
		return loadAvatarFromSdcard(avatarPath,max_avatar_height);
	}
	static public Bitmap loadAvatarFromSdcard(String avatarPath,int maxHeight){

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
        if(bitmap != null &&  bitmap.getWidth() != avatarWidth){
        	Bitmap tmp = bitmap;
        	bitmap = zoomImageByWidth(tmp,avatarWidth);
        	tmp.recycle();
        }
        
        return bitmap;
	}
	static public Bitmap loadAvatarFromStream(InputStream is,InputStream is2)
	{
		return loadAvatarFromStream(is,is2,max_avatar_height);
	}
	static public Bitmap loadAvatarFromStream(InputStream is,InputStream is2,int maxHeight){
		if(is== null)
			return null;
		if(is == is2)
			return null;
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeStream(is, null,opts);
		final int avatarWidth = PhoneConfiguration.getInstance().getNikeWidth();
		
		final int minSideLength = Math.min(avatarWidth, maxHeight);
		opts.inSampleSize = ImageUtil.computeSampleSize(opts, minSideLength,
				avatarWidth * maxHeight);
        opts.inJustDecodeBounds = false;
        opts.inInputShareable = true;
        opts.inPurgeable = true;
        bitmap = BitmapFactory.decodeStream(is2, null, opts);
        if(bitmap != null && bitmap.getWidth() != avatarWidth){
        	Bitmap tmp = bitmap;
        	bitmap = zoomImageByWidth(tmp,avatarWidth);
        	tmp.recycle();
        }
        
        return bitmap;
	}
	
	static public byte[] fitImageToUpload(InputStream is,InputStream is2){
		if(is== null)
			return null;
		if(is == is2)
			return null;
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeStream(is, null,opts);
		final int minSideLength = 512;
		opts.inSampleSize = ImageUtil.computeSampleSize(opts, minSideLength,
				1024*1024);
        opts.inJustDecodeBounds = false;
        opts.inInputShareable = true;
        opts.inPurgeable = true;
        bitmap = BitmapFactory.decodeStream(is2, null, opts);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
        
	}
	
	public static void recycleImageView(ImageView avatarIV){
		
		Drawable drawable = avatarIV.getDrawable();
		if (drawable instanceof BitmapDrawable) {
		    BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
		    Bitmap bitmap = bitmapDrawable.getBitmap();
		    if(bitmap != null)
		    	bitmap.recycle();
		}
	}
	
}
