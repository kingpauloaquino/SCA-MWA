package com.cheappartsguy.app.cpg;

/**
 * Created by king.a on 11/28/2016.
 */
import android.graphics.Bitmap;

public class ImageGalleryItem {

    private Bitmap image;
    private String title;
    private int grade;
    private String url;

    private int width;
    private int height;

    public ImageGalleryItem(Bitmap image, String title, int grade, String url, int[] image_size) {
        super();
        this.image = image;
        this.title = title;
        this.grade = grade;
        this.url = url;

        this.width = image_size[0];
        this.height = image_size[1];
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int[] getSizes() {
        return new int[] { width, height };
    }

    public void setSizes(int[] Sizes) {
        this.width = Sizes[0];
        this.height = Sizes[1];
    }
}
