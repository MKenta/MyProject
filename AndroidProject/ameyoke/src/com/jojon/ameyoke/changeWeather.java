package com.jojon.ameyoke;

import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;


public class changeWeather{
	Handler guiHandler=new Handler();
	public changeWeather(MainActivity context,String estimate,int power){
		TextView textView=(TextView)context.findViewById(R.id.rainTime);
		ImageView imageView=(ImageView)context.findViewById(R.id.power);
		
		switch (power) {
		case 0:
			imageView.setImageResource(R.drawable.x1);
			break;
		case 1:
			imageView.setImageResource(R.drawable.s1);
			break;
		case 2:
			imageView.setImageResource(R.drawable.m1);
			break;
		case 3:
			imageView.setImageResource(R.drawable.l1);
			break;
		default:
			break;
		}
		textView.setText(estimate);
	}
}