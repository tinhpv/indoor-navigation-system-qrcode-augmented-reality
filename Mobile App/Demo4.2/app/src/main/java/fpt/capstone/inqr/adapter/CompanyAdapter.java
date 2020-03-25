//package com.example.inqr.adapter;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.inqr.R;
//import com.example.inqr.fragment.ListBuildingFragment;
//import com.example.inqr.model.Company;
//
//import java.util.List;
//
//public class CompanyAdapter extends RecyclerView.Adapter<CompanyHolder> {
//
//    private List<Company> listCompany;
//    private ListBuildingFragment fragment;
//
//    public CompanyAdapter(ListBuildingFragment fragment) {
//        this.fragment = fragment;
//    }
//
//    public void setListCompany(List<Company> listCompany) {
//        this.listCompany = listCompany;
//        notifyDataSetChanged();
//    }
//
//    @NonNull
//    @Override
//    public CompanyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_company_adapter, parent, false);
//
//        return new CompanyHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull CompanyHolder holder, int position) {
//        Company company = listCompany.get(position);
//
//        holder.tvName.setText(company.getName());
//
//        // setup show list building
//        BuildingAdapter adapter = new BuildingAdapter(fragment);
//        adapter.setListBuilding(company.getListBuilding());
//
//
//    }
//
//    @Override
//    public int getItemCount() {
//        return listCompany.size();
//    }
//}
//
//class CompanyHolder extends RecyclerView.ViewHolder {
//
//    TextView tvName;
//    RecyclerView rvBuilding;
//
//    public CompanyHolder(@NonNull View itemView) {
//        super(itemView);
//
//        tvName = itemView.findViewById(R.id.tvName);
//        rvBuilding = itemView.findViewById(R.id.rvBuilding);
//    }
//}
