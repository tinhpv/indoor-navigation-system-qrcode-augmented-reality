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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_buidling_adapter, parent, false);
        return new BuildingHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BuildingHolder holder, int position) {
        Building building = listBuilding.get(position);

        holder.tvBuildingName.setText(building.getName());
        holder.tvCompany.setText(building.getCompanyName());
        holder.progressBar.setVisibility(View.INVISIBLE);

//        if (checkExpired(building.getDayExpired())) {
//            holder.bgCard.setBackgroundColor(Color.WHITE);
//        } else {
        holder.bgCard.setBackgroundColor(Color.WHITE);
//        }

        if (building.getStatus() == Building.NOT_DOWNLOAD) {
            holder.imgGetData.setImageResource(R.drawable.download);
            holder.imgGetData.setVisibility(View.VISIBLE);

//            holder.imgDeleteData.setVisibility(View.INVISIBLE);
        } else if (building.getStatus() == Building.DOWNLOADED) {
            holder.imgGetData.setVisibility(View.INVISIBLE);
//            holder.imgDeleteData.setVisibility(View.VISIBLE);
        } else if (building.getStatus() == Building.UPDATE) {
            holder.imgGetData.setImageResource(R.drawable.update);
            holder.imgGetData.setVisibility(View.VISIBLE);
//            holder.imgDeleteData.setVisibility(View.VISIBLE);
        }


//        holder.imgDeleteData.setOnClickListener(v -> {
//            fragment.deleteBuildingData(building.getId(), building.getName());
//        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

//                if (checkExpired(building.getDayExpired())) {
                if (building.getStatus() != Building.NOT_DOWNLOAD) {
                    fragment.askDeleteBuildingData(building.getId(), building.getName());
                } else {
//                    Toasty.info(v.getContext(), building.getName() + " does not have data.", Toast.LENGTH_SHORT).show();
                    fragment.showWarning(building.getName() + " does not have data.");
                }
//                } else {
//                    Toasty.info(v.getContext(), building.getName() + " has expired.", Toast.LENGTH_SHORT).show();
//                }
                return true;
            }
        });


        holder.imgGetData.setOnClickListener(v -> {
//            if (checkExpired(building.getDayExpired())) {
//            holder.imgGetData.setVisibility(View.INVISIBLE);
//            holder.progressBar.setVisibility(View.VISIBLE);

            if (building.getStatus() == Building.UPDATE) {
                fragment.updateBuildingData(building.getId(), position);
            } else if (building.getStatus() == Building.NOT_DOWNLOAD) {
                fragment.downloadBuildingData(building.getId(), position);
            }
//            } else {
//                Toasty.info(v.getContext(), building.getName() + " has expired.", Toast.LENGTH_SHORT).show();
//            }
        });

        if (this.position == position) {
            holder.imgGetData.setVisibility(View.INVISIBLE);
            holder.progressBar.setVisibility(View.VISIBLE);
        }


        holder.imgShowInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                new AlertDialog.Builder(holder.itemView.getContext())
//                        .setTitle(building.getName())
//                        .setMessage(building.getDescription()
//                                + "\n\n"
//                                + "Ngày hết hạn: "
//                                + building.getDayExpired())
//
//                        .setPositiveButton(android.R.string.yes, null)
//                        .setCancelable(false)
//                        .show();
                fragment.showInfo(building);
            }
        });

        holder.itemView.setOnClickListener(v -> {
//                if (checkExpired(building.getDayExpired())) {
            if (building.getStatus() == Building.DOWNLOADED) {
                fragment.showMapFragment(building.getId());
            } else if (building.getStatus() == Building.NOT_DOWNLOAD) {
//                    Toasty.warning(v.getContext(), "Please download data of " + building.getName() + " before to use.", Toast.LENGTH_SHORT).show();
                fragment.showWarningDownload(building, WarningDownloadDialog.TYPE_DOWNLOAD, position);
            } else if (building.getStatus() == Building.UPDATE) {
                fragment.showWarningDownload(building, WarningDownloadDialog.TYPE_UPDATE, position);
            }
//                } else {
//                    Toasty.info(v.getContext(), building.getName() + " has expired.", Toast.LENGTH_SHORT).show();
//                }
        });
    }

//    private boolean checkExpired(String dayExpired) {
//        try {
//            DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
//            Date date1 = new Date();
//            Date date2 = df.parse(dayExpired);
//            long diff = (date2.getTime() - date1.getTime()) / (1000 * 60 * 60 * 24);
//
//            if (diff > 0) {
//                return true;
//            }
//        } catch (Exception e) {
//            Log.e("Error", e.getMessage());
//        }
//        return false;
//    }

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
//        imgDeleteData = itemView.findViewById(R.id.imgDeleteData);
    }
}