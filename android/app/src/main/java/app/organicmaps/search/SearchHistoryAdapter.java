package app.organicmaps.search;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import app.organicmaps.MwmApplication;
import app.organicmaps.R;
import app.organicmaps.sdk.routing.RoutingController;
import app.organicmaps.sdk.search.SearchRecents;
import app.organicmaps.util.Graphics;
import app.organicmaps.widget.SearchToolbarController;
import com.google.android.material.textview.MaterialTextView;

class SearchHistoryAdapter extends RecyclerView.Adapter<SearchHistoryAdapter.ViewHolder>
{
  private static final int TYPE_ITEM = 0;
  private static final int TYPE_CLEAR = 1;
  private static final int TYPE_MY_POSITION = 2;
  private static final int TYPE_CONTACT = 3;
  private final ActivityResultLauncher<Intent> mContactPickerLauncher;

  @NonNull
  private final SearchToolbarController mSearchToolbarController;
  private final boolean mShowMyPosition;

  public static class ViewHolder extends RecyclerView.ViewHolder
  {
    private final MaterialTextView mText;

    public ViewHolder(View itemView)
    {
      super(itemView);
      mText = (MaterialTextView) itemView;
      Graphics.tint(mText);
    }
  }

  public SearchHistoryAdapter(@NonNull SearchToolbarController searchToolbarController, boolean showMyPosition, ActivityResultLauncher<Intent> launcher)
  {
    SearchRecents.refresh();
    mSearchToolbarController = searchToolbarController;
    mShowMyPosition = showMyPosition;
    this.mContactPickerLauncher = launcher;
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int type)
  {
    final ViewHolder res;

    switch (type)
    {
    case TYPE_ITEM:
      res = new ViewHolder(
          LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_search_recent, viewGroup, false));
      res.mText.setOnClickListener(v -> mSearchToolbarController.setQuery(res.mText.getText()));
      break;

    case TYPE_CLEAR:
      res = new ViewHolder(
          LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_search_clear_history, viewGroup, false));
      res.mText.setOnClickListener(v -> {
        SearchRecents.clear();
        notifyDataSetChanged();
      });
      break;
      case TYPE_CONTACT:
        res = new ViewHolder(
                LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_search_my_position, viewGroup, false));

        res.mText.setOnClickListener(v -> {
          Intent intent = new Intent(Intent.ACTION_PICK);
          intent.setType(android.provider.ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_TYPE);
          mContactPickerLauncher.launch(intent);
        });
        break;

    default: throw new IllegalArgumentException("Unsupported ViewHolder type given");
    }

    Graphics.tint(res.mText);
    return res;
  }

  @Override
  public void onBindViewHolder(ViewHolder viewHolder, int position)
  {
    if (getItemViewType(position) == TYPE_ITEM)
    {
      if (mShowMyPosition)
        position--;

      viewHolder.mText.setText(SearchRecents.get(position));
    }
  }

  @Override
  public int getItemCount()
  {
    int res = SearchRecents.getSize();
    if (res > 0)
      res++;

    if (mShowMyPosition)
      res++;

    return res;
  }

  @Override
  public int getItemViewType(int position)
  {
    if (mShowMyPosition)
    {
      if (position == 0)
        return TYPE_MY_POSITION;

      position--;
    }

    return (position < SearchRecents.getSize() ? TYPE_ITEM : TYPE_CLEAR);
  }
}
