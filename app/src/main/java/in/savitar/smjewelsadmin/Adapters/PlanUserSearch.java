package in.savitar.smjewelsadmin.Adapters;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import in.savitar.smjewelsadmin.MainActivity;
import in.savitar.smjewelsadmin.ModalClasses.PlanListModal;
import in.savitar.smjewelsadmin.R;
import in.savitar.smjewelsadmin.mvp.ui.Dashboard.UserProfileFragment;


public class PlanUserSearch extends RecyclerView.Adapter<PlanUserSearch.MyViewHolder>
        implements Filterable {
    private Context context;
    private List<PlanListModal> contactList;
    private List<PlanListModal> contactListFiltered;
    private ContactsAdapterListener listener;
    public List<String> keys = new ArrayList<>();
    String planName;
    String setName;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, phone,id;
        public ImageView thumbnail;
        public LinearLayout singleUserListLayout;

        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.singleUserNamePlanList);
            phone = view.findViewById(R.id.singleUserPhonePlanList);
            id = view.findViewById(R.id.singleUserIDPlanList);
            thumbnail = view.findViewById(R.id.singleUserImagePlanList);
            singleUserListLayout = view.findViewById(R.id.singleUserListLayout);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected contact in callback
                    //listener.onContactSelected(contactListFiltered.get(getAdapterPosition()));
                }
            });

            thumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //NavigationUtilityMain.INSTANCE.toUserDetailsFragment();
                }
            });
        }
    }


    public PlanUserSearch(Context context, List<PlanListModal> contactList, List<String> keys, String planName, String setName) {
        this.context = context;
        //this.listener = listener;
        this.contactList = contactList;
        this.contactListFiltered = contactList;
        this.keys = keys;
        this.planName = planName;
        this.setName = setName;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_user_plan_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final PlanListModal contact = contactListFiltered.get(position);
        holder.name.setText(contact.getName());
        holder.phone.setText(contact.getPhone());

        try {
            holder.id.setText(contact.getID().toString());
        } catch (Exception e){
            holder.id.setText("");
        }

        if (contact.getProfilePhoto() != null){
            Glide.with(context)
                    .load(contact.getProfilePhoto())
                    .apply(RequestOptions.circleCropTransform())
                    .into(holder.thumbnail);
        }

        holder.singleUserListLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserProfileFragment fragment = new UserProfileFragment();
                Bundle args = new Bundle();
                args.putString("Name",contact.getName());
                args.putString("ID",contact.getID().toString());
                args.putString("Photo",contact.getProfilePhoto());
                args.putString("PlanName",planName);
                args.putString("UserKey",keys.get(position));
                if (planName.compareToIgnoreCase("PlanA")==0){
                    args.putString("SetName","Set1");
                }
                fragment.setArguments(args);
                FragmentTransaction transaction = ((MainActivity)context).getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.main_container, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

    }

    @Override
    public int getItemCount() {
        return contactListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    contactListFiltered = contactList;
                } else {
                    List<PlanListModal> filteredList = new ArrayList<>();
                    for (PlanListModal row : contactList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getName().toLowerCase().contains(charString.toLowerCase()) || row.getPhone().contains(charSequence) || row.getID().toString().contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }

                    contactListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = contactListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                contactListFiltered = (ArrayList<PlanListModal>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface ContactsAdapterListener {
        void onContactSelected(PlanListModal contact);
    }
}

