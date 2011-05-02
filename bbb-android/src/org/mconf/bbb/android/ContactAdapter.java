/*
 * GT-Mconf: Multiconference system for interoperable web and mobile
 * http://www.inf.ufrgs.br/prav/gtmconf
 * PRAV Labs - UFRGS
 * 
 * This file is part of Mconf-Mobile.
 *
 * Mconf-Mobile is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Mconf-Mobile is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Mconf-Mobile.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.mconf.bbb.android;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.mconf.bbb.users.IParticipant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class ContactAdapter extends BaseAdapter {
	//adapter on the contacts list
	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(ContactAdapter.class);
	private Context context;
	View view;

	//list of contacts on the meeting
	private List<IParticipant> listContact = new ArrayList<IParticipant>();

	public ContactAdapter(Context context) {
		this.context = context;
	}

	public void addSection(IParticipant participant) {
		Contact contact = new Contact(participant);
		if(getUserById(contact.getUserId())==null) //only add if the user is not on the list
			listContact.add(contact);
	}

	public void removeSection(IParticipant participant){
		Contact contact = getUserById(participant.getUserId());
		if (contact != null)
			listContact.remove(contact);
	}

	public void setPrivateChat(Contact contact)
	{
		ImageView privateChat = (ImageView) view.findViewById(R.id.private_chat);
		if(contact.getChatStatus()==Contact.CONTACT_ON_PRIVATE_MESSAGE)
		{
			privateChat.setImageDrawable(this.context.getResources().getDrawable(R.drawable.balloon2));
			privateChat.setVisibility(ImageView.VISIBLE);
		}
		else
			privateChat.setVisibility(ImageView.INVISIBLE);
	}

	public void setPresenterStatus(Contact changedStatus)
	{
		ImageView presenter = (ImageView) view.findViewById(R.id.presenter);
		if(changedStatus.isPresenter())
		{
			presenter.setImageDrawable(this.context.getResources().getDrawable(R.drawable.presenter));
			presenter.setVisibility(ImageView.VISIBLE);
		}
		else
			presenter.setVisibility(ImageView.INVISIBLE);

	}

	public void setStreamStatus( Contact changedStatus)
	{
		ImageView stream = (ImageView) view.findViewById(R.id.stream);
		if(changedStatus.hasStream())
		{
			stream.setImageDrawable(this.context.getResources().getDrawable(R.drawable.webcam));
			stream.setVisibility(ImageView.VISIBLE);
		}
		else
			stream.setVisibility(ImageView.INVISIBLE);
	}

	public void setRaiseHandStatus(Contact changedStatus)
	{
		ImageView raiseHand = (ImageView) view.findViewById(R.id.raise_hand);
		if(changedStatus.isRaiseHand())
		{
			raiseHand.setImageDrawable(this.context.getResources().getDrawable(R.drawable.raisehand));
			raiseHand.setVisibility(ImageView.VISIBLE);
		}
		else
			raiseHand.setVisibility(ImageView.INVISIBLE);

	}

	public void resetAllChatStatus()
	{
		for (IParticipant contact : listContact)
		{
			Contact entry = new Contact(contact);
			entry.setChatStatus(Contact.CONTACT_NORMAL);
			
		}
	}


	public int getCount() {
		return listContact.size();
	}

	public Object getItem(int position) {
		return listContact.get(position);
	}

	public long getItemId(int position) {
		return listContact.get(position).getUserId();
	}

	public Contact getUserById(int id) {
		for (IParticipant contact : listContact) {
			if (contact.getUserId() == id)
				return (Contact) contact;
		}
		return null;
	}

	public View getView(int position, View convertView, ViewGroup viewGroup) {
		Contact entry = (Contact) listContact.get(position);

		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.contact, null);
		}
		view = convertView;


		String name = entry.getName();
		TextView contactName = (TextView) convertView.findViewById(R.id.contact_name);
		//indicates who you are on the list
		if (entry.getUserId() == ((BigBlueButton) context.getApplicationContext()).getHandler().getMyUserId()) {
			name += " (" + context.getResources().getString(R.string.you) + ")";
			contactName.setTextAppearance(context, R.style.MyNameStyle);
		} else
			contactName.setTextAppearance(context, R.style.ParticipantNameStyle);
		contactName.setText(name);
		contactName.setTag(name);

		//puts the correct images of moderator, presenter, etc
		ImageView moderator = (ImageView) convertView.findViewById(R.id.moderator);
		if(entry.isModerator()) {
			moderator.setImageDrawable(this.context.getResources().getDrawable(R.drawable.administrator));
			moderator.setVisibility(ImageView.VISIBLE);
		}
		else
			moderator.setVisibility(ImageView.INVISIBLE);

		setPresenterStatus(entry);
		setStreamStatus(entry);
		setRaiseHandStatus(entry);
		setPrivateChat(entry);

//		int color;
//		//change the background of people on the private chat
//		switch (entry.getChatStatus()) {
//		case Contact.CONTACT_ON_PRIVATE_MESSAGE:
//			color = R.color.title_background;
//			break;
//		default:
//			color = R.color.background;
//		}
//
//		convertView.setBackgroundResource(color);
		
		//selector doesn't work if seResourceBackground is called
		//need to find another way to show yhat a user is on private chat
	


		return convertView;

	}

	public void setChatStatus(int userId, int chatStatus) {
		if(getUserById(userId)!=null)
			getUserById(userId).setChatStatus(chatStatus);
	}

	public int getChatStatus(int position)
	{
		int userId = listContact.get(position).getUserId();
		return getUserById(userId).getChatStatus();
	}

	public int getUserId (int position)
	{
		return listContact.get(position).getUserId();
	}

	public void sort() {
		Collections.sort(listContact, new Comparator<IParticipant>() {

			@Override
			public int compare(IParticipant object1, IParticipant object2) {
				Contact p1 = (Contact) object1;
				Contact p2 = (Contact) object2;

				if (p1.getChatStatus() != Contact.CONTACT_NORMAL && p2.getChatStatus() == Contact.CONTACT_NORMAL)
					return -1;
				if (p2.getChatStatus() != Contact.CONTACT_NORMAL && p1.getChatStatus() == Contact.CONTACT_NORMAL)
					return 1;				

				if (p1.isModerator() && !p2.isModerator())
					return -1;
				if (p2.isModerator() && !p1.isModerator())
					return 1;

				if (p1.isPresenter() && !p2.isPresenter())
					return -1;
				if (p2.isPresenter() && !p1.isPresenter())
					return 1;

				return p1.getName().compareToIgnoreCase(p2.getName());
			}
		}); 

	}

}
