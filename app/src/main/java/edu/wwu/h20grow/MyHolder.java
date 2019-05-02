package edu.wwu.h20grow;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    ImageView img;
    TextView nameTxt;
    TextView waterTxt;
    RelativeLayout cardLayout;
    ImageView plantColorIcon;


    ItemClickListener itemClickListener;


    public MyHolder(View itemView) {
        super(itemView);

        this.img= (ImageView) itemView.findViewById(R.id.plantImage);
        this.nameTxt= (TextView) itemView.findViewById(R.id.nameTxt);
        this.waterTxt= (TextView) itemView.findViewById(R.id.waterTxt);
        this.cardLayout = (RelativeLayout) itemView.findViewById(R.id.card_layout);
        this.plantColorIcon = (ImageView) itemView.findViewById(R.id.plant_color_icon);

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        this.itemClickListener.onItemClick(v,getLayoutPosition());
    }

    public void setItemClickListener(ItemClickListener ic)
    {
        this.itemClickListener=ic;
    }
}
