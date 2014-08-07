/**
 Copyright 2014 Google Inc. All rights reserved.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package com.google.googlemediaframeworkdemo.demo;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.libraries.mediaframework.exoplayerextensions.Video;
import com.google.android.libraries.mediaframework.layeredvideo.PlaybackControlLayer;
import com.google.android.libraries.mediaframework.layeredvideo.SimpleVideoPlayer;
import com.google.googlemediaframeworkdemo.demo.adplayer.ImaPlayer;

public class MainActivity extends Activity implements PlaybackControlLayer.FullscreenCallback {

  private ImaPlayer imaPlayer;
  private FrameLayout videoPlayerContainer;
  private ListView videoListView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    View view = getLayoutInflater().inflate(R.layout.activity_main, null);

    // Get rid of the action bar.
    getActionBar().hide();

    // This container will be the video player.
    videoPlayerContainer = (FrameLayout) view.findViewById(R.id.video_frame);

    // Lists the available videos.
    videoListView = (ListView) view.findViewById(R.id.video_list_view);

    // Retrieve the list of available videos.
    final VideoListItem[] videoListItems = getVideoListItems();

    // Extract the titles of the videos and put them into this array.
    final String[] videoTitles = new String[videoListItems.length];

    for (int i = 0; i < videoListItems.length; i++) {
      videoTitles[i] = videoListItems[i].title;
    }

    // Create a ListView of the available videos titles.
    videoListView.setAdapter(new ArrayAdapter<String>(this,
        android.R.layout.simple_list_item_1,
        videoTitles));

    // When a video is selected, create the ImaPlayer and play the video.
    videoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        createImaPlayer(videoListItems[i]);
      }
    });

    setContentView(view);
  }

  /**
   * Release the video player when the activity is destroyed.
   */
  @Override
  protected void onDestroy() {
    if (imaPlayer != null) {
      imaPlayer.release();
    }
    super.onDestroy();
  }

  /**
   * When a video has been selected, create an {@link ImaPlayer} and play the video.
   */
  public void createImaPlayer(VideoListItem videoListItem) {
    if (imaPlayer != null) {
      imaPlayer.release();
    }

    // If there was previously a video player in the container, remove it.
    videoPlayerContainer.removeAllViews();

    String adTagUrl = videoListItem.adUrl;
    String videoTitle = videoListItem.title;

    imaPlayer = new ImaPlayer(this,
        videoPlayerContainer,
        videoListItem.video,
        videoTitle,
        adTagUrl);
    imaPlayer.setFullscreenCallback(this);

    Resources res = getResources();

    // Customize the UI of the video player.

    // Set a logo (an Android icon will be displayed in the top left)
    Drawable logo = res.getDrawable(R.drawable.gmf_icon);
    imaPlayer.setLogoImage(logo);

    // Uncomment the following lines to set the color of the player's top bar, bottom bar, and
    // background to be a blue color.

    // int sampleChromeColor = res.getColor(R.color.sample_chrome_color);
    // imaPlayer.setChromeColor(sampleChromeColor);

    // Uncomment the following lines to set the color of the buttons and seekbar in the player
    // to be a green color.

    // int samplePlaybackControlColor = res.getColor(R.color.sample_playback_control_color);
    // imaPlayer.setPlaybackControlColor(samplePlaybackControlColor);

    // Add three buttons to the video player's set of action buttons.
    //
    // When the player is not fullscreen, there will be an overflow button in the top right of the
    // video player's playback control UI. When the overflow button is clicked, a dialog box
    // will appear listing the possible actions (in this case, "Option 1", "Option 2",
    // and "Option 3").
    //
    // When the player is in fullscreen, each of the buttons' icons (in this case, share, discard,
    // and favorite) will be displayed in the top right of the video player's playback control UI.
    //
    // When an action is triggered (either by clicking it in the dialog box when the video player is
    // not in fullscreen or by clicking its corresponding button when the video player is in
    // fullscreen), it will display a toast message.
    imaPlayer.addActionButton(
        res.getDrawable(R.drawable.ic_action_share),
        getString(R.string.option1),
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            Toast.makeText(MainActivity.this,
                getString(R.string.clicked_option_1),
                Toast.LENGTH_SHORT)
                .show();
          }
        }
    );
    imaPlayer.addActionButton(
        getResources().getDrawable(R.drawable.ic_action_discard),
        getString(R.string.option2),
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            Toast.makeText(MainActivity.this,
                getString(R.string.clicked_option_2),
                Toast.LENGTH_SHORT)
                .show();
          }
        }
    );
    imaPlayer.addActionButton(
        getResources().getDrawable(R.drawable.ic_action_favorite),
        getString(R.string.option3),
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            Toast.makeText(MainActivity.this,
                getString(R.string.clicked_option_3),
                Toast.LENGTH_SHORT)
                .show();
          }
        });

    // Now that the player is set up, let's start playing.
    imaPlayer.play();
  }

  public VideoListItem[] getVideoListItems() {
    return new VideoListItem[] {
        new VideoListItem("No ads (DASH)",
            new Video("http://www.youtube.com/api/manifest/dash/id/bf5bb2419360daf1/source/youtub" +
                "e?as=fmp4_audio_clear,fmp4_sd_hd_clear&sparams=ip,ipbits,expire,as&ip=0.0.0.0&ip" +
                "bits=0&expire=19000000000&signature=255F6B3C07C753C88708C07EA31B7A1A10703C8D.2D6" +
                "A28B21F921D0B245CDCF36F7EB54A2B5ABFC2&key=ik0",
                Video.VideoType.DASH_VOD,
                "bf5bb2419360daf1"),
            null),
        new VideoListItem("Skippable preroll (DASH)",
            new Video("http://www.youtube.com/api/manifest/dash/id/bf5bb2419360daf1/source/youtub" +
                "e?as=fmp4_audio_clear,fmp4_sd_hd_clear&sparams=ip,ipbits,expire,as&ip=0.0.0.0&ip" +
                "bits=0&expire=19000000000&signature=255F6B3C07C753C88708C07EA31B7A1A10703C8D.2D6" +
                "A28B21F921D0B245CDCF36F7EB54A2B5ABFC2&key=ik0",
                Video.VideoType.DASH_VOD,
                "bf5bb2419360daf1"),
            "http://pubads.g.doubleclick.net/gampad/ads?sz=400x300&iu=%2F6062%2Fgmf_demo&ciu_" +
            "szs&impl=s&gdfp_req=1&env=vp&output=xml_vast2&unviewed_position_start=1&url=[ref" +
            "errer_url]&correlator=[timestamp]&cust_params=gmf_format%3Dskip"),
        new VideoListItem("Unskippable preroll (DASH)",
            new Video("http://www.youtube.com/api/manifest/dash/id/bf5bb2419360daf1/source/youtub" +
                "e?as=fmp4_audio_clear,fmp4_sd_hd_clear&sparams=ip,ipbits,expire,as&ip=0.0.0.0&ip" +
                "bits=0&expire=19000000000&signature=255F6B3C07C753C88708C07EA31B7A1A10703C8D.2D6" +
                "A28B21F921D0B245CDCF36F7EB54A2B5ABFC2&key=ik0",
                Video.VideoType.DASH_VOD,
                "bf5bb2419360daf1"),
            "http://pubads.g.doubleclick.net/gampad/ads?sz=400x300&iu=%2F6062%2Fhanna_MA_grou" +
            "p%2Fvideo_comp_app&ciu_szs=&impl=s&gdfp_req=1&env=vp&output=xml_vast2&unviewed_p" +
            "osition_start=1&m_ast=vast&url=[referrer_url]&correlator=[timestamp]"),
        new VideoListItem("Ad rules - 0s, 5s, 10s, 15s (DASH)",
            new Video("http://www.youtube.com/api/manifest/dash/id/bf5bb2419360daf1/source/youtub" +
                "e?as=fmp4_audio_clear,fmp4_sd_hd_clear&sparams=ip,ipbits,expire,as&ip=0.0.0.0&ip" +
                "bits=0&expire=19000000000&signature=255F6B3C07C753C88708C07EA31B7A1A10703C8D.2D6" +
                "A28B21F921D0B245CDCF36F7EB54A2B5ABFC2&key=ik0",
                Video.VideoType.DASH_VOD,
                "bf5bb2419360daf1"),
            "http://pubads.g.doubleclick.net/gampad/ads?sz=400x300&iu=%2F6062%2Fgmf_demo&" +
            "ciu_szs&impl=s&gdfp_req=1&env=vp&output=xml_vast2&unviewed_position_start=1&" +
            "url=[referrer_url]&correlator=[timestamp]&ad_rule=1&cmsid=11924&vid=cWCkSYdF" +
            "lU0&cust_params=gmf_format%3Dstd%2Cskip"),
        new VideoListItem("No ads (mp4)",
            new Video("http://rmcdn.2mdn.net/MotifFiles/html/1248596/android_1330378998288.mp4",
                Video.VideoType.OTHER),
            null)
    };
  }

  /**
   * When the video player goes into fullscreen, hide the video list so that the video player can
   * occupy the entire screen.
   */
  @Override
  public void onGoToFullscreen() {
    videoListView.setVisibility(View.INVISIBLE);
  }

  /**
   * When the player returns from fullscreen, show the video list again.
   */
  @Override
  public void onReturnFromFullscreen() {
    videoListView.setVisibility(View.VISIBLE);
  }

  public static class VideoListItem {
    public final String title;
    public final Video video;
    public final String adUrl;

    public VideoListItem(String title, Video video, String adUrl) {
      this.title = title;
      this.video = video;
      this.adUrl = adUrl;
    }
  }


}
