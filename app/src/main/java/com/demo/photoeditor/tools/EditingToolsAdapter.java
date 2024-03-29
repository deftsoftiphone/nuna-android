package com.demo.photoeditor.tools;


import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.demo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="https://github.com/burhanrashid52">Burhanuddin Rashid</a>
 * @version 0.1.2
 * @since 5/23/2018
 */
public class EditingToolsAdapter extends RecyclerView.Adapter<EditingToolsAdapter.ViewHolder> {

    private List<ToolModel> mToolList = new ArrayList<>();
    private OnItemSelected mOnItemSelected;

    Resources resources;
    public EditingToolsAdapter(OnItemSelected onItemSelected, Resources res) {
        mOnItemSelected = onItemSelected;
        resources=res;
        mToolList.add(new ToolModel(resources.getString(R.string.label_brush), R.drawable.ic_brush, ToolType.BRUSH));
        mToolList.add(new ToolModel(resources.getString(R.string.label_text), R.drawable.ic_text, ToolType.TEXT));
        mToolList.add(new ToolModel(resources.getString(R.string.label_eraser), R.drawable.ic_eraser, ToolType.ERASER));
        mToolList.add(new ToolModel(resources.getString(R.string.label_filter), R.drawable.ic_photo_filter, ToolType.FILTER));
        mToolList.add(new ToolModel(resources.getString(R.string.label_emoji), R.drawable.ic_insert_emoticon, ToolType.EMOJI));
        mToolList.add(new ToolModel(resources.getString(R.string.label_sticker), R.drawable.ic_sticker, ToolType.STICKER));
        mToolList.add(new ToolModel(resources.getString(R.string.label_more), R.drawable.ic_more_horiz_white_24dp, ToolType.MORE));
    }

    public interface OnItemSelected {
        void onToolSelected(ToolType toolType);
    }

    class ToolModel {
        private String mToolName;
        private int mToolIcon;
        private ToolType mToolType;

        ToolModel(String toolName, int toolIcon, ToolType toolType) {
            mToolName = toolName;
            mToolIcon = toolIcon;
            mToolType = toolType;
        }

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_editing_tools, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ToolModel item = mToolList.get(position);
        holder.txtTool.setText(item.mToolName);
        holder.imgToolIcon.setImageResource(item.mToolIcon);
    }

    @Override
    public int getItemCount() {
        return mToolList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgToolIcon;
        TextView txtTool;

        ViewHolder(View itemView) {
            super(itemView);
            imgToolIcon = itemView.findViewById(R.id.imgToolIcon);
            txtTool = itemView.findViewById(R.id.txtTool);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemSelected.onToolSelected(mToolList.get(getLayoutPosition()).mToolType);
                }
            });
        }
    }
}
