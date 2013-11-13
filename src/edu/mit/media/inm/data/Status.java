package edu.mit.media.inm.data;

import java.util.HashMap;
import java.util.Map;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Represents a Status in the Feed, or composed by Tell.
 * 
 * @name joyc4luck
 * 
 */
public class Status implements Parcelable, Comparable<Status> {

	public static final String NEW_STATUS = "new_status";

	public long id;
	public int avail;
	public String briefing;
	public long date;
	public int mood;
	public String name;


	public static String BAD = "Bad";
	public static String SOSO = "So-so";
	public static String CHEERFUL = "Cheerful";
	
	public static final Map<Integer, String> moodStringMap;
	static {
		moodStringMap = new HashMap<Integer, String>();
		moodStringMap.put(0, BAD);
		moodStringMap.put(1, SOSO);
		moodStringMap.put(2, CHEERFUL);
	}

	public static String NO = "Please do not disturb.";
	public static String MAYBE = "I'm flexible today.";
	public static String YES = "Come on in!";

	public static final Map<Integer, String> availStringMap;
	static {
		availStringMap = new HashMap<Integer, String>();
		availStringMap.put(0, NO);
		availStringMap.put(1, MAYBE);
		availStringMap.put(2, YES);
	}

	@Override
	public String toString() {
		return this.name + " is feeling " + moodStringMap.get(mood) + ". "
				+ availStringMap.get(avail);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeLong(id);
		out.writeInt(avail);
		out.writeString(briefing);
		out.writeLong(date);
		out.writeInt(mood);
		out.writeString(name);
	}

	/**
	 * Regeneration of object.
	 */
	public static final Parcelable.Creator<Status> CREATOR = new Parcelable.Creator<Status>() {
		public Status createFromParcel(Parcel in) {
			return new Status(in);
		}

		public Status[] newArray(int size) {
			return new Status[size];
		}
	};

	public Status() {

	}

	private Status(Parcel in) {
		this.id = in.readLong();
		this.avail = in.readInt();
		this.briefing = in.readString();
		this.date = in.readLong();
		this.mood = in.readInt();
		this.name = in.readString();
	}

	@Override
	public int compareTo(Status other) {
		return (int) (other.date - this.date);
	}
}