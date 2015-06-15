package troop.com.themesample.subfragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import troop.com.themesample.R;
import troop.com.themesample.views.menu.MenuItem;

/**
 * Created by troop on 15.06.2015.
 */
public class ValuesMenuFragment extends Fragment implements ListView.OnItemClickListener
{
    String[] item;
    ListView listView;
    View view;
    Interfaces.I_CloseNotice i_closeNotice;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.valuesmenufragment, container, false);
        listView = (ListView)view.findViewById(R.id.values_fragment_listview);


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity().getApplicationContext(),
                R.layout.listviewlayout, R.id.listviewlayout_textview, item);
        //attach adapter to the listview and fill
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        return view;
    }

    public void SetMenuItem(String[] item, Interfaces.I_CloseNotice i_closeNotice)
    {
        this.item = item;
        this.i_closeNotice = i_closeNotice;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String value = (String) listView.getItemAtPosition(position);
        i_closeNotice.onClose(value);
    }
}
