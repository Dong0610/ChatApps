package com.g51.demo.myapp.utility;

import java.util.HashMap;

public class Contanst {
    public static final String KEY_COLLECTON_USER = "user";
    public static final String KEY_NAME = "name";
    public static final String KEY_USER_STATUS = "statusuer";
    public static final String KEY_EMAIL ="email";
    public static final String KEY_PASSWORD="password";
    public static final String KEY_PREFERENCE_NAME ="ChatApppreference";
    public static final String KEY_IS_SIGN_IN ="isSignedIn";
    public static final String KEY_USER_ID ="userId";
    public static final String KEY_IMAGE ="image";
    public static final String KEY_FCM_TOKEN= "fcmToken";
    public static final String KEY_USE="users";
    public static final String KEY_USER_URI="imageUri";
    public static final String KEY_COLECION_CHAT="chat";
    public static final String KEY_SENDER_ID="senderId";
    public static final String KEY_RECEIVE_ID="receiveId";
    public static final String KEY_MESSAGE="message";
    public static final String KEY_TIMETAM ="time";
    public static final String KEY_COLECTION_CONVERSION="conversion";
    public static final String KEY_SENDER_NAME= "sendername";
    public static final String KEY_RECEIVE_NAME="revceivename";
    public static final String KEY_SENDER_IMAGE="senderIMg";
    public static final String KEY_RECEIVE_IMAGE="receiveImg";
    public static final String KEY_LAST_MESSAGE="lastMess";
    public static final String KEY_AVAIBILITY="availability";
    public static final String KEY_MESS_IMG = "messuri";
    public static final String REMOTE_MSG_AUTHORATION="Authorization";
    public static final String REMOTE_MSG_CONTEN_TYPE="Content-Type";
    public static final String REMOTE_MSG_DATA= "data";
    public static final String REMOTE_MSG_IDS ="registration_ids";

    //
    public static final String KEY_POST_UPLOAD="uploadpost";
    public static final String KEY_POST_USER="imageuser";
    public static final String KEY_POST_NAME="nameuser";
    public static final String KEY_POST_STATUS="status";
    public static final String KEY_POST_IMAGE="imageUri";
    public static final String KEY_POST_TIME="time";


    //
    public static final String KEY_COMMENT="comment";
    public static final String KEY_COMMENT_POSTID="postid";
    public static final String KEY_COMMENT_IMG="cmuserimg";
    public static final String KEY_COMMENT_NAME="cmusername";
    public static final String KEY_COMMENT_MES="cmmessage";
    public static final String KEY_COMMENT_TIME="cmtime";

    public static HashMap<String,String> remoteMSGHeader= null;
    public static HashMap<String, String > getRemoteMSGHeader(){
        if(remoteMSGHeader==null){
            remoteMSGHeader= new HashMap<>();
            remoteMSGHeader.put(
                    REMOTE_MSG_AUTHORATION,
                    "key=AAAAvnyhBdc:APA91bGPRSOuAcmDD8Ek76O1JhZy-IW2Z1n5z2TeJDBvllF1FvrmawS0GuwzT5NkgmGbCwN9q6X0DC5XG76zWeXy8ZzKPrs7is4ZsmIOj5m7O_lLdiAsPuTvxEQSaM2EAvuSbvoXtLg5"
            );
            remoteMSGHeader.put(REMOTE_MSG_CONTEN_TYPE,
                    "application/json");

        }
        return remoteMSGHeader;
    }

}


















