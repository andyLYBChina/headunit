// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: common.proto

package info.anodsplace.headunit.aap.protocol.proto;

public final class Common {
  private Common() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  /**
   * Protobuf enum {@code info.anodsplace.headunit.aap.protocol.proto.MessageStatus}
   */
  public enum MessageStatus
      implements com.google.protobuf.ProtocolMessageEnum {
    /**
     * <code>STATUS_OK = 0;</code>
     */
    STATUS_OK(0),
    ;

    /**
     * <code>STATUS_OK = 0;</code>
     */
    public static final int STATUS_OK_VALUE = 0;


    public final int getNumber() {
      return value;
    }

    /**
     * @deprecated Use {@link #forNumber(int)} instead.
     */
    @java.lang.Deprecated
    public static MessageStatus valueOf(int value) {
      return forNumber(value);
    }

    public static MessageStatus forNumber(int value) {
      switch (value) {
        case 0: return STATUS_OK;
        default: return null;
      }
    }

    public static com.google.protobuf.Internal.EnumLiteMap<MessageStatus>
        internalGetValueMap() {
      return internalValueMap;
    }
    private static final com.google.protobuf.Internal.EnumLiteMap<
        MessageStatus> internalValueMap =
          new com.google.protobuf.Internal.EnumLiteMap<MessageStatus>() {
            public MessageStatus findValueByNumber(int number) {
              return MessageStatus.forNumber(number);
            }
          };

    public final com.google.protobuf.Descriptors.EnumValueDescriptor
        getValueDescriptor() {
      return getDescriptor().getValues().get(ordinal());
    }
    public final com.google.protobuf.Descriptors.EnumDescriptor
        getDescriptorForType() {
      return getDescriptor();
    }
    public static final com.google.protobuf.Descriptors.EnumDescriptor
        getDescriptor() {
      return info.anodsplace.headunit.aap.protocol.proto.Common.getDescriptor().getEnumTypes().get(0);
    }

    private static final MessageStatus[] VALUES = values();

    public static MessageStatus valueOf(
        com.google.protobuf.Descriptors.EnumValueDescriptor desc) {
      if (desc.getType() != getDescriptor()) {
        throw new java.lang.IllegalArgumentException(
          "EnumValueDescriptor is not for this type.");
      }
      return VALUES[desc.getIndex()];
    }

    private final int value;

    private MessageStatus(int value) {
      this.value = value;
    }

    // @@protoc_insertion_point(enum_scope:info.anodsplace.headunit.aap.protocol.proto.MessageStatus)
  }


  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\014common.proto\022+info.anodsplace.headunit" +
      ".aap.protocol.proto*\036\n\rMessageStatus\022\r\n\t" +
      "STATUS_OK\020\000"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
