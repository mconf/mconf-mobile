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
	private static final Logger log = LoggerFactory.getLogger(ContactAdapter.class);
	private Context context;
	View view;

    private List<IParticipant> listContact = new ArrayList<IParticipant>();

    public ContactAdapter(Context context) {
        this.context = context;
    }
    
    public void addSection(IParticipant participant) {
    	Contact contact = new Contact(participant);
    	listContact.add(contact);
    }
    
    public void removeSection(IParticipant participant){
    	Contact contact = getUserById(participant.getUserId());
    	if (contact != null)
    		listContact.remove(contact);
    }
    
    
    public void setPresenterStatus(Contact changedStatus)
    {
    	ImageView presenter = (ImageView) view.findViewById(R.id.presenter);
    	if(changedStatus.isPresenter())
    	{
    		presenter.setImageDrawable(this.context.getResources().getDrawable(R.drawable.presenter_big));
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
        	stream.setImageDrawable(this.context.getResources().getDrawable(R.drawable.webcam_big));
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
    		raiseHand.setImageDrawable(this.context.getResources().getDrawable(R.drawable.raisehand_big));
    		raiseHand.setVisibility(ImageView.VISIBLE);
    	}
    	else
    		raiseHand.setVisibility(ImageView.INVISIBLE);

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
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.contact, null);
        }
        view = convertView;
        
        TextView contactName = (TextView) convertView.findViewById(R.id.contact_name);
        contactName.setText(entry.getName());
        contactName.setTag(entry.getName());

       
        ImageView moderator = (ImageView) convertView.findViewById(R.id.moderator);
        if(entry.isModerator()) {
           	moderator.setImageDrawable(this.context.getResources().getDrawable(R.drawable.administrator_big));
           	moderator.setVisibility(ImageView.VISIBLE);
        }
        else
        	moderator.setVisibility(ImageView.INVISIBLE);
        
        setPresenterStatus(entry);
        setStreamStatus(entry);
        setRaiseHandStatus(entry);
        
        int color;
        System.out.println(entry.getName()+entry.getChatStatus());
        switch (entry.getChatStatus()) {
        	case Contact.CONTACT_ON_PRIVATE_MESSAGE:
        		color = R.color.title_background;
        		break;
        	case Contact.CONTACT_ON_PUBLIC_MESSAGE:
        		color = android.R.color.white;
        		break;
    		default:
    				color = R.color.background;
        }
        
        convertView.setBackgroundResource(color);

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
