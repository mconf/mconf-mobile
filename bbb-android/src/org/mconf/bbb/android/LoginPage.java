package org.mconf.bbb.android;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginPage extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        
     
        Button launch = (Button)findViewById(R.id.button);
        
        launch.setOnClickListener( new OnClickListener()
        {
               @Override
                public void onClick(View viewParam)
                {
                	EditText usernameEditText = (EditText) findViewById(R.id.txt_username);
                	String sUserName = usernameEditText.getText().toString();
                	                	 
                	if(sUserName.length()<1){                		
                		Toast empty = Toast.makeText(getApplicationContext(),"Username vazio", Toast.LENGTH_LONG);  
                        empty.show();
                      
                    }
                	else{
                	  
                    Toast login = Toast.makeText(getApplicationContext(),"Loggin in \nUsername: " + sUserName, Toast.LENGTH_SHORT);  
                    login.show();  
                    
                     //logar no BBB                           
                    //chamar a outra view
                    Intent myIntent = new Intent(getApplicationContext(), BBBandroid.class);
                    myIntent.putExtra("Username", sUserName);
                    startActivityForResult(myIntent, 0);
                    

                    finish();
                	}
                }
        	}
        );
    }
}
