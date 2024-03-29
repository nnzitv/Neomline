package com.bkaiquesilva.nnzi.Playaudio;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.StrictMode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.contentcapture.ContentCaptureCondition;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;

import com.bkaiquesilva.nnzi.Playaudio.PlayerVisualizerSeekbar;
import com.bkaiquesilva.nnzi.R;


public class VoicePlayerView extends LinearLayout {

    private int playPaueseBackgroundColor, shareBackgroundColor, viewBackgroundColor,
            seekBarProgressColor, seekBarThumbColor, timingBackgroundColor,playPauseShape,
            visualizationPlayedColor, visualizationNotPlayedColor, playProgressbarColor;
    private float viewCornerRadius, playPauseCornerRadius;
    private boolean showTiming, enableVirtualizer;
    private GradientDrawable viewShape;
    private Context context;
    private String path;
    private String progressTimeColor;

    private LinearLayout main_layout, padded_layout, container_layout;
    private ImageView imgPlay, imgPause;
    private ProgressBar progressBar;
    private TextView txtProcess;
    private MediaPlayer mediaPlayer;
    private ProgressBar pb_play;

    private PlayerVisualizerSeekbar seekbarV;
    private Uri contentUri = null;

    public VoicePlayerView(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.main_view, this);
        this.context = context;
    }

    public VoicePlayerView(Context context,  AttributeSet attrs) {
        super(context, attrs);
        initViews(context, attrs);
        this.context = context;
    }

    public VoicePlayerView(Context context,  AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(context, attrs);
        this.context = context;
    }

    private void initViews(Context context, AttributeSet attrs) {

        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.VoicePlayerView, 0, 0);

        viewShape = new GradientDrawable();
        //playPauseShape = new GradientDrawable();

        try{
            showTiming = typedArray.getBoolean(R.styleable.VoicePlayerView_showTiming, true);
            viewCornerRadius = typedArray.getFloat(R.styleable.VoicePlayerView_viewCornerRadius, 0);
            playPauseCornerRadius = typedArray.getFloat(R.styleable.VoicePlayerView_playPauseCornerRadius, 0);
            playPaueseBackgroundColor = typedArray.getColor(R.styleable.VoicePlayerView_playPauseBackgroundColor, getResources().getColor(R.color.pink));
            shareBackgroundColor = typedArray.getColor(R.styleable.VoicePlayerView_shareBackgroundColor, getResources().getColor(R.color.pink));
            viewBackgroundColor = typedArray.getColor(R.styleable.VoicePlayerView_viewBackground, getResources().getColor(R.color.branco));
            seekBarProgressColor = typedArray.getColor(R.styleable.VoicePlayerView_seekBarProgressColor, getResources().getColor(R.color.pink));
            seekBarThumbColor = typedArray.getColor(R.styleable.VoicePlayerView_seekBarThumbColor, getResources().getColor(R.color.pink));
            progressTimeColor = "#FFFFFF";
            enableVirtualizer = typedArray.getBoolean(R.styleable.VoicePlayerView_enableVisualizer, false);
            timingBackgroundColor = typedArray.getColor(R.styleable.VoicePlayerView_timingBackgroundColor, getResources().getColor(android.R.color.transparent));
            visualizationNotPlayedColor = typedArray.getColor(R.styleable.VoicePlayerView_visualizationNotPlayedColor, getResources().getColor(R.color.gray));
            visualizationPlayedColor = typedArray.getColor(R.styleable.VoicePlayerView_visualizationPlayedColor, getResources().getColor(R.color.pink));
            playProgressbarColor= typedArray.getColor(R.styleable.VoicePlayerView_playProgressbarColor, getResources().getColor(R.color.pink));



        }finally {
            typedArray.recycle();
        }


        LayoutInflater.from(context).inflate(R.layout.main_view, this);
        main_layout = this.findViewById(R.id.collectorLinearLayout);
        padded_layout = this.findViewById(R.id.paddedLinearLayout);
        container_layout = this.findViewById(R.id.containerLinearLayout);
        imgPlay = this.findViewById(R.id.imgPlay);
        imgPause = this.findViewById(R.id.imgPause);
        progressBar = this.findViewById(R.id.progressBar);
        txtProcess = this.findViewById(R.id.txtTime);
        seekbarV = this.findViewById(R.id.seekBarV);
        pb_play = this.findViewById(R.id.pb_play);


        viewShape.setColor(viewBackgroundColor);
        viewShape.setCornerRadius(viewCornerRadius);
        //playPauseShape.setColor(playPaueseBackgroundColor);
        //playPauseShape.setCornerRadius(playPauseCornerRadius);

        main_layout.setBackground(viewShape);

        GradientDrawable timingBackground = new GradientDrawable();
        timingBackground.setColor(timingBackgroundColor);
        timingBackground.setCornerRadius(25);
        txtProcess.setBackground(timingBackground);
        txtProcess.setPadding(16, 0, 16, 0);
        txtProcess.setTextColor(Color.parseColor("#FAFCFF"));


        pb_play.getIndeterminateDrawable().setColorFilter(
                playProgressbarColor,
                PorterDuff.Mode.SRC_IN);



        if (!showTiming)
            txtProcess.setVisibility(INVISIBLE);

            seekbarV.setVisibility(VISIBLE);
            seekbarV.getProgressDrawable().setColorFilter(getResources().getColor(android.R.color.transparent), PorterDuff.Mode.SRC_IN);
            seekbarV.getThumb().setColorFilter(getResources().getColor(android.R.color.transparent), PorterDuff.Mode.SRC_IN);
            seekbarV.setColors(visualizationPlayedColor, visualizationNotPlayedColor);

    }


    //Set the audio source and prepare mediaplayer

    public void setAudio(String audioPath){
        path = audioPath;
        mediaPlayer =  new MediaPlayer();
        if (path != null) {
            try {
                mediaPlayer.setDataSource(path);
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.prepare();
                mediaPlayer.setVolume(10, 10);
                //START and PAUSE are in other listeners
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        seekbarV.setMax(mp.getDuration());
                        txtProcess.setText("00:00:00/"+convertSecondsToHMmSs(mp.getDuration() / 1000));
                    }
                });
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                            imgPause.setVisibility(View.GONE);
                            imgPlay.setVisibility(View.VISIBLE);
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        imgPlay.setOnClickListener(imgPlayClickListener);
        imgPause.setOnClickListener(imgPauseClickListener);
        seekbarV.updateVisualizer(new File(path));
        seekbarV.setOnSeekBarChangeListener(seekBarListener);
        seekbarV.updateVisualizer(new File(path));
    }



    //Components' listeners

    OnClickListener imgPlayClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    imgPause.setVisibility(View.VISIBLE);
                    imgPlay.setVisibility(View.GONE);
                }
            });

            try{
                if (mediaPlayer != null){
                    mediaPlayer.start();
                }
                update(mediaPlayer, txtProcess, context);
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    };



    private SeekBar.OnSeekBarChangeListener seekBarListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(final SeekBar seekBar, final int progress, boolean fromUser) {
            if (fromUser) {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mediaPlayer.seekTo(progress);
                        update(mediaPlayer, txtProcess, context);
                        seekbarV.updatePlayerPercent((float) mediaPlayer.getCurrentPosition()/mediaPlayer.getDuration());
                    }
                });
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    imgPause.setVisibility(View.GONE);
                    imgPlay.setVisibility(View.VISIBLE);
                }
            });
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    imgPlay.setVisibility(View.GONE);
                    imgPause.setVisibility(View.VISIBLE);
                    try{
                        mediaPlayer.start();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });

        }
    };

    OnClickListener imgPauseClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    imgPause.setVisibility(View.GONE);
                    imgPlay.setVisibility(View.VISIBLE);
                    try{
                        mediaPlayer.pause();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });

        }
    };

    //Updating seekBar in realtime
    private void update(final MediaPlayer mediaPlayer, final TextView time, final Context context) {
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                    seekbarV.setProgress(mediaPlayer.getCurrentPosition());
                    seekbarV.updatePlayerPercent((float) mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration());



                if (mediaPlayer.getDuration() - mediaPlayer.getCurrentPosition() > 100) {
                    time.setText(convertSecondsToHMmSs(mediaPlayer.getCurrentPosition() / 1000) + " / " + convertSecondsToHMmSs(mediaPlayer.getDuration() / 1000));
                }
                else {
                    time.setText(convertSecondsToHMmSs(mediaPlayer.getDuration() / 1000));
                    seekbarV.updatePlayerPercent(0);
                    seekbarV.setProgress(0);
                }
                Handler handler = new Handler();
                try{
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            try{
                                if (mediaPlayer.getCurrentPosition() > -1) {
                                    try {
                                        update(mediaPlayer, time, context);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    };
                    handler.postDelayed(runnable, 500);
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });
    }

    //Convert long milli seconds to a formatted String to display it

    private static String convertSecondsToHMmSs(long seconds) {
        long s = seconds % 60;
        long m = (seconds / 60) % 60;
        long h = (seconds / (60 * 60)) % 24;
        return String.format("%02d:%02d:%02d", h,m,s);
    }

    //These both functions to avoid mediaplayer errors

    public void onStop(){
        try{
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void onPause(){
        try{
            if (mediaPlayer != null){
                if (mediaPlayer.isPlaying())
                    mediaPlayer.pause();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                imgPause.setVisibility(View.GONE);
                imgPlay.setVisibility(View.VISIBLE);
            }
        });
    }


  // Programmatically functions

   public void setViewBackgroundShape(int color, float radius){
       GradientDrawable shape = new GradientDrawable();
       shape.setColor(getResources().getColor(color));
       shape.setCornerRadius(radius);
       main_layout.setBackground(shape);
   }
    public void setShareBackgroundShape(int color, float radius){
        GradientDrawable shape = new GradientDrawable();
        shape.setColor(getResources().getColor(color));
        shape.setCornerRadius(radius);
    }
    public void setPlayPaueseBackgroundShape(int color, float radius){
        GradientDrawable shape = new GradientDrawable();
        shape.setColor(getResources().getColor(R.color.tranparente));
        shape.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        shape.setCornerRadius(radius);
        imgPause.setBackground(shape);
        imgPlay.setBackground(shape);
    }
    public void setSeekBarStyle(int progressColor, int thumbColor){
        seekbarV.getProgressDrawable().setColorFilter(getResources().getColor(progressColor), PorterDuff.Mode.SRC_IN);
        seekbarV.getThumb().setColorFilter(getResources().getColor(thumbColor), PorterDuff.Mode.SRC_IN);
    }
    public void setTimingVisibility(boolean visibility){
       if (!visibility)
           txtProcess.setVisibility(INVISIBLE);
       else
           txtProcess.setVisibility(VISIBLE);
    }

    public void showPlayProgressbar(){
        imgPlay.setVisibility(GONE);
        pb_play.setVisibility(VISIBLE);
    }

    public void hidePlayProgresbar(){
        pb_play.setVisibility(GONE);
        imgPlay.setVisibility(VISIBLE);
    }

        public void refreshPlayer(String audioPath){
            path = audioPath;
            if (mediaPlayer != null){
                try{
                    if (mediaPlayer.isPlaying()){
                    mediaPlayer.stop();
                }
                mediaPlayer.release();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            mediaPlayer = null;
            mediaPlayer =  new MediaPlayer();
            if (path != null) {
                try {
                    mediaPlayer.setDataSource(path);
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mediaPlayer.prepare();
                    mediaPlayer.setVolume(10, 10);
                    //START and PAUSE are in other listeners
                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(final MediaPlayer mp) {
                            ((Activity) context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                        seekbarV.setMax(mp.getDuration());
                                        seekbarV.setProgress(0);

                                    if (imgPause.getVisibility() == View.VISIBLE){
                                        imgPause.setVisibility(View.GONE);
                                        imgPlay.setVisibility(View.VISIBLE);
                                    }
                                    txtProcess.setText("00:00:00/"+convertSecondsToHMmSs(mp.getDuration() / 1000));
                                }
                            });
                        }
                    });
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            ((Activity) context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    imgPause.setVisibility(View.GONE);
                                    imgPlay.setVisibility(View.VISIBLE);
                                }
                            });
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    imgPlay.setOnClickListener(imgPlayClickListener);
                    imgPause.setOnClickListener(imgPauseClickListener);
                    seekbarV.updateVisualizer(new File(path));
                    seekbarV.setOnSeekBarChangeListener(seekBarListener);
                    seekbarV.updateVisualizer(new File(path));
                }
            });

            seekbarV.invalidate();
            this.invalidate();
        }


    public void refreshVisualizer(){
            seekbarV.updateVisualizer(new File(path));
    }
    public ProgressBar getPlayProgressbar(){
        return pb_play;
    }

        public int getPlayPaueseBackgroundColor() {
            return playPaueseBackgroundColor;
        }

        public void setPlayPaueseBackgroundColor(int playPaueseBackgroundColor) {
            this.playPaueseBackgroundColor = playPaueseBackgroundColor;
        }

        public int getShareBackgroundColor() {
            return shareBackgroundColor;
        }

        public void setShareBackgroundColor(int shareBackgroundColor) {
            this.shareBackgroundColor = shareBackgroundColor;
        }

        public int getViewBackgroundColor() {
            return viewBackgroundColor;
        }

        public void setViewBackgroundColor(int viewBackgroundColor) {
            this.viewBackgroundColor = viewBackgroundColor;
        }

        public int getSeekBarProgressColor() {
            return seekBarProgressColor;
        }

        public void setSeekBarProgressColor(int seekBarProgressColor) {
            this.seekBarProgressColor = seekBarProgressColor;
        }

        public int getSeekBarThumbColor() {
            return seekBarThumbColor;
        }

        public void setSeekBarThumbColor(int seekBarThumbColor) {

            this.seekBarThumbColor = seekBarThumbColor;
        }

        public String getProgressTimeColor() {
            return progressTimeColor;
        }

        public void setProgressTimeColor(String progressTimeColor) {
        if (progressTimeColor != null) {
            txtProcess.setTextColor(Color.parseColor(progressTimeColor));
        }
            this.progressTimeColor = progressTimeColor;
        }

        public int getTimingBackgroundColor() {
            return timingBackgroundColor;
        }

        public void setTimingBackgroundColor(int timingBackgroundColor) {
            this.timingBackgroundColor = timingBackgroundColor;
        }

        public int getVisualizationPlayedColor() {
            return visualizationPlayedColor;
        }

        public void setVisualizationPlayedColor(int visualizationPlayedColor) {
            this.visualizationPlayedColor = visualizationPlayedColor;
        }

        public int getVisualizationNotPlayedColor() {
            return visualizationNotPlayedColor;
        }

        public void setVisualizationNotPlayedColor(int visualizationNotPlayedColor) {
            this.visualizationNotPlayedColor = visualizationNotPlayedColor;
        }

        public int getPlayProgressbarColor() {
            return playProgressbarColor;
        }

        public void setPlayProgressbarColor(int playProgressbarColor) {
            this.playProgressbarColor = playProgressbarColor;
        }

        public float getViewCornerRadius() {
            return viewCornerRadius;
        }

        public void setViewCornerRadius(float viewCornerRadius) {
            this.viewCornerRadius = viewCornerRadius;
        }

        public float getPlayPauseCornerRadius() {
            return playPauseCornerRadius;
        }

        public void setPlayPauseCornerRadius(float playPauseCornerRadius) {
            this.playPauseCornerRadius = playPauseCornerRadius;
        }

        public boolean isShowTiming() {
            return showTiming;
        }

        public void setShowTiming(boolean showTiming) {
            this.showTiming = showTiming;
        }

        public boolean isEnableVirtualizer() {
            return enableVirtualizer;
        }

        public void setEnableVirtualizer(boolean enableVirtualizer) {
            this.enableVirtualizer = enableVirtualizer;
        }

        public int getPlayPauseShape() {
            return playPauseShape;
        }

        public void setPlayPauseShape(Context c, int playPauseShape) {
        if (playPauseShape != 0){
            imgPlay.setColorFilter(ContextCompat.getColor(c, playPauseShape));
            imgPause.setColorFilter(ContextCompat.getColor(c, playPauseShape));
        }
        }

        public GradientDrawable getViewShape() {
            return viewShape;
        }

        public void setViewShape(GradientDrawable viewShape) {
            this.viewShape = viewShape;
        }

        public void setContext(Context context) {
            this.context = context;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public LinearLayout getMain_layout() {
            return main_layout;
        }

        public void setMain_layout(LinearLayout main_layout) {
            this.main_layout = main_layout;
        }

        public LinearLayout getPadded_layout() {
            return padded_layout;
        }

        public void setPadded_layout(LinearLayout padded_layout) {
            this.padded_layout = padded_layout;
        }

        public LinearLayout getContainer_layout() {
            return container_layout;
        }

        public void setContainer_layout(LinearLayout container_layout) {
            this.container_layout = container_layout;
        }

        public ImageView getImgPlay() {
            return imgPlay;
        }

        public void setImgPlay(ImageView imgPlay) {
            this.imgPlay = imgPlay;
        }

        public ImageView getImgPause() {
            return imgPause;
        }

        public void setImgPause(ImageView imgPause) {
            this.imgPause = imgPause;
        }

        public ProgressBar getProgressBar() {
            return progressBar;
        }

        public void setProgressBar(ProgressBar progressBar) {
            this.progressBar = progressBar;
        }

        public TextView getTxtProcess() {
            return txtProcess;
        }

        public void setTxtProcess(TextView txtProcess) {
            this.txtProcess = txtProcess;
        }

        public MediaPlayer getMediaPlayer() {
            return mediaPlayer;
        }

        public void setMediaPlayer(MediaPlayer mediaPlayer) {
            this.mediaPlayer = mediaPlayer;
        }

        public ProgressBar getPb_play() {
            return pb_play;
        }

        public void setPb_play(ProgressBar pb_play) {
            this.pb_play = pb_play;
        }

        public PlayerVisualizerSeekbar getSeekbarV() {
            return seekbarV;
        }

        public void setSeekbarV(PlayerVisualizerSeekbar seekbarV) {
            this.seekbarV = seekbarV;
        }

        public OnClickListener getImgPlayClickListener() {
            return imgPlayClickListener;
        }

        public void setImgPlayClickListener(OnClickListener imgPlayClickListener) {
            this.imgPlayClickListener = imgPlayClickListener;
        }

        public SeekBar.OnSeekBarChangeListener getSeekBarListener() {
            return seekBarListener;
        }

        public void setSeekBarListener(SeekBar.OnSeekBarChangeListener seekBarListener) {
            this.seekBarListener = seekBarListener;
        }

        public OnClickListener getImgPauseClickListener() {
            return imgPauseClickListener;
        }

        public void setImgPauseClickListener(OnClickListener imgPauseClickListener) {
            this.imgPauseClickListener = imgPauseClickListener;
        }


        public Uri getContentUri() {
            return contentUri;
        }

        public void setContentUri(Uri contentUri) {
            this.contentUri = contentUri;
        }
    }


