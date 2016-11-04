package com.google.android.gms.samples.vision.inner.bink.ui.card_detail;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.loyaltyangels.bink.R;
import com.loyaltyangels.bink.model.scheme.Transaction;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by hansonaboagye on 17/08/16.
 */
public class TransactionsAdapter extends BaseAdapter {


    private List<Transaction> items;


    String curFilter;
    LayoutInflater li;
    TextView description;
    TextView date;
    TextView points;
    Context context;

    private SimpleDateFormat transactionDateFormat;
    private SimpleDateFormat transactionDateDisplayFormat;

    public TransactionsAdapter(Context context, List<Transaction> values) {
        super();
        this.items = values;
        this.context = context;
        this.curFilter = null;

        this.li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        transactionDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        transactionDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        transactionDateDisplayFormat = new SimpleDateFormat("dd MMM yyyy");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = li.inflate(R.layout.transaction_view_item, null);


        description = (TextView) v.findViewById(R.id.description);
        date = (TextView) v.findViewById(R.id.date);
        points = (TextView) v.findViewById(R.id.points);

        Transaction transaction = items.get(position);


        if (!TextUtils.isEmpty(transaction.getDate())) {
            try {
                if (position > 0) {
                    Date transactionDate = transactionDateFormat.parse(transaction.getDate());
                    date.setText(transactionDateDisplayFormat.format(transactionDate));
                } else {
                    date.setText(transaction.getDate());
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        description.setText(transaction.getDescription());

        if (position == 0) {
            //set the heading titles in a different color
            description.setTextColor(context.getResources().getColor(R.color.pink));
            date.setTextColor(context.getResources().getColor(R.color.pink));
            points.setTextColor(context.getResources().getColor(R.color.pink));

            points.setText(transaction.getPoints());
            date.setText(transaction.getDate());
        } else {
            Double pointsDisplay = Double.parseDouble(transaction.getPoints());
            points.setText(String.valueOf(pointsDisplay.intValue()));
        }

        return v;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


}
