package org.mconf.bbb.android.mconf;

import org.mconf.bbb.android.CustomListview;
import org.mconf.bbb.android.R;
import org.mconf.web.Room;

import android.app.Dialog;
import android.content.Context;


public class RoomsDialog extends Dialog {

	private RoomsAdapter rooms_user = new RoomsAdapter();
	private RoomsAdapter rooms_user_spaces = new RoomsAdapter();
	private RoomsAdapter rooms_public_spaces = new RoomsAdapter();
	
	public RoomsDialog(Context context) {
		super(context);
		setContentView(R.layout.rooms_list);
		setTitle("Rooms");
		
		for (int i = 0; i < 10; ++i) {
			rooms_user.add(new Room("rooms_user " + i, "..."));
			rooms_user_spaces.add(new Room("rooms_user_spaces " + i, "..."));
			rooms_public_spaces.add(new Room("rooms_public_spaces " + i, "..."));
		}
		
		final CustomListview view_rooms_user = (CustomListview) findViewById(R.id.rooms_user);
		view_rooms_user.setAdapter(rooms_user);
		view_rooms_user.setHeight();
		final CustomListview view_rooms_user_spaces = (CustomListview) findViewById(R.id.rooms_user_spaces);
		view_rooms_user_spaces.setAdapter(rooms_user_spaces);
		view_rooms_user_spaces.setHeight();
		final CustomListview view_rooms_public_spaces = (CustomListview) findViewById(R.id.rooms_public_spaces);
		view_rooms_public_spaces.setAdapter(rooms_public_spaces);
		view_rooms_public_spaces.setHeight();
	}

}
