package edu.mit.media.inm.types;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Represents a Story in the Feed, or composed by Tell.
 * 
 * @author joyc4luck
 * 
 */
public class Note implements Parcelable, Comparable<Note> {

	public static final String EVERYONE = "everyone";
	public static final String INNER = "inner";
	
	public static final String NEW_STORY = "new_story";
	public static final String OPEN_STORY = "open_story";
	
	public long id;
	public String author;
	public long date;
	public String text;
	public String plant;
	public String server_id;

	@Override
	public String toString() {
		return text;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeLong(id);
		out.writeString(author);
		out.writeLong(date);
		out.writeString(text);
		out.writeString(plant);
		out.writeString(server_id);
	}
	
	/**
	 * Regeneration of object.
	 */
    public static final Parcelable.Creator<Note> CREATOR = new Parcelable.Creator<Note>() {
        public Note createFromParcel(Parcel in) {
            return new Note(in);
        }

        public Note[] newArray(int size) {
            return new Note[size];
        }
    };

    public Note(){
    	
    }

    private Note(Parcel in) {
        this.id = in.readLong();
        this.author = in.readString();
        this.date = in.readLong();
        this.text = in.readString();
        this.plant = in.readString();
        this.server_id = in.readString();
    }

	@Override
	public int compareTo(Note other) {
		return (int) (other.date - this.date);
	}
}