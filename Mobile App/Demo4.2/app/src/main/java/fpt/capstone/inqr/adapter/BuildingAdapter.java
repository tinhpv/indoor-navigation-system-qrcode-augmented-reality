package fpt.capstone.inqr.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import fpt.capstone.inqr.R;
import fpt.capstone.inqr.dialog.WarningDownloadDialog;
import fpt.capstone.inqr.fragment.ListBuildingFragment;
import fpt.capstone.inqr.model.Building;

public class BuildingAdapter extends RecyclerView.Adapter<BuildingHolder> {

    private List<Building> listBuilding;
    private ListBuildingFragment fragment;
    private int position = -1;

    public BuildingAdapter(ListBuildingFragment fragment) {
        this.fragment = fragment;
    }

    public void setListBuilding(List<Building> listBuilding) {
        this.listBuilding = listBuilding;
        this.position = -1;
        notifyDataSetChanged();
    }

    public void setPosition(int position) {
        this.position = position;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BuildingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_buidling_adapter_updated, parent, false);
        return new BuildingHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BuildingHolder holder, int position) {
        Building building = listBuilding.get(position);

        holder.tvBuildingName.setText(building.getName());
        holder.tvCompany.setText(building.getCompanyName());
        holder.progressBar.setVisibility(View.INVISIBLE);


        // CHECK STATUS TO SHOW BUTTON
        if (building.getStatus() == Building.NOT_DOWNLOAD) {
            holder.imgGetData.setImageResource(R.drawable.ic_download);
            holder.imgGetData.setVisibility(View.VISIBLE);
        } else if (building.getStatus() == Building.DOWNLOADED) {
            holder.imgGetData.setVisibility(View.INVISIBLE);
        } else if (building.getStatus() == Building.UPDATE) {
            holder.imgGetData.setImageResource(R.drawable.ic_update);
            holder.imgGetData.setVisibility(View.VISIBLE);
        }

        // LONG TAPPING FOR DELETEING
        holder.itemView.setOnLongClickListener(v -> {
            if (building.getStatus() != Building.NOT_DOWNLOAD) {
                fragment.askDeleteBuildingData(building.getId(), building.getName());
            } else {
                fragment.showWarning(building.getName() + " does not have data.");
            }
            return true;
        });


        // HANDLE TAPPING DOWNLOAD | UPDATE
        holder.imgGetData.setOnClickListener(v -> {
            if (building.getStatus() == Building.UPDATE) {
                fragment.updateBuildingData(building.getId(), position);
            } else if (building.getStatus() == Building.NOT_DOWNLOAD) {
                fragment.downloadBuildingData(building.getId(), position);
            }
        });

        if (this.position == position) {
            holder.imgGetData.setVisibility(View.INVISIBLE);
            holder.progressBar.setVisibility(View.VISIBLE);
        }

        // HANDLE TAPPING INFORMATION
        holder.imgShowInfo.setOnClickListener(v -> fragment.showInfo(building));

        // HANDLE SHOW BUILDING'S MAP
        holder.itemView.setOnClickListener(v -> {
            if (building.getStatus() == Building.DOWNLOADED) {
                fragment.showMapFragment(building.getId());
            } else if (building.getStatus() == Building.NOT_DOWNLOAD) {
                fragment.showWarningDownload(building, WarningDownloadDialog.TYPE_DOWNLOAD, position);
            } else if (building.getStatus() == Building.UPDATE) {
                fragment.showWarningDownload(building, WarningDownloadDialog.TYPE_UPDATE, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listBuilding.size();
    }
}

class BuildingHolder extends RecyclerView.ViewHolder {

    TextView tvBuildingName, tvCompany;
    ImageView imgGetData, imgShowInfo;
    ConstraintLayout bgCard;
    ProgressBar progressBar;

    public BuildingHolder(@NonNull View itemView) {
        super(itemView);

        progressBar = itemView.findViewById(R.id.prLoading);
        tvBuildingName = itemView.findViewById(R.id.tvName);
        tvCompany = itemView.findViewById(R.id.tvCompany);
        imgGetData = itemView.findViewById(R.id.imgGetData);
        imgShowInfo = itemView.findViewById(R.id.imgShowInfo);
        bgCard = itemView.findViewById(R.id.bgCard);
    }
}