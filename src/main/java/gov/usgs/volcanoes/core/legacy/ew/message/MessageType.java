package gov.usgs.volcanoes.core.legacy.ew.message;

/**
 * An enum listing Earthworm message types.
 * 
 * @author Tom Parker
 */

public enum MessageType {

  TYPE_WILDCARD(0), // wildcard value - DO NOT CHANGE!!!
  TYPE_ADBUF(1), // multiplexed waveforms from DOS adsend
  TYPE_ERROR(2), // error
  TYPE_HEARTBEAT(3), // heartbeat
  TYPE_NANOBUF(4), // single-channel waveforms from nanometrics
  TYPE_ACK(6), // used by import_ack
  TYPE_PICK2K(10), // P-wave arrival time (with 4 digit year)
  TYPE_CODA2K(11), // coda info (plus station code) from pick_ew
  // TYPE_PICK2(12), // P-wave arrival time from picker & pick_ew
  // TYPE_CODA2(13), // coda info from picker & pick_ew
  TYPE_HYP2000ARC(14), // hyp2000 (Y2K hypoinverse) event archive
  TYPE_H71SUM2K(15), // hypo71-format hypocenter summary msg (with 4-digit year) from
                     // eqproc/eqprelim
  // TYPE_HINVARC(17), // hypoinverse event archive msg from eqproc/eqprelim
  // TYPE_H71SUM(18), // hypo71-format summary msg from eqproc/eqprelim
  TYPE_TRACEBUF2(19), // single-channel waveforms from NT adsend, getdst2, nano2trace, rcv_ew,
                      // import_ida...
  TYPE_TRACEBUF(20), // single-channel waveforms from NT adsend, getdst2, nano2trace, rcv_ew,
                     // import_ida...
  TYPE_LPTRIG(21), // single-channel long-period trigger from lptrig & evanstrig
  TYPE_CUBIC(22), // cubic-format summary msg from cubic_msg
  TYPE_CARLSTATRIG(23), // single-channel trigger from carlstatrig
  // TYPE_TRIGLIST(24), // trigger-list msg (used by tracesave modules) from arc2trig, trg_assoc,
  // carlsubtrig
  TYPE_TRIGLIST2K(25), // trigger-list msg (with 4-digit year) used by tracesave modules from
                       // arc2trig, trg_assoc, carlsubtrig
  TYPE_TRACE_COMP_UA(26), // compressed waveforms from compress_UA
  TYPE_STRONGMOTION(27), // single-instrument peak accel, peak velocity, peak displacement, spectral
                         // acceleration
  TYPE_MAGNITUDE(28), // event magnitude: summary plus station info
  TYPE_STRONGMOTIONII(29);

  private int type;

  private MessageType(int t) {
    type = t;
  }

  public static MessageType fromInt(int i) {
    for (MessageType m : MessageType.values())
      if (m.getType() == i)
        return m;

    return null;
  }

  public int getType() {
    return type;
  }
}
