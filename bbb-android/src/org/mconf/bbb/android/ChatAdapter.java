package org.mconf.bbb.android;

import java.util.ArrayList;
import java.util.List;

import org.mconf.bbb.chat.ChatMessage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ChatAdapter extends BaseAdapter {
	
	private List<ChatMessage> list = new ArrayList<ChatMessage>();
	private Context context;
	
	public ChatAdapter(Context context) {
		this.context = context;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        ChatMessage entry = (ChatMessage) getItem(position);
        
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.chat_message, null);
        }

        TextView chat_user = (TextView) convertView.findViewById(R.id.chat_user);
    	chat_user.setText(entry.getUsername() + ":");
        if (position > 0 && entry.getUserId() == list.get(position-1).getUserId()) {
        	chat_user.setMaxLines(0);
        } else {
        	chat_user.setMaxLines(200);
        }
        ((TextView) convertView.findViewById(R.id.chat_message)).setText(entry.getMessage());
        ((TextView) convertView.findViewById(R.id.chat_time)).setText(entry.getTime());

        return convertView;
    }

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	public void add(ChatMessage message) {
		list.add(message);
	}
	
	public void remove(ChatMessage message) {
		list.remove(message);
	}

}
