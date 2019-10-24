package com.cheappartsguy.app.cpg;

import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewImageGalleryActivity extends ArrayAdapter<ImageGalleryItem> {

    private Context context;
    private int layoutResourceId;
    private ArrayList<ImageGalleryItem> data = new ArrayList<ImageGalleryItem>();

    public ViewImageGalleryActivity(Context context, int layoutResourceId, ArrayList<ImageGalleryItem> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    private Context mContext;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.imageTitle = (TextView) row.findViewById(R.id.text);
            holder.imageGrade = (ImageView) row.findViewById(R.id.img_grade);
            holder.image = (ImageView) row.findViewById(R.id.image);

            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        ImageGalleryItem item = data.get(position);
        holder.imageTitle.setText(item.getTitle());

        ShowGradeStatus(holder.imageGrade, item.getGrade());

        holder.image.setImageBitmap(item.getImage());
        holder.image.setMinimumWidth(item.getSizes()[0]);
        holder.image.setMinimumHeight(item.getSizes()[1]);
        return row;
    }

    public void ShowGradeStatus(ImageView grade_img, int grade_id) {
        switch (grade_id) {
            case 5:
                grade_img.setImageResource(R.drawable.emptygbg);
                break;
            case 1:
                grade_img.setImageResource(R.drawable.fullgbg);
                break;
            case 4:
                grade_img.setImageResource(R.drawable.one4gbg);
                break;
            case 3:
                grade_img.setImageResource(R.drawable.one2gbg);
                break;
            case 2:
                grade_img.setImageResource(R.drawable.three4gbg);
                break;
            default:
                grade_img.setImageResource(R.drawable.unknowngbg);
                break;
        }
    }

    static class ViewHolder {
        TextView imageTitle;
        ImageView image, imageGrade;
    }

}
