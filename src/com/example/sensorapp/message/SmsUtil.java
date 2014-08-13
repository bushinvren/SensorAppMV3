/**
 * 
 */
package com.example.sensorapp.message;

import java.util.ArrayList;
import java.util.List;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;

/**
 * @author Administrator
 * 
 */
public class SmsUtil {
	private final static Uri SMS_INBOX = Uri.parse("content://sms/inbox");;
	private static List<SmsMessage> msgList = new ArrayList<SmsMessage>();
	private Activity context;

	/**
	 * 
	 */
	public SmsUtil(Activity context) {
		// TODO Auto-generated constructor stub
		this.context = context;

	}

	public List<SmsMessage> readSMS(int count) {

		return updateMsgList(count);
	}

	private List<SmsMessage> updateMsgList(int limit) {
		Cursor cursor = context.managedQuery(SMS_INBOX, new String[] {
				"address", "person", "body", "thread_id", "date", "read",
				"status", "type" }, null, null, "date DESC "
				+ (limit <= 0 ? "" : "limit " + limit));
		List<SmsMessage> list = new ArrayList<SmsMessage>();
		if (cursor == null)
			return list;
		if (cursor.moveToFirst()) {
			int addrIdx = cursor.getColumnIndexOrThrow("address");
			int personIdx = cursor.getColumnIndexOrThrow("person");
			int bodyIdx = cursor.getColumnIndexOrThrow("body");
			int thread_id = cursor.getColumnIndexOrThrow("thread_id");
			int date_id = cursor.getColumnIndexOrThrow("date");
			int read_id = cursor.getColumnIndexOrThrow("read");
			int type_id = cursor.getColumnIndexOrThrow("type");

			do {
				SmsMessage message = new SmsMessage();

				String addr = cursor.getString(addrIdx);
				String person = cursor.getString(personIdx);
				String body = cursor.getString(bodyIdx);
				long thread = cursor.getLong(thread_id);
				long date = cursor.getLong(date_id);
				int read = cursor.getInt(read_id);
				int type = cursor.getInt(type_id);
				String nickNameString = getContactName(addr);

				message.setAddr(addr);
				message.setPerson(person);
				message.setBody(body);
				message.setDate(date);
				message.setReadFlag(read);
				message.setType(type);
				message.setNickName(nickNameString);

				list.add(message);

			} while (cursor.moveToNext());
			// cursor.close();
		}
		cursor.close();
		return list;
	}

	static final String[] projection = {
			ContactsContract.PhoneLookup.DISPLAY_NAME,
			ContactsContract.CommonDataKinds.Phone.NUMBER };

	/*
	 * 根据电话号码取得联系人姓名
	 */
	public String getContactName(String number) {

		// 将自己添加到 msPeers 中
		Cursor cursor = context.getContentResolver().query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
				projection, // Which
							// columns
							// to
							// return.
				ContactsContract.CommonDataKinds.Phone.NUMBER + " = '" + number
						+ "'", // WHERE
								// clause.
				null, // WHERE clause value substitution
				null); // Sort order.

		if (cursor == null) {
			return number;
		}
		if (cursor.moveToFirst()) {
			int nameFieldColumnIndex = cursor
					.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
			String name = cursor.getString(nameFieldColumnIndex);
			cursor.close();
			return name;
		}
		cursor.close();
		return number;

	}
}
