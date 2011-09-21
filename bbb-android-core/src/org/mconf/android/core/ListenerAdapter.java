package org.mconf.android.core;

import java.util.ArrayList;
import java.util.List;

import org.mconf.bbb.listeners.IListener;
import org.mconf.bbb.listeners.Listener;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListenerAdapter extends BaseAdapter {
	//list of the listeners on the meeting
	private List<IListener> listListener = new ArrayList<IListener>();
	
	public ListenerAdapter() {
    }
    
    public void addSection(IListener listener) {
    	ListenerContact contact = new ListenerContact(listener);
    	listListener.add(contact);
    }
    
    public void removeSection(IListener listener){
    	ListenerContact contact = getUserById(listener.getUserId());
    	if (contact != null)
    		listListener.remove(contact);
    }
    
    //set the correct images on each listener
    public void setMutedStatus(Listener changedStatus)
	{
	}

	public void setTalkingStatus(Listener changedStatus)
	{
	}
    
    public int getCount() { 
        return listListener.size();
    } 

    public Object getItem(int position) {
        return listListener.get(position);
    }

    public long getItemId(int position) {
        return listListener.get(position).getUserId();
    }
     
    public ListenerContact getListener ( int position)
    {
    	return new ListenerContact (listListener.get(position));
    }
    
    public ListenerContact getUserById(int id) { 
    	for (IListener listener : listListener) { 
    		if (listener.getUserId() == id)
    			return (ListenerContact) listener;
    	}
    	return null;  
    }
    
    public View getView(int position, View convertView, ViewGroup viewGroup) {
    	ListenerContact entry = (ListenerContact) listListener.get(position);
        
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) viewGroup.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listener, null);
        }
        
        String name = entry.getCidName();
        final TextView contactName = (TextView) convertView.findViewById(R.id.listener_name);
        contactName.setTextAppearance(viewGroup.getContext(), R.style.ParticipantNameStyle);
        contactName.setText(name);
        contactName.setTag(name);

        
		final ImageView muted = (ImageView) convertView.findViewById(R.id.muted);
		if(entry.isMuted()) {
			muted.setImageDrawable(viewGroup.getContext().getResources().getDrawable(R.drawable.sound_mute));
			muted.setVisibility(ImageView.VISIBLE);
		} else {
			muted.setImageDrawable(viewGroup.getContext().getResources().getDrawable(R.drawable.sound_none));
			muted.setVisibility(ImageView.VISIBLE);
		}

		final ImageView talking = (ImageView) convertView.findViewById(R.id.talking);
		if(entry.isTalking()) {
			talking.setImageDrawable(viewGroup.getContext().getResources().getDrawable(R.drawable.sound));
			talking.setVisibility(ImageView.VISIBLE);
		} else
			talking.setVisibility(ImageView.INVISIBLE);
		
        return convertView;
    }

	public void clearList() {
		listListener.clear();
	}

}
