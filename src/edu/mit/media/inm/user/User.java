package edu.mit.media.inm.user;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Represents a Story in the Feed, or composed by Tell.
 * 
 * @author joyc4luck
 * 
 */
public class User implements Parcelable, Comparable<User> {	
	public long id;
	public String server_id;
	public String alias;
	public long date_joined;

	@Override
	public String toString() {
		return alias;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeLong(id);
		out.writeString(server_id);
		out.writeString(alias);
		out.writeLong(date_joined);
	}
	
	/**
	 * Regeneration of object.
	 */
    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public User(){
    	
    }

    private User(Parcel in) {
        this.id = in.readLong();
        this.server_id = in.readString();
        this.alias = in.readString();
        this.date_joined = in.readLong();
    }

	@Override
	public int compareTo(User other) {
		return (int) (other.date_joined - this.date_joined);
	}
}