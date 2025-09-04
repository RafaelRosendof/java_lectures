import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 * <pre>
 * Definição do serviço
 * </pre>
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.58.0)",
    comments = "Source: service.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class ComponentServiceGrpc {

  private ComponentServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "ComponentService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<ServiceOuterClass.RequestMessage,
      ServiceOuterClass.ResponseMessage> getProcessRequestMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ProcessRequest",
      requestType = ServiceOuterClass.RequestMessage.class,
      responseType = ServiceOuterClass.ResponseMessage.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<ServiceOuterClass.RequestMessage,
      ServiceOuterClass.ResponseMessage> getProcessRequestMethod() {
    io.grpc.MethodDescriptor<ServiceOuterClass.RequestMessage, ServiceOuterClass.ResponseMessage> getProcessRequestMethod;
    if ((getProcessRequestMethod = ComponentServiceGrpc.getProcessRequestMethod) == null) {
      synchronized (ComponentServiceGrpc.class) {
        if ((getProcessRequestMethod = ComponentServiceGrpc.getProcessRequestMethod) == null) {
          ComponentServiceGrpc.getProcessRequestMethod = getProcessRequestMethod =
              io.grpc.MethodDescriptor.<ServiceOuterClass.RequestMessage, ServiceOuterClass.ResponseMessage>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ProcessRequest"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ServiceOuterClass.RequestMessage.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ServiceOuterClass.ResponseMessage.getDefaultInstance()))
              .setSchemaDescriptor(new ComponentServiceMethodDescriptorSupplier("ProcessRequest"))
              .build();
        }
      }
    }
    return getProcessRequestMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static ComponentServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<ComponentServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<ComponentServiceStub>() {
        @java.lang.Override
        public ComponentServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new ComponentServiceStub(channel, callOptions);
        }
      };
    return ComponentServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static ComponentServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<ComponentServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<ComponentServiceBlockingStub>() {
        @java.lang.Override
        public ComponentServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new ComponentServiceBlockingStub(channel, callOptions);
        }
      };
    return ComponentServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static ComponentServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<ComponentServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<ComponentServiceFutureStub>() {
        @java.lang.Override
        public ComponentServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new ComponentServiceFutureStub(channel, callOptions);
        }
      };
    return ComponentServiceFutureStub.newStub(factory, channel);
  }

  /**
   * <pre>
   * Definição do serviço
   * </pre>
   */
  public interface AsyncService {

    /**
     */
    default void processRequest(ServiceOuterClass.RequestMessage request,
        io.grpc.stub.StreamObserver<ServiceOuterClass.ResponseMessage> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getProcessRequestMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service ComponentService.
   * <pre>
   * Definição do serviço
   * </pre>
   */
  public static abstract class ComponentServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return ComponentServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service ComponentService.
   * <pre>
   * Definição do serviço
   * </pre>
   */
  public static final class ComponentServiceStub
      extends io.grpc.stub.AbstractAsyncStub<ComponentServiceStub> {
    private ComponentServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ComponentServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new ComponentServiceStub(channel, callOptions);
    }

    /**
     */
    public void processRequest(ServiceOuterClass.RequestMessage request,
        io.grpc.stub.StreamObserver<ServiceOuterClass.ResponseMessage> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getProcessRequestMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service ComponentService.
   * <pre>
   * Definição do serviço
   * </pre>
   */
  public static final class ComponentServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<ComponentServiceBlockingStub> {
    private ComponentServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ComponentServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new ComponentServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public ServiceOuterClass.ResponseMessage processRequest(ServiceOuterClass.RequestMessage request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getProcessRequestMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service ComponentService.
   * <pre>
   * Definição do serviço
   * </pre>
   */
  public static final class ComponentServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<ComponentServiceFutureStub> {
    private ComponentServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ComponentServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new ComponentServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<ServiceOuterClass.ResponseMessage> processRequest(
        ServiceOuterClass.RequestMessage request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getProcessRequestMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_PROCESS_REQUEST = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AsyncService serviceImpl;
    private final int methodId;

    MethodHandlers(AsyncService serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_PROCESS_REQUEST:
          serviceImpl.processRequest((ServiceOuterClass.RequestMessage) request,
              (io.grpc.stub.StreamObserver<ServiceOuterClass.ResponseMessage>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
          getProcessRequestMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              ServiceOuterClass.RequestMessage,
              ServiceOuterClass.ResponseMessage>(
                service, METHODID_PROCESS_REQUEST)))
        .build();
  }

  private static abstract class ComponentServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    ComponentServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return ServiceOuterClass.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("ComponentService");
    }
  }

  private static final class ComponentServiceFileDescriptorSupplier
      extends ComponentServiceBaseDescriptorSupplier {
    ComponentServiceFileDescriptorSupplier() {}
  }

  private static final class ComponentServiceMethodDescriptorSupplier
      extends ComponentServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    ComponentServiceMethodDescriptorSupplier(java.lang.String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (ComponentServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new ComponentServiceFileDescriptorSupplier())
              .addMethod(getProcessRequestMethod())
              .build();
        }
      }
    }
    return result;
  }
}
