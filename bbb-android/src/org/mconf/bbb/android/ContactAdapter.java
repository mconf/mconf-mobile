package org.mconf.bbb.android;

import java.util.List;

import org.mconf.bbb.users.Participant;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class ContactAdapter extends BaseAdapter {
	private Context context;

    private List<Contact> listContact;

    public ContactAdapter(Context context, List<Contact> listContact) {
        this.context = context;
        this.listContact = listContact;
    }
    
    public void addSection(Participant adapter) {
    	Contact contact = (Contact)adapter;
        listContact.add(contact);
    }

    
    public int getCount() {
        return listContact.size();
    }

    public Object getItem(int position) {
        return listContact.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup viewGroup) {
        Contact entry = listContact.get(position);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.contact, null);
        }
        
        
        TextView contactName = (TextView) convertView.findViewById(R.id.contact_name);
        contactName.setText(entry.getContactName());
        

       
        ImageView moderator = (ImageView) convertView.findViewById(R.id.moderator);
        if(entry.isModerator())
           	moderator.setImageDrawable(this.context.getResources().getDrawable(R.drawable.moderator));
        else
        	moderator.setImageDrawable(this.context.getResources().getDrawable(R.drawable.empty));
        
        
        ImageView presenter = (ImageView) convertView.findViewById(R.id.presenter);
        if(entry.isPresenter())
        	presenter.setImageDrawable(this.context.getResources().getDrawable(R.drawable.presenter));
        else
        	presenter.setImageDrawable(this.context.getResources().getDrawable(R.drawable.empty));
        
        ImageView stream = (ImageView) convertView.findViewById(R.id.stream);
        if(entry.hasStream())
        	stream.setImageDrawable(this.context.getResources().getDrawable(R.drawable.stream));
        else
        	stream.setImageDrawable(this.context.getResources().getDrawable(R.drawable.empty));
        
        ImageView raiseHand = (ImageView) convertView.findViewById(R.id.raise_hand);
        if(entry.isRaiseHand())
        	raiseHand.setImageDrawable(this.context.getResources().getDrawable(R.drawable.raise_hand));
        else
        	raiseHand.setImageDrawable(this.context.getResources().getDrawable(R.drawable.empty));
                      

        return convertView;
    }

}
