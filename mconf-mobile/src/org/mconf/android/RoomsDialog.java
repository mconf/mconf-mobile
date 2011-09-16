package org.mconf.android;

import java.util.List;

import org.mconf.bbb.android.NoScrollListView;
import org.mconf.bbb.android.R;
import org.mconf.web.Room;
import org.mconf.web.Space;
import org.mconf.web.User;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.AdapterView.OnItemClickListener;


public class RoomsDialog extends Dialog {

	private class RoomsDialogOnItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			if (onSelectRoomListener != null)
				onSelectRoomListener.onSelectRoom((Room) parent.getAdapter().getItem(position));
			dismiss();
		}
	}
	
	public interface OnSelectRoomListener {
		public void onSelectRoom(Room room);
	}
	
	private RoomsAdapter rooms_user = new RoomsAdapter();
	private RoomsAdapter rooms_user_spaces = new RoomsAdapter();
	private RoomsAdapter rooms_public_spaces = new RoomsAdapter();
	private OnSelectRoomListener onSelectRoomListener = null;

	public RoomsDialog(Context context, List<Room> rooms) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.rooms_list);
		
		for (Room room : rooms) {
			if (room.getOwner().getClass() == User.class)
				rooms_user.add(room);
			else if (room.getOwner().getClass() == Space.class) {
				Space space = (Space) room.getOwner();
				if (space.isMember())
					rooms_user_spaces.add(room);
				else
					rooms_public_spaces.add(room);
			}
		}

		initView(rooms_user, R.id.rooms_user, R.id.layout_rooms_user);
		initView(rooms_user_spaces, R.id.rooms_user_spaces, R.id.layout_rooms_user_spaces);
		initView(rooms_public_spaces, R.id.rooms_public_spaces, R.id.layout_rooms_public_spaces);
	}
	
	void initView(RoomsAdapter adapter, int listViewId, int layoutId) {
		if (adapter.isEmpty()) {
			RelativeLayout layout = (RelativeLayout) findViewById(layoutId);
			LayoutParams params = layout.getLayoutParams();
			params.height = 0;
			layout.setLayoutParams(params);
		} else {
			NoScrollListView listView = (NoScrollListView) findViewById(listViewId);
			listView.setAdapter(adapter);
			listView.setOnItemClickListener(new RoomsDialogOnItemClickListener());
		}		
	}
	
	void setOnSelectRoomListener(OnSelectRoomListener listener) {
		onSelectRoomListener = listener;
	}
}
