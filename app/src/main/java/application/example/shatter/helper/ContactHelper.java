package application.example.shatter.helper;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.List;

import application.example.shatter.model.ContactDetails;

public class ContactHelper {

    Context context;
    Uri uri;
    ContentResolver contentResolver;

    public ContactHelper(Context context) {
        this.context = context;
        this.uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        ;
        contentResolver = context.getContentResolver();
    }

    public List<ContactDetails> getAllContacts() {

        List<ContactDetails> list = new ArrayList<>();

        String[] projection = {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER};
        Cursor cursor = contentResolver.query(uri, projection, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(0);
                String number = cursor.getString(1);
                String no = new StringBuilder(number)
                        .reverse()
                        .toString()
                        .replace("-", "")
                        .replace("(", "")
                        .replace(")", "")
                        .replace(" ", "");

                if (no.length() > 10) {
                    number = new StringBuilder(no.substring(0, 10)).reverse().toString();
                } else {
                    number = new StringBuilder(no).reverse().toString();
                }

                list.add(new ContactDetails(name, number));
            }
        }
        assert cursor != null;
        cursor.close();

        return list;
    }

    public String getNameByPhone(String phoneNo) {

        List<ContactDetails> list = getAllContacts();

        for(ContactDetails cd : list) {
            if (cd.getContactNumber().equals(phoneNo)) {
                return cd.getContactName();
            }
        }
        return null;
    }

}
