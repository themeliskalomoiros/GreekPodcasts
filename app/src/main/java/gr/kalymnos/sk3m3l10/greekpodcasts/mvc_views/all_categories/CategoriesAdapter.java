package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.all_categories;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import static gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.all_categories.AllCategoriesViewMvc.OnCategoryItemClickListener;

import com.squareup.picasso.Picasso;

import java.util.List;

import gr.kalymnos.sk3m3l10.greekpodcasts.R;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Category;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoryHolder> {

    private Context context;
    private List<Category> categories;

    private OnCategoryItemClickListener onCategoryItemClickListener;

    public CategoriesAdapter(Context context) {
        this.context = context;
    }

    public void addCategories(List<Category> categories) {
        this.categories = categories;
    }

    public void setOnCategoryItemClickListener(OnCategoryItemClickListener onCategoryItemClickListener) {
        this.onCategoryItemClickListener = onCategoryItemClickListener;
    }

    @NonNull
    @Override
    public CategoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(this.context);
        View itemView = inflater.inflate(R.layout.list_item_category, parent, false);
        return new CategoryHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryHolder holder, int position) {
        if (this.categories != null || this.categories.size() > 0) {
            Category podcast = this.categories.get(position);
            holder.bindCategory(podcast);
        }
    }

    @Override
    public int getItemCount() {
        if (this.categories != null) {
            return this.categories.size();
        }
        return 0;
    }

    class CategoryHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        static final int IC_HEADSET = R.drawable.ic_headset_black_light_148dp;
        static final int IC_ERROR = R.drawable.ic_error_black_light_148dp;
        final String TAG = CategoryHolder.class.getCanonicalName();

        TextView title, explanation;
        ImageView poster;

        public CategoryHolder(View itemView) {
            super(itemView);
            this.itemView.setOnClickListener(this);
            this.title = itemView.findViewById(R.id.list_item_category_title);
            this.explanation = itemView.findViewById(R.id.list_item_category_explanation);
            this.poster = itemView.findViewById(R.id.list_item_category_poster);
        }

        void bindCategory(Category category) {
            this.title.setText(category.getTitle());
            this.explanation.setText(category.getExplanation());
            Picasso.get().load(category.getImageUrl()).placeholder(IC_HEADSET).error(IC_ERROR).into(poster);
        }

        @Override
        public void onClick(View v) {
            if (onCategoryItemClickListener != null) {
                onCategoryItemClickListener.onCategoryItemClick(this.getAdapterPosition());
            }
        }
    }
}
