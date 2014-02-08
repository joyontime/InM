package edu.mit.media.inm.types;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Represents a Story in the Feed, or composed by Tell.
 * 
 * @author joyc4luck
 * 
 */
public class Collection implements Parcelable, Comparable<Collection> {	
	public long id;
	public String server_id;
	public String name;
	public String plants;
	public String[] plant_list;

	@Override
	public String toString() {
		return name;
	}

	@Override
	public int describeContents() {
		return plant_list.length;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeLong(id);
		out.writeString(server_id);
		out.writeString(name);
		out.writeString(plants);
	}
	
	/**
	 * Regeneration of object.
	 */
    public static final Parcelable.Creator<Collection> CREATOR = new Parcelable.Creator<Collection>() {
        public Collection createFromParcel(Parcel in) {
            return new Collection(in);
        }

        public Collection[] newArray(int size) {
            return new Collection[size];
        }
    };
    

    public Collection(){
    }

    public Collection(String server_id, String name, String plants){
    	this.server_id = server_id;
    	this.name = name;
    	this.plants = plants;
        this.plant_list = this.plants.split(",");
    }

    private Collection(Parcel in) {
        this.id = in.readLong();
        this.server_id = in.readString();
        this.name = in.readString();
        this.plants = in.readString();
        this.plant_list = this.plants.split(",");
    }

	@Override
	public int compareTo(Collection other) {
		return other.name.compareTo(this.name);
	}
}