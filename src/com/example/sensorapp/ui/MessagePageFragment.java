/**
 * 
 */
package com.example.sensorapp.ui;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.sensorapp.R;
import com.example.sensorapp.message.SmsMessage;
import com.example.sensorapp.message.SmsUtil;
import com.example.sensorapp.ui.adapter.MyMessageAdapter;

/**
 * @author Administrator
 * 
 */
public class MessagePageFragment extends Fragment implements
		OnItemClickListener {

	private View view;
	private SuperFragmentActivity activity;

	private List<SmsMessage> msgList;
	private ImageView closeBtn;
	private ListView msgListView;

	private View msgDetailLayout;
	private TextView msgAddrTextView;
	private TextView msgDetailTextView;

	private View.OnClickListener clickListener;

	public void setOnClickListener(View.OnClickListener listener) {
		this.clickListener = listener;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup root,
			Bundle bundle) {
		view = inflater.inflate(R.layout.messageview, null);
		activity = (SuperFragmentActivity) getActivity();
		init();
		return view;
	}

	private void init() {
		// List<SmsMessage> msgList0 = new SmsUtil(activity).readSMS(10);
		Thread t = new Thread() {
			public void run() {
				List<SmsMessage> list;
				list = new SmsUtil(activity).readSMS(10);// 先读10条.
				Message msg = handler.obtainMessage(0);
				msg.obj = list;
				msg.sendToTarget();

				List<SmsMessage> list1 = new SmsUtil(activity).readSMS(-1);
				Message msg1 = handler.obtainMessage(0);
				msg1.obj = list1;
				msg1.sendToTarget();

			};
		};
		t.start();
		msgListView = (ListView) view.findViewById(R.id.msgList);
		closeBtn = (ImageView) view.findViewById(R.id.closeBtn);
		closeBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (msgDetailLayout.getVisibility() == View.VISIBLE) {
					msgDetailLayout.setVisibility(View.INVISIBLE);
					msgListView.setVisibility(View.VISIBLE);
					return;
				}

				activity.removeFragment(MessagePageFragment.this);
				if (clickListener != null) {
					clickListener.onClick(closeBtn);
				}
			}
		});

		msgAddrTextView = (TextView) view.findViewById(R.id.addressTextView);
		msgDetailLayout = view.findViewById(R.id.msgDetail);
		msgDetailTextView = (TextView) view
				.findViewById(R.id.msgDetailTextView);

		msgListView.setOnItemClickListener(this);
	}

	private final Handler handler = new Handler() {
		public void dispatchMessage(Message msg) {
			switch (msg.what) {
			case 0: {
				List<SmsMessage> list = (List<SmsMessage>) msg.obj;
				MyMessageAdapter adapter = new MyMessageAdapter(activity, list);
				msgListView.setAdapter(adapter);
			}
				break;

			default:
				break;
			}
		}
	};

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		SmsMessage msg = (SmsMessage) parent.getAdapter().getItem(position);
		if (msg != null) {
			msgAddrTextView.setText("发件人:    " + msg.getNickName());
			msgDetailTextView.setText("内容:    " + msg.getBody());
			msgDetailLayout.setVisibility(View.VISIBLE);
			parent.setVisibility(View.INVISIBLE);
		}
	}
}
