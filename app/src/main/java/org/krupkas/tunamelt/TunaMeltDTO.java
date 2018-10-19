package org.krupkas.tunamelt;

import java.util.ArrayList;

public class TunaMeltDTO {
    public String owner;  // TunaMelt user/owner - the person who is sending this data
    public String version; // version of TunaMelt
    public ArrayList<TMRecord> tmRecords; // The TunaMelt records, without photos (sent separately)
}
