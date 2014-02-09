package edu.mit.media.inm.handlers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import edu.mit.media.inm.R;
import edu.mit.media.inm.types.Note;
import edu.mit.media.inm.types.Plant;
import edu.mit.media.inm.util.AesUtil;

public class NoteAdapter extends ArrayAdapter<Note> {
	private Context context;
	private int layoutResourceId;
	private List<Note> data;
	private Plant plant;

	public NoteAdapter(Context context, List<Note> data, Plant plant) {
		super(context, R.layout.note_list_item, data);
		this.layoutResourceId = R.layout.note_list_item;
		this.context = context;
		this.data = data;
		this.plant = plant;
		Collections.sort(this.data);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		NoteHolder holder = null;

		if (row == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);

			holder = new NoteHolder();
			holder.author = (TextView) row.findViewById(R.id.note_author);
			holder.date = (TextView) row.findViewById(R.id.note_date);
			holder.excerpt = (TextView) row.findViewById(R.id.note_text);

			row.setTag(holder);
		} else {
			holder = (NoteHolder) row.getTag();
		}

		Note note = data.get(position);
		holder.author.setText(note.author);

		DateFormat df = new SimpleDateFormat("HH:mm\n MM/dd");
		holder.date.setText(df.format(new Date(note.date)));

		holder.excerpt.setText(decryptText(note.text));
		holder.id = note.id;
		
		return row;
	}
	
	private String decryptText(String note_text){
		String IV = new PreferenceHandler(context).IV();
		String pass = plant.passphrase;
		String salt = plant.salt;

		AesUtil util = new AesUtil();

        String decrypt = util.decrypt(salt, IV, pass, note_text);
        return decrypt;
	}

	public static class NoteHolder {
		TextView author;
		TextView date;
		TextView excerpt;
		long id;

		public long getId() {
			return id;
		}
	}
}