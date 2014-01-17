package edu.mit.media.inm.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Represents a Plant in the Feed, or composed by Tell.
 * 
 * @author joyc4luck
 * 
 */
public class Plant implements Parcelable, Comparable<Plant> {
	public static final String EVERYONE = "everyone";
	public static final String INNER = "inner";
	
	public static final String NEW_STORY = "new_plant";
	public static final String OPEN_STORY = "open_plant";
	
	public long id;
	public String author;
	public long date;
	//public String image;
	public String passphrase;
	public String salt;
	public String server_id;
	public String shared_with;
	public int status;
	//public String plant;
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
		//out.writeString(image);
		out.writeString(passphrase);
		out.writeString(salt);
		out.writeString(server_id);
		out.writeString(shared_with);
		out.writeInt(status);
		//out.writeString(plant);
		out.writeString(title);
	}
	
	/**
	 * Regeneration of object.
	 */
    public static final Parcelable.Creator<Plant> CREATOR = new Parcelable.Creator<Plant>() {
        public Plant createFromParcel(Parcel in) {
            return new Plant(in);
        }

        public Plant[] newArray(int size) {
            return new Plant[size];
        }
    };

    public Plant(){
    	
    }

    private Plant(Parcel in) {
        this.id = in.readLong();
        this.author = in.readString();
        this.date = in.readLong();
        //this.image = in.readString();
        this.passphrase = in.readString();
        this.salt = in.readString();
        this.server_id = in.readString();
        this.shared_with = in.readString();
        this.status = in.readInt();
        //this.plant = in.readString();
        this.title = in.readString();
    }

	@Override
	public int compareTo(Plant other) {
		return (int) (other.date - this.date);
	}
}
