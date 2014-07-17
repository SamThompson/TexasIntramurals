package com.xenithturtle.texasim.asynctasks;

/**
 * Created by sam on 6/5/14.
 */
public class AsyncTaskConstants {

    public static final String SERVER_BASE = "http://www.cs.utexas.edu/~st028/cgi-bin/";

    public static final String SERVER_STATUS = SERVER_BASE + "server_status.scgi";

    public static final String IM_REQ_BASE =
            SERVER_BASE + "im_data_request.scgi?";

    public static final String LEAGUES_REQ_BASE =
            SERVER_BASE + "league_data_request.scgi?event=";

    public static final String LEAGUES_REQ_MID = "&request=";


    //JSON constants
    public static final String EID = "event_id";
    public static final String ENAME = "event_name";
    public static final String DID = "division_id";
    public static final String DNAME = "division_name";
    public static final String LID = "league_id";
    public static final String LNAME = "league_name";
    public static final String LINFO = "league_info";
    public static final String LEAGUES = "leagues";
    public static final String STATUS = "status_ok";
    public static final String MESSAGE = "message";

    //Deals with league requests
    public static final String NAME = "name";
    public static final String DIVISION = "division";
    public static final String TIME = "play_time";
    public static final String UPDATE = "update_time";
    public static final String SPORT = "sport";

}
