package edu.mit.media.inm.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Represents a Story in the Feed, or composed by Tell.
 * 
 * @author joyc4luck
 * 
 */
public class Story implements Parcelable, Comparable<Story> {

	public static final String EVERYONE = "everyone";
	public static final String INNER = "inner";
	
	public static final String NEW_STORY = "new_story";
	
	public long id;
	public String author;
	public long date;
	public String image;
	public String share;
	public String story;
	public String title;

	@Override
	public String toString() {
		return title;
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
		out.writeString(image);
		out.writeString(share);
		out.writeString(story);
		out.writeString(title);
	}
	
	/**
	 * Regeneration of object.
	 */
    public static final Parcelable.Creator<Story> CREATOR = new Parcelable.Creator<Story>() {
        public Story createFromParcel(Parcel in) {
            return new Story(in);
        }

        public Story[] newArray(int size) {
            return new Story[size];
        }
    };

    public Story(){
    	
    }

    private Story(Parcel in) {
        this.id = in.readLong();
        this.author = in.readString();
        this.date = in.readLong();
        this.image = in.readString();
        this.share = in.readString();
        this.story = in.readString();
        this.title = in.readString();
    }

	@Override
	public int compareTo(Story other) {
		return (int) (other.date - this.date);
	}
}