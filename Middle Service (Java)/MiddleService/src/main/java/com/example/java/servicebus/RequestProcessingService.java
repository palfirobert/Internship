package com.example.java.servicebus;

import com.azure.core.amqp.AmqpTransportType;
import com.azure.core.amqp.ProxyOptions;
import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusErrorContext;
import com.azure.messaging.servicebus.ServiceBusException;
import com.azure.messaging.servicebus.ServiceBusFailureReason;
import com.azure.messaging.servicebus.ServiceBusMessage;
import com.azure.messaging.servicebus.ServiceBusProcessorClient;
import com.azure.messaging.servicebus.ServiceBusReceivedMessage;
import com.azure.messaging.servicebus.ServiceBusReceivedMessageContext;
import com.azure.messaging.servicebus.ServiceBusSenderClient;
import com.example.java.domain.networking.AssignTenantsRequest;
import com.example.java.domain.networking.BatteryRequest;
import com.example.java.domain.networking.BatteryResponse;
import com.example.java.domain.networking.ImageRequest;
import com.example.java.domain.networking.ImageResponse;
import com.example.java.domain.networking.QueueType;
import com.example.java.domain.networking.Request;
import com.example.java.domain.networking.Response;
import com.example.java.domain.networking.TenantRequest;
import com.example.java.domain.networking.TenantResponse;
import com.example.java.dto.RequestDto;
import com.example.java.service.BatteryService;
import com.example.java.service.BlobService;
import com.example.java.service.PythonService;
import com.example.java.service.UserTenantsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.SerializationUtils;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public final class RequestProcessingService {
    /**
     * Integer that represents the size of the unique identifier for each request and response.
     */
    static final int IDSIZE = 30;
    /**
     * Counter that shows how many responses the thread will check until it decides to try and process another request.
     */
    static final int COUNTERFORRESPONSES = 20;
    /**
     * Connection string of the azure service bus.
     */
    @Value("${azure.servicebus.connection-string}")
    private String azureServiceBusConnectionString;
    /**
     * The queue for the battery requests.
     */
    @Value("${azure.servicebus.queue.battery.endpoint.queue}")
    private String azureBatteryQueue;
    /**
     * The queue for the plotting requests.
     */
    @Value("${azure.servicebus.queue.ploting.endpoint.queue}")
    private String azurePlottingQueue;
    /**
     * The queue for the tenants requests.
     */
    @Value("${azure.servicebus.queue.tenant.endpoint.queue}")
    private String azureTenantQueue;

    /**
     * Queue with the responses from each request.
     * The responses are put here after they are made,
     * and taken from here when needed.
     */
    private final ArrayBlockingQueue<Response> responseQueue;
    /**
     * Service responsable for the battery requests processing.
     */
    private final BatteryService batteryService;
    /**
     * Proxy options for the connection with the service bus.
     */
    private final ProxyOptions proxyOptions;
    /**
     * Service used to process requests for tenants.
     */
    private final UserTenantsService userTenantsService;
    /**
     * Service used to get images from blob container.
     */
    private final BlobService blobService;
    /**
     * Service used to request plots from the python server.
     */
    private final PythonService pythonService;
    /**
     * Connection string of the azure service bus.
     *//*
    @Value("${azure.servicebus.connection-string}")
    private String azureServiceBusConnectionString;
    *//**
     * The queue for the battery requests.
     *//*
    @Value("${azure.servicebus.queue.battery.endpoint.queue}")
    private String azureBatteryQueue;
    *//**
     * The queue for the plotting requests.
     *//*
    @Value("${azure.servicebus.queue.ploting.endpoint.queue}")
    private String azurePlottingQueue;
    *//**
     * The queue for the tenants requests.
     *//*
    @Value("${azure.servicebus.queue.tenant.endpoint.queue}")
    private String azureTenantQueue;*/

    /**
     * Constructor used to instantiate the RequestProcessingService.
     *
     * @param batteryServiceInstance     - service used to manage batteries.
     * @param proxyOptionsInstance       - options used to configure the proxy.
     * @param userTenantsServiceInstance - service used to manage tenants.
     * @param blobServiceInstance        - service used to access the blob storage.
     * @param pythonServiceInstance      - service used to make plots.
     * @param responseQueueInstance      - queue where the response will be put.
     */
    @Autowired
    public RequestProcessingService(final BatteryService batteryServiceInstance,
                                    final ProxyOptions proxyOptionsInstance,
                                    final UserTenantsService userTenantsServiceInstance,
                                    final BlobService blobServiceInstance,
                                    final PythonService pythonServiceInstance,
                                    final ArrayBlockingQueue<Response> responseQueueInstance) {
        this.batteryService = batteryServiceInstance;
        this.proxyOptions = proxyOptionsInstance;
        this.userTenantsService = userTenantsServiceInstance;
        this.blobService = blobServiceInstance;
        this.pythonService = pythonServiceInstance;
        this.responseQueue = responseQueueInstance;
    }

    /**
     * Function used to send messages to the azure service bus.
     *
     * @param serviceBusMessage - the message that you want to send.
     * @param queue             - the queue where it should be put.
     */
    public void sendMessage(final ServiceBusMessage serviceBusMessage, final String queue) {
        ServiceBusSenderClient serviceBusSenderClient =
                new ServiceBusClientBuilder()
                        .connectionString(azureServiceBusConnectionString)
                        .proxyOptions(proxyOptions)
                        .transportType(AmqpTransportType.AMQP_WEB_SOCKETS)
                        .sender()
                        .queueName(queue)
                        .buildClient();

        // send one message to the queue
        serviceBusSenderClient.sendMessage(serviceBusMessage);
        log.info("Sent a single message to the queue: " + queue);
    }

    /**
     * Function used to convert a request into a message that will be send to the service bus.
     *
     * @param request - the request that you want to convert.
     * @return - ServiceBusMessage.
     */
    public ServiceBusMessage createMessages(final Request request) {

        byte[] data = SerializationUtils.serialize(request);
        if (data == null) {
            log.error("Request was null !");
            return null;
        }
        // create a list of messages and return it to the caller
        return new ServiceBusMessage(data);
    }

    /**
     * Function that receives a message from the service bus and starts processing it.
     *
     * @param queue          - the queue from where you want to take the message.
     * @param countDownLatch - CountDownLatch that is used to wake
     *                       up the thread that waits for the message to be processed.
     * @throws InterruptedException if the thread is interrupted.
     */
    public void receiveMessages(final String queue, final CountDownLatch countDownLatch) throws InterruptedException {
        ServiceBusProcessorClient serviceBusProcessorClient =
                new ServiceBusClientBuilder()
                        .connectionString(azureServiceBusConnectionString)
                        .proxyOptions(proxyOptions)
                        .transportType(AmqpTransportType.AMQP_WEB_SOCKETS)
                        .processor()
                        .queueName(queue)
                        .processMessage(context -> this.processMessage(context, countDownLatch))
                        .processError(context -> processError(context, countDownLatch))
                        .buildProcessorClient();

        // Create an instance of the processor through the ServiceBusClientBuilder

        log.info("Starting the processor");
        serviceBusProcessorClient.start();

        log.info("Stopping and closing the processor");
        countDownLatch.await();
        serviceBusProcessorClient.close();
    }

    /**
     * Function that processes messages wen they are taken form the service bus.
     *
     * @param context        - something.
     * @param countDownLatch - CountDownLatch used to wake up the thread that waits for the message to be received.
     */
    public void processMessage(final ServiceBusReceivedMessageContext context, final CountDownLatch countDownLatch) {
        ServiceBusReceivedMessage message = context.getMessage();
        log.info("Processing message. Session: "
                + message.getMessageId() + " Sequence #: "
                + message.getSequenceNumber()
                + " Contents: "
                + message.getBody()
        );
        Request request = (Request) SerializationUtils.deserialize(context.getMessage().getBody().toBytes());
        if (request instanceof BatteryRequest) {
            this.processBatteryRequest((BatteryRequest) request, countDownLatch);
        } else if (request instanceof ImageRequest) {
            this.processPlotRequest((ImageRequest) request, countDownLatch);
        } else if (request instanceof TenantRequest) {
            this.processTenantRequest((TenantRequest) request, countDownLatch);
        } else if (request instanceof AssignTenantsRequest) {
            this.processAssignTenantsRequest((AssignTenantsRequest) request, countDownLatch);
        }


    }

    /**
     * Function used to process the errors that apear from the service bus operations.
     *
     * @param context        - something.
     * @param countDownLatch - CountDownLatch used to wake up the thread that waits for the message to be received.
     */
    public void processError(final ServiceBusErrorContext context, final CountDownLatch countDownLatch) {
        System.out.printf("Error when receiving messages from namespace: '%s'. Entity: '%s'%n",
                context.getFullyQualifiedNamespace(), context.getEntityPath());

        if (!(context.getException() instanceof ServiceBusException)) {
            log.info("Non-ServiceBusException occurred: %s%n", context.getException());
            return;
        }

        ServiceBusException exception = (ServiceBusException) context.getException();
        ServiceBusFailureReason reason = exception.getReason();

        if (reason == ServiceBusFailureReason.MESSAGING_ENTITY_DISABLED
                || reason == ServiceBusFailureReason.MESSAGING_ENTITY_NOT_FOUND
                || reason == ServiceBusFailureReason.UNAUTHORIZED) {
            log.info("An unrecoverable error occurred. Stopping processing with reason"
                    +
                    reason
                    + " "
                    + exception.getMessage());
        } else if (reason == ServiceBusFailureReason.MESSAGE_LOCK_LOST) {
            log.info("Message lock lost for message: %s%n", context.getException());
        } else if (reason == ServiceBusFailureReason.SERVICE_BUSY) {
            try {
                // Choosing an arbitrary amount of time to wait until trying again.
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                log.info("Unable to sleep for period of time");
            }
        } else {
            log.info("Error source " + context.getErrorSource()
                    + " , reason"
                    + reason
                    + " message:"
                    + context.getException()
            );
        }
        countDownLatch.countDown();
    }


    /**
     * Function used to process the incoming requests. It chooses which queue to used and creates a new thread that
     * will handle the processing while this thread waits for the response. This function triggers
     * the sending and receiving and also calls the function that gets the response from the response queue.
     *
     * @param requestDto - dto containing the request and the queue where we want to put it.
     * @return Response - a response to the request.
     */
    public synchronized Response processRequest(final RequestDto requestDto) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Request request = requestDto.getRequest();
        request.setRequestId(generateUniqueId());
        QueueType queueType = requestDto.getQueueType();
        final String queue;
        if (queueType.equals(QueueType.BATTERYQUEUE)) {
            queue = this.azureBatteryQueue;
        } else if (queueType.equals(QueueType.TENANTQUEUE)) {
            queue = this.azureTenantQueue;
        } else {
            queue = azurePlottingQueue;
        }
        //Here we create a new thread that will handle the processing of the request.
        new Thread(() -> {
            log.info("Sending request to service bus");
            this.sendMessage(this.createMessages(request), queue);
            try {
                log.info("Receiving request from service bus");
                this.receiveMessages(queue, countDownLatch);

            } catch (InterruptedException e) {
                e.printStackTrace();
                countDownLatch.countDown();
            }

        }).start();
        try {
            //The parent thread will wait until the processing is done.
            countDownLatch.await();
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        }
        log.info("Retrieving response from queue " + request.getRequestId());

        return getResponseFromQueue(request, queue);
    }

    /**
     * Function that process the battery requests and puts the response in the queue.
     *
     * @param request        - the request to be processed.
     * @param countDownLatch - CountDownLatch used to wake up the thread that waits for the message to be received.
     */
    private void processBatteryRequest(final BatteryRequest request, final CountDownLatch countDownLatch) {
        BatteryResponse response =
                new BatteryResponse(
                        this.batteryService.getTenantBatteries(request.getTenantId(),
                                request.getPage(),
                                request.getSize()));
        log.info("Adding battery response to queue for request with id " + request.getRequestId());
        response.setResponseId(request.getRequestId());
        this.responseQueue.add(response);
        countDownLatch.countDown();
    }

    /**
     * Function that processes a plotting request and puts the response in the response queue.
     *
     * @param request        - the request to be processed.
     * @param countDownLatch - CountDownLatch used to wake up the thread that waits for the message to be received.
     */
    private void processPlotRequest(final ImageRequest request, final CountDownLatch countDownLatch) {
        try {
            ImageResponse response =
                    new ImageResponse(
                            blobService.getImagesFromLocations(
                                    pythonService.getPLotLocations(
                                            request.getRequest()).getLocations()));
            response.setResponseId(request.getRequestId());
            this.responseQueue.add(response);
            countDownLatch.countDown();
            log.info("Adding image response to queue for request with id " + request.getRequestId());
        } catch (Exception e) {
            log.error(e.getMessage());
            countDownLatch.countDown();
        }

    }

    /**
     * Function used to process tenant requests and put the response in the response queue.
     *
     * @param request        - request to be processed.
     * @param countDownLatch - CountDownLatch used to wake up the thread that waits for the message to be received.
     */
    private void processTenantRequest(final TenantRequest request, final CountDownLatch countDownLatch) {
        TenantResponse response =
                new TenantResponse(
                        this.userTenantsService.getAllTenants(
                                request.getPageNr(),
                                request.getPageSize(),
                                request.getUsername()));
        log.info("Adding to queue tenant response for the request with id " + request.getRequestId());
        response.setResponseId(request.getRequestId());
        this.responseQueue.add(response);
        countDownLatch.countDown();
    }

    /**
     * Function used to process assign tenants requests and put the response in the response queue.
     *
     * @param request        - request to be processed.
     * @param countDownLatch - CountDownLatch used to wake up the thread that waits for the message to be received.
     */
    private void processAssignTenantsRequest(final AssignTenantsRequest request, final CountDownLatch countDownLatch) {
        Response response = new Response();
        this.userTenantsService.assignTenants(request.getEmail(), request.getTenantsList());
        response.setResponseId(request.getRequestId());
        this.responseQueue.add(response);
        countDownLatch.countDown();
    }

    /**
     * Function that gets the response from the response queue.
     * It takes a response and checks if the response id is the same as the request id, if so it returns the response.
     * If the response if not the response associated with the current request,, it tryes 10 times to get another
     * response from the queue. If it dose not find its response, it triggers the function that takes a request
     * from the service bus and waits untill the new request is processed than tries again to find its response form the
     * original request. For sure it will find its response because its request is either processed by another thread
     * at the time it tries to get its response or the request is not yet processed and will be processed soon.
     *
     * @param request - the request that we trie to find a response to.
     * @param queue   - the queue from where to get the new request in case it needs to process another request.
     * @return Response - a response to its request.
     */
    private Response getResponseFromQueue(final Request request, final String queue) {
        Response response = this.responseQueue.poll();
        log.info("getting response for request with id " + request.getRequestId());
        int counter = COUNTERFORRESPONSES;
        while (response != null && !response.getResponseId().equals(request.getRequestId()) && counter > 0) {
            log.info("checking response with id " + response.getResponseId());
            this.responseQueue.add(response);
            response = this.responseQueue.poll();
            counter--;
        }
        if (counter == 0) {
            this.responseQueue.add(response);
            try {
                CountDownLatch countDownLatch = new CountDownLatch(1);
                receiveMessages(queue, countDownLatch);
                countDownLatch.await();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        log.info("Returning response for the request with id" + request.getRequestId());
        return response;
    }

    /**
     * Function used to generate a random string that will be the unique identifier of the request
     * and its associated response .
     *
     * @return String containing the generated string.
     */
    private String generateUniqueId() {
        return RandomStringUtils.random(IDSIZE, true, false);
    }

}
