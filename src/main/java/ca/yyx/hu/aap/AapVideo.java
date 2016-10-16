package ca.yyx.hu.aap;

import java.util.ArrayDeque;

import ca.yyx.hu.utils.AppLog;

/**
 * @author algavris
 * @date 01/10/2016.
 */
class AapVideo {

    private final AapTransport mTransport;

    private byte vid_ack[] = {(byte) 0x80, 0x04, 0x08, 0, 0x10, 1};                    // Global Ack: 0, 1

    private static final int VIDEO_BUFS_NUM = 16;            // Maximum of NUM_vid_buf_BUFS - 1 in progress; 1 is never used

    private byte[] assy = new byte[65536 * 16];  // Global assembly buffer for video fragments: Up to 1 megabyte   ; 128K is fine for now at 800*640
    private int assy_size = 0;                   // Current size

    private ArrayDeque<ByteArray> mQueue = new ArrayDeque<>(VIDEO_BUFS_NUM);

    AapVideo(AapTransport transport) {
        mTransport = transport;
    }

    int buffersCount() {
        return  mQueue.size();// vid_buf_buf_tail - vid_buf_buf_head;
    }

    ByteArray poll() {
        return mQueue.poll();
    }

    public int process(AapMessage message) {
        return process(message.type, message.flags, message.data, message.length);
    }

    //iaap_video_process
    int process(int msg_type, int flags, byte[] buf, int len) {
        // Process video packet
        // MaxUnack

        int ret = mTransport.sendEncrypted(Channel.AA_CH_VID, vid_ack, vid_ack.length);      // Respond with ACK (for all fragments ?)

        if (flags == 11 && (msg_type == 0 || msg_type == 1) && (buf[10] == 0 && buf[11] == 0 && buf[12] == 0 && buf[13] == 1)) {  // If Not fragmented Video
            iaap_video_decode(buf, 10, len - 10);
            // Decode H264 video
        } else if (flags == 9 && (msg_type == 0 || msg_type == 1) && (buf[10] == 0 && buf[11] == 0 && buf[12] == 0 && buf[13] == 1)) {   // If First fragment Video
            System.arraycopy(buf, 10, assy, 0, len - 10);
            // Len in bytes 2,3 doesn't include total len 4 bytes at 4,5,6,7
            assy_size = len - 10;                                                                                                   // Add to re-assembly in progress
        }
        else if (flags == 11 && msg_type == 1 && (buf[2] == 0 && buf[3] == 0 && buf[4] == 0 && buf[5] == 1)) {                     // If Not fragmented First video config packet
            iaap_video_decode(buf, 2, len - 2);                                                                                 // Decode H264 video
        }
        else if (flags == 8) {                                                                                                     // If Middle fragment Video
            System.arraycopy(buf, 0, assy, assy_size, len);
            assy_size += len;                                                                                                       // Add to re-assembly in progress
        }
        else if (flags == 10) {                                                                                                    // If Last fragment Video
            System.arraycopy(buf, 0, assy, assy_size, len);
            assy_size += len;                                                                                                       // Add to re-assembly in progress
            iaap_video_decode(assy, 0, assy_size);
            // Decode H264 video fully re-assembled
        }
        else
            AppLog.loge("Video error msg_type: %d  flags: 0x%x  buf: %p  len: %d", msg_type, flags, buf, len);

        return 0;
    }

    private void iaap_video_decode(byte[] buf, int start, int len) {
        ByteArray ba = new ByteArray(len);
        ba.put(start, buf, len);

        mQueue.add(ba);
    }

}
