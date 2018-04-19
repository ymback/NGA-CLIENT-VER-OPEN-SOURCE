package gov.anzong.androidnga.gallery;

import android.graphics.Rect;

public class GalleryData {

	private Rect imageRect;
	private Rect pointsRect;
	private Rect galleryRect;
	private int count;
	private String[] files;
	private String[] imgTexts;
	private int imgBgColor;

	private boolean hasImgDesc = false;

	public Rect getImageRect() {
		return imageRect;
	}

	public void setImageRect(Rect imageRect) {
		this.imageRect = imageRect;
	}

	public Rect getPointsRect() {
		return pointsRect;
	}

	public void setPointsRect(Rect pointsRect) {
		this.pointsRect = pointsRect;
	}
	
	public Rect getGalleryRect() {
		return galleryRect;
	}

	public void setGalleryRect(Rect galleryRect) {
		this.galleryRect = galleryRect;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String[] getFiles() {
		return files;
	}

	public void setFiles(String[] files) {
		this.files = files;
	}

	public boolean isHasImgDesc() {
		return hasImgDesc;
	}

	public void setHasImgDesc(boolean hasImgDesc) {
		this.hasImgDesc = hasImgDesc;
	}
	
	@Override
	public String toString() {
		StringBuffer sbf = new StringBuffer();
		sbf.append(imageRect);
		sbf.append(", images:" + (files == null ? 0 : files.length));
		return sbf.toString();
	}

	public String[] getImgTexts() {
		return imgTexts;
	}

	public void setImgTexts(String[] imgTexts) {
		this.imgTexts = imgTexts;
	}

	public int getImgBgColor() {
		return imgBgColor;
	}

	public void setImgBgColor(int imgBgColor) {
		this.imgBgColor = imgBgColor;
	}
	
}
