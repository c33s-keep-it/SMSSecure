/** 
 * Copyright (C) 2011 Whisper Systems
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.smssecure.smssecure.mms;

import java.io.IOException;

import org.smssecure.smssecure.R;
import org.smssecure.smssecure.crypto.MasterSecret;
import org.smssecure.smssecure.util.ListenableFutureTask;
import org.smssecure.smssecure.util.ResUtil;

import ws.com.google.android.mms.pdu.PduPart;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Pair;

public class VideoSlide extends Slide {

  public VideoSlide(Context context, Uri uri) throws IOException, MediaTooLargeException {
    super(context, constructPartFromUri(context, uri));
  }

  public VideoSlide(Context context, MasterSecret masterSecret, PduPart part) {
    super(context, masterSecret, part);
  }

  @Override
  public ListenableFutureTask<Pair<Drawable,Boolean>> getThumbnail(Context context) {
    return new ListenableFutureTask<>(new Pair<>(ResUtil.getDrawable(context, R.attr.conversation_icon_attach_video), true));
  }

  @Override
  public boolean hasImage() {
    return true;
  }

  @Override
  public boolean hasVideo() {
    return true;
  }

  private static PduPart constructPartFromUri(Context context, Uri uri)
      throws IOException, MediaTooLargeException
  {
    PduPart         part     = new PduPart();
    ContentResolver resolver = context.getContentResolver();
    Cursor          cursor   = null;

    try {
      cursor = resolver.query(uri, new String[] {MediaStore.Video.Media.MIME_TYPE}, null, null, null);
      if (cursor != null && cursor.moveToFirst()) {
        Log.w("VideoSlide", "Setting mime type: " + cursor.getString(0));
        part.setContentType(cursor.getString(0).getBytes());
      }
    } finally {
      if (cursor != null)
        cursor.close();
    }

    assertMediaSize(context, uri);
    part.setDataUri(uri);
    part.setContentId((System.currentTimeMillis()+"").getBytes());
    part.setName(("Video" + System.currentTimeMillis()).getBytes());

    return part;
  }
}
