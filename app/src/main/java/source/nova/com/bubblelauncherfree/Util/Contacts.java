package source.nova.com.bubblelauncherfree.Util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class Contacts {

    public static class ContactInfo{
        private Integer ID;
        private String name;
        private String photoUri;
        private String number;

        public ContactInfo(){}

        public ContactInfo(Integer ID, String name, String photoUri, String number) {
            this.ID = ID;
            this.name = name;
            this.photoUri = photoUri;
            this.number = number;
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public Integer getID() {
            return ID;
        }

        public void setID(Integer ID) {
            this.ID = ID;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPhotoUri() {
            return photoUri;
        }

        public void setPhotoUri(String photoUri) {
            this.photoUri = photoUri;
        }
    }

    private static final String CONTACT_ID = ContactsContract.Contacts._ID;
    private static final String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;

    private static final String PHONE_NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;
    private static final String PHONE_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
    private static final String CONTACT_NAME = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME;
    private static final String CONTACT_PHOTO = ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI;

    public static ArrayList<ContactInfo> getAll(Context context) {
        ContentResolver cr = context.getContentResolver();

        String[] mProjection = new String[]
                {
                        PHONE_CONTACT_ID,
                        PHONE_NUMBER,
                        CONTACT_NAME,
                        CONTACT_PHOTO
                };

        Cursor pCur = cr.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                mProjection,
                null,
                null,
                null
        );
        if(pCur != null){
            if(pCur.getCount() > 0) {
                HashMap<Integer, ArrayList<String>> phones = new HashMap<>();
                while (pCur.moveToNext()) {
                    Integer contactId = pCur.getInt(pCur.getColumnIndex(PHONE_CONTACT_ID));
                    ArrayList<String> curPhones = new ArrayList<>();
                    if (phones.containsKey(contactId)) {
                        curPhones = phones.get(contactId);
                    }
                    curPhones.add(contactId.toString());
                    curPhones.add(pCur.getString(pCur.getColumnIndex(PHONE_NUMBER)));
                    curPhones.add(pCur.getString(pCur.getColumnIndex(CONTACT_NAME)));
                    curPhones.add(pCur.getString(pCur.getColumnIndex(CONTACT_PHOTO)));
                    phones.put(contactId, curPhones);
                }
                Cursor cur = cr.query(
                        ContactsContract.Contacts.CONTENT_URI,
                        new String[]{CONTACT_ID, HAS_PHONE_NUMBER},
                        HAS_PHONE_NUMBER + " > 0",
                        null,null);
                if (cur != null) {
                    if (cur.getCount() > 0) {
                        ArrayList<ContactInfo> contacts = new ArrayList<>();
                        while (cur.moveToNext()) {
                            int id = cur.getInt(cur.getColumnIndex(CONTACT_ID));
                            if(phones.containsKey(id)) {
                                ArrayList<String> s = phones.get(id);
                                ContactInfo contactInfo = new ContactInfo();
                                contactInfo.setID(Integer.valueOf(s.get(0)));
                                Log.i("compare ids","contact_id: "+id+" phone_contact_id: "+s.get(0));
                                contactInfo.setName(s.get(2));
                                contacts.add(contactInfo);
                            }
                        }
                        return contacts;
                    }
                    cur.close();
                }
            }
            pCur.close();
        }
        return null;
    }
}
