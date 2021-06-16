package com.example.SocyMusic;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.musicplayer.R;
import com.gauravk.audiovisualizer.visualizer.BarVisualizer;
import com.woxthebox.draglistview.DragItemAdapter;
import com.woxthebox.draglistview.DragListView;

public class QueueFragment extends Fragment {
    private DragListView listView;
    private ItemAdapter adapter;
    private QueueFragmentHost hostCallBack;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_queue, container, false);

        listView = view.findViewById(R.id.queue_song_listview);
        listView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ItemAdapter(R.layout.queue_item, R.id.queue_small_visualizer, false);
        listView.setAdapter(adapter, false);
        listView.setDragListListener(new DragListView.DragListListener() {
            @Override
            public void onItemDragStarted(int position) {

            }

            @Override
            public void onItemDragging(int itemPosition, float x, float y) {

            }

            @Override
            public void onItemDragEnded(int fromPosition, int toPosition) {
                SongsData.getInstance().onQueueReordered(fromPosition, toPosition);
                adapter.releasePlayingVisualizer();
            }
        });
        listView.setCanDragHorizontally(false);
        return view;
    }

    @Override
    public void onDestroyView() {
        adapter.releasePlayingVisualizer();
        super.onDestroyView();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            this.hostCallBack = (QueueFragment.QueueFragmentHost) context;
        } catch (final ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnCompleteListener");
        }
    }

    public void updateSong() {
        adapter.notifyDataSetChanged();
    }

    class ItemAdapter extends DragItemAdapter<Song, ItemAdapter.ViewHolder> {
        private ViewHolder playingHolder;
        private final int grabHandleID;
        private final boolean dragOnLongPress;
        private final int layoutID;

        public ItemAdapter(int layoutID, int grabHandleID, boolean dragOnLongPress) {
            super();
            this.layoutID = layoutID;
            this.grabHandleID = grabHandleID;
            this.dragOnLongPress = dragOnLongPress;
            setItemList(SongsData.getInstance().getPlayingQueue());
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(layoutID, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            super.onBindViewHolder(holder, position);
            holder.position = position;
            Song song = SongsData.getInstance().getSongFromQueueAt(position);
            holder.setSong(song);
            if (holder.isPlaying())
                playingHolder = holder;
            holder.updateViews();
        }

        void releasePlayingVisualizer() {
            if (playingHolder != null)
                playingHolder.visualizer.release();
        }

        @Override
        public long getUniqueItemId(int position) {
            Song song = SongsData.getInstance().getSongFromQueueAt(position);
            return song.hashCode();
        }

        @Override
        public int getItemCount() {
            return SongsData.getInstance().getPlayingQueueCount();
        }


        class ViewHolder extends DragItemAdapter.ViewHolder {
            private final TextView songTitleTextView;
            private final BarVisualizer visualizer;
            private int position;
            private Song song;

            public ViewHolder(View itemView) {
                super(itemView, grabHandleID, dragOnLongPress);
                songTitleTextView = itemView.findViewById(R.id.queue_song_title_text_view);
                visualizer = itemView.findViewById(R.id.queue_small_visualizer);
            }

            public void updateViews() {
                songTitleTextView.setText(song.getTitle());
                visualizer.release();
                if (isPlaying()) {
                    visualizer.setBackground(null);

                    int audioSessionID = MediaPlayerUtil.getAudioSessionId();
                    if (audioSessionID != -1 && audioSessionID != 0)
                        visualizer.setAudioSessionId(audioSessionID);
                    visualizer.show();
                } else {
                    visualizer.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_drag_handle, null));
                    visualizer.hide();
                }
            }

            public void setSong(Song song) {
                this.song = song;
            }

            public boolean isPlaying() {
                return song.equals(SongsData.getInstance().getSongPlaying());
            }

            @Override
            public void onItemClicked(View view) {
                super.onItemClicked(view);
                SongsData.getInstance().setPlaying(position);
                MediaPlayerUtil.startPlaying(getContext(), SongsData.getInstance().getSongPlaying());
                updateViews();
                hostCallBack.onSongUpdate();
            }


        }

    }

    public interface QueueFragmentHost {
        void onSongUpdate();
    }
}

