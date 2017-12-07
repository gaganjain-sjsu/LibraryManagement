package com.example.shauryamittal.librarymanagement;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shauryamittal.librarymanagement.model.CurrentUser;

import java.util.List;

/**
 * Created by shauryamittal on 12/6/17.
 */

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private List<CartItem> cartItems;
    private Context context;

    public CartAdapter(List<CartItem> cartItems, Context context){
        this.cartItems = cartItems;
        this.context = context;
    }

    @Override
    public CartAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.cart_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CartAdapter.ViewHolder holder, final int position) {


        CartItem cartItem = cartItems.get(position);
        holder.bind(cartItem);

        holder.bookName.setText(cartItem.getBookName());
        holder.authorName.setText(cartItem.getAuthorName());



        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("REMOVE", "Remove item " + position);
                ShoppingCartActivity.cartItems.remove(position);
                ShoppingCartActivity.adapter.notifyDataSetChanged();

                SharedPreferences SP;
                SP = PreferenceManager.getDefaultSharedPreferences(context);
                String currentCartItems = SP.getString(CurrentUser.UID, null);

                String bookToRemove = (currentCartItems.split(","))[position];

                if((currentCartItems.split(",")).length == 1){
                    SharedPreferences.Editor edit = SP.edit();
                    edit.remove(CurrentUser.UID);
                    edit.commit();
                    ShoppingCartActivity.adapter.notifyDataSetChanged();
                    return;
                }

                //String test =  currentCartItems.replace(bookToRemove + ",", "");
                String newCartItems = currentCartItems.replaceAll(bookToRemove+",","").replaceAll(""+","+bookToRemove,"").replaceAll(bookToRemove, "");

                SharedPreferences.Editor edit = SP.edit();
                edit.putString (CurrentUser.UID, newCartItems);
                Log.v("SHARED PREFERENCE:", newCartItems);
                edit.commit();

                ShoppingCartActivity.adapter.notifyDataSetChanged();

            }
        });
    }

    @Override
    public int getItemCount() {
        if(ShoppingCartActivity.cartItems == null) return 0;
        return ShoppingCartActivity.cartItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{


        private CartItem mCartItem;
        public TextView bookName, authorName, remove;

        public ViewHolder(View itemView) {
            super(itemView);

            bookName = (TextView) itemView.findViewById(R.id.cart_book_name);
            authorName = (TextView) itemView.findViewById(R.id.cart_author);
            remove = (TextView) itemView.findViewById(R.id.remove);

        }

        public void bind(final CartItem cartItem){
            mCartItem = cartItem;
            bookName.setText(cartItem.getBookName());
            authorName.setText(cartItem.getAuthorName());

            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    cartItems.remove(mCartItem);
                    int position = cartItems.indexOf(mCartItem);
                    notifyDataSetChanged();

//                    Log.d("REMOVE", "Remove item " + position);
//
//
//                    ShoppingCartActivity.cartItems.remove(position);
//                    ShoppingCartActivity.adapter.notifyDataSetChanged();

                    SharedPreferences SP;
                    SP = PreferenceManager.getDefaultSharedPreferences(context);
                    String currentCartItems = SP.getString(CurrentUser.UID, null);

                    String bookToRemove = (currentCartItems.split(","))[position];

                    if((currentCartItems.split(",")).length == 1){
                        SharedPreferences.Editor edit = SP.edit();
                        edit.remove(CurrentUser.UID);
                        edit.commit();
                        ShoppingCartActivity.adapter.notifyDataSetChanged();
                        return;
                    }

                    //String test =  currentCartItems.replace(bookToRemove + ",", "");
                    //String newCartItems = currentCartItems.replaceAll(bookToRemove+",","").replaceAll(""+","+bookToRemove,"").replaceAll(bookToRemove, "");

                    if(currentCartItems.contains(bookToRemove)) currentCartItems=currentCartItems.replaceAll(bookToRemove,"");
                    if(currentCartItems.contains(",,")) currentCartItems=currentCartItems.replaceAll(",,",",");
                    String newCartItems=currentCartItems;

                    SharedPreferences.Editor edit = SP.edit();
                    edit.putString (CurrentUser.UID, newCartItems);
                    Log.v("SHARED PREFERENCE:", newCartItems);
                    System.out.println("####Items after removal"+newCartItems);
                    edit.commit();

                    ShoppingCartActivity.adapter.notifyDataSetChanged();

                }
            });

        }
    }
}
