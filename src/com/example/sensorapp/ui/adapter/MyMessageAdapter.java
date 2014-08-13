/**
 * 
 */
package com.example.sensorapp.ui.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sensorapp.R;
import com.example.sensorapp.message.SmsMessage;

/**
 * @author Administrator
 * 
 */
public class MyMessageAdapter extends BaseAdapter {
	private List<SmsMessage> msgList;
	private LayoutInflater inflater;
	private Context context;

	/**
	 * 
	 */
	public MyMessageAdapter(Context context, List<SmsMessage> msg) {
		// TODO Auto-generated constructor stub
		this.context = context;
		inflater = LayoutInflater.from(context);
		this.msgList = msg;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return msgList == null ? 0 : msgList.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return msgList.get(position);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return msgList.get(position).getId();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.sms_list_item1, null);
			holder = new ViewHolder();
			holder.address = (TextView) convertView.findViewById(R.id.sender);
			holder.body = (TextView) convertView.findViewById(R.id.body);
			holder.icon = (ImageView) convertView.findViewById(R.id.icon);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.address.setText("发件人:" + msgList.get(position).getNickName());
		holder.body.setText("内容:" + msgList.get(position).getBody());
		return convertView;
	}

	class ViewHolder {
		public ImageView icon;
		public TextView address;
		public TextView body;
	}
}
