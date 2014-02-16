package edu.mit.media.inm.types;

import android.os.Parcel;
import android.os.Parcelable;
import edu.mit.media.inm.R;

/**
 * Represents a Plant in the Feed, or composed by Tell.
 * 
 * @author joyc4luck
 * 
 */
public class Plant implements Parcelable, Comparable<Plant> {	
	public static final String NEW_STORY = "new_plant";
	public static final String OPEN_PLANT = "open_plant";
	public static final String PLANT = "plant";
	public static final String BIRD = "bird";
	public static final String HAMSTER = "ham";

	public static final String[] TYPES = {PLANT, BIRD, HAMSTER};
	
	public long id;
	public String author;
	public boolean archived;
	public long date;
	public String passphrase;
	public int pot;
	public String salt;
	public String server_id;
	public String shared_with;
	public int status;
	public boolean shiny;
	public String title;
	public String type;
	

	public static int[] pots = {
			R.drawable.pot_1,
			R.drawable.pot_2,
			R.drawable.pot_3,
			R.drawable.pot_4,
			R.drawable.pot_5,
			R.drawable.pot_6,
			R.drawable.pot_7,
			R.drawable.pot_8,
			R.drawable.pot_8,
			R.drawable.pot_8,
			R.drawable.pot_8,
			R.drawable.pot_8,
			R.drawable.pot_8,
	};
	
	public static int[] b_pots = {
		R.drawable.b_pot_1,
		R.drawable.b_pot_2,
		R.drawable.b_pot_3,
		R.drawable.b_pot_4,
		R.drawable.b_pot_5,
		R.drawable.b_pot_6,
		R.drawable.b_pot_7,
		R.drawable.b_pot_8,
		R.drawable.b_pot_8,
		R.drawable.b_pot_8,
		R.drawable.b_pot_8,
		R.drawable.b_pot_8,
		R.drawable.b_pot_8,
	};
	
	public static int[] growth = {
		R.drawable.plant_0,
		R.drawable.plant_1,
		R.drawable.plant_2,
		R.drawable.plant_3,
		R.drawable.plant_4,
		R.drawable.plant_5,
		R.drawable.plant_6,
		R.drawable.plant_7,
		R.drawable.plant_8,
	};
	

	public static int[] birds = {
		R.drawable.bird_0,
		R.drawable.bird_1,
		R.drawable.bird_2,
		R.drawable.bird_3,
		R.drawable.bird_4,
	};
	
	public static int[] water = {
		R.drawable.water_1,
		R.drawable.water_2,
		R.drawable.water_3,
		R.drawable.water_4,
		R.drawable.water_5,
		R.drawable.water_6,
	};

	public static int[] b_water = {
		R.drawable.b_water_1,
		R.drawable.b_water_2,
		R.drawable.b_water_3,
		R.drawable.b_water_4,
		R.drawable.b_water_5,
		R.drawable.b_water_6,
	};


	public static int[] ham = {
		R.drawable.ham_0,
		R.drawable.ham_1,
		R.drawable.ham_2,
		R.drawable.ham_3,
		R.drawable.ham_4,
		R.drawable.ham_5,
		R.drawable.ham_6,
	};
	
	public static int[] wheel = {
		R.drawable.wheel_1,
		R.drawable.wheel_2,
		R.drawable.wheel_3,
		R.drawable.wheel_4,
		R.drawable.wheel_5,
		R.drawable.wheel_6,
		R.drawable.wheel_7,
		R.drawable.wheel_8,
	};

	public static int[] b_ham = {
		R.drawable.b_ham_1,
		R.drawable.b_ham_2,
		R.drawable.b_ham_3,
		R.drawable.b_ham_4,
		R.drawable.b_ham_5,
		R.drawable.b_ham_6,
		R.drawable.b_ham_7,
		R.drawable.b_ham_8,
	};

	
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
		out.writeInt(archived? 1:0);
		out.writeLong(date);
		out.writeString(passphrase);
		out.writeInt(pot);
		out.writeString(salt);
		out.writeString(server_id);
		out.writeString(shared_with);
		out.writeInt(status);
		out.writeString(title);
		out.writeString(type);
		out.writeInt(shiny? 1:0);
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
        this.archived = in.readInt() == 1;
        this.date = in.readLong();
        this.passphrase = in.readString();
        this.pot = in.readInt();
        this.salt = in.readString();
        this.server_id = in.readString();
        this.shared_with = in.readString();
        this.status = in.readInt();
        this.title = in.readString();
        this.type = in.readString();
        this.shiny = in.readInt() == 1;
    }

	@Override
	public int compareTo(Plant other) {
		return (int) (other.date - this.date);
	}
}
