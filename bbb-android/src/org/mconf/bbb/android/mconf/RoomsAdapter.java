package org.mconf.bbb.android.mconf;

import java.util.ArrayList;
import java.util.List;

import org.mconf.bbb.android.R;
import org.mconf.web.Room;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class RoomsAdapter extends BaseAdapter {
	List<Room> rooms = new ArrayList<Room>();
	
	@Override
	public int getCount() {
		return rooms.size();
	}

	@Override
	public Object getItem(int position) {
		return rooms.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.rooms_list_item, null);
        }
        
        Room room = rooms.get(position);
        
        final TextView textViewRoomName = (TextView) convertView.findViewById(R.id.room_name);
        textViewRoomName.setText(room.getName());
        
        return convertView;
	}

	public void add(Room room) {
		rooms.add(room);
	}
}
