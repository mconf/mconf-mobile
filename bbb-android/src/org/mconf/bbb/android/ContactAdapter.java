package org.mconf.bbb.android;

import java.util.List;

import org.mconf.bbb.users.IParticipant;

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
	ImageView presenter;
	View view;

    private List<IParticipant> listContact;

    public ContactAdapter(Context context, List<IParticipant> listContact) {
        this.context = context;
        this.listContact = listContact;
    }
    
    public void addSection(IParticipant newParticipant) {
    	Contact contact = new Contact (newParticipant);
       listContact.add(contact);
    	
         }
    
    public void removeSection(String name){
    	Contact contact;
    	int location=0;
    	while(listContact.get(location).getName().equals(name)==false)
    		location++;
    	
    	contact=(Contact) listContact.get(location);
    	listContact.remove(contact);
    }
    
    public void setPresenterStatus(Contact changedStatus)
    {
    	 ImageView presenter = (ImageView) view.findViewById(R.id.presenter);
        if(changedStatus.isPresenter())
        	presenter.setImageDrawable(this.context.getResources().getDrawable(R.drawable.presenter));
        else
        	presenter.setImageDrawable(this.context.getResources().getDrawable(R.drawable.empty));
    }

    public void setStreamStatus( Contact changedStatus)
    {
    	ImageView stream = (ImageView) view.findViewById(R.id.stream);
        if(changedStatus.hasStream())
        	stream.setImageDrawable(this.context.getResources().getDrawable(R.drawable.stream));
        else
        	stream.setImageDrawable(this.context.getResources().getDrawable(R.drawable.empty));
    }
    //continuar refatorando
    
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
        if(entry.isModerator())
           	moderator.setImageDrawable(this.context.getResources().getDrawable(R.drawable.moderator));
        else
        	moderator.setImageDrawable(this.context.getResources().getDrawable(R.drawable.empty));
        
        setPresenterStatus(entry);
        setStreamStatus(entry);
        
        
        ImageView raiseHand = (ImageView) convertView.findViewById(R.id.raise_hand);
        if(entry.isRaiseHand())
        	raiseHand.setImageDrawable(this.context.getResources().getDrawable(R.drawable.raise_hand));
        else
        	raiseHand.setImageDrawable(this.context.getResources().getDrawable(R.drawable.empty));
                      

        return convertView;
    }

}
